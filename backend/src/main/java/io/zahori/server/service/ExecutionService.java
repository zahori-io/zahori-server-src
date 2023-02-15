package io.zahori.server.service;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 PANEL SISTEMAS INFORMATICOS,S.L
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.zahori.server.model.CaseExecution;
import io.zahori.server.model.ClientTestRepo;
import io.zahori.server.model.Configuration;
import io.zahori.server.model.EvidenceType;
import io.zahori.server.model.Execution;
import io.zahori.server.model.Process;
import io.zahori.server.repository.CaseExecutionsRepository;
import io.zahori.server.repository.ConfigurationRepository;
import io.zahori.server.repository.ExecutionsRepository;
import io.zahori.server.repository.ProcessesRepository;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * The type Execution service.
 */
@Service
public class ExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionService.class);

    private static final String PASSED = "Passed";
    private static final String FAILED = "Failed";
    private static final String PENDING = "Pending";
    private static final String CREATED = "Created";
    private static final String RUNNING = "Running";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private ExecutionsRepository executionsRepository;
    private ProcessesRepository processesRepository;
    private ServiceRegistry serviceRegistry;
    private SelenoidService selenoidService;
    private CaseExecutionsRepository caseExecutionsRepository;
    private ConfigurationRepository configurationRepository;

    @Autowired
    public ExecutionService(ExecutionsRepository executionsRepository, ProcessesRepository processesRepository, ServiceRegistry serviceRegistry,
            SelenoidService selenoidService, CaseExecutionsRepository caseExecutionsRepository, ConfigurationRepository configurationRepository) {
        this.executionsRepository = executionsRepository;
        this.processesRepository = processesRepository;
        this.serviceRegistry = serviceRegistry;
        this.selenoidService = selenoidService;
        this.caseExecutionsRepository = caseExecutionsRepository;
        this.configurationRepository = configurationRepository;
    }

    public Iterable<Execution> getExecutions(Long clientId, Long processId) {
        return executionsRepository.findByClientIdAndProcessId(clientId, processId);
    }

    public Page<Execution> getExecutionsPageable(Long clientId, Long processId, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("executionId").descending());
        return executionsRepository.findByClientIdAndProcessId(clientId, processId, pageRequest);
    }

    public Optional<Execution> getExecutionById(Long executionId) {
        return executionsRepository.findById(executionId);
    }

    public Execution create(Execution execution) throws Exception {

        /* Get Process from database */
        Optional<Process> processOpt = processesRepository.findById(execution.getProcess().getProcessId());
        if (!processOpt.isPresent()) {
            throw new RuntimeException("El proceso no existe en la base de datos");
        }
        Process process = processOpt.get();

        /* Verify if process is running */
        String processUrl = serviceRegistry.getProcessUrl(process);
        String processNotReadyError = "El proceso parece estar offline! Si el proceso se inició recientemente vuelve a intentarlo pasados unos segundos, sino revisa que el proceso esté arrancado.";

        if (StringUtils.isBlank(processUrl)) {
            // TODO retry???
            throw new RuntimeException(processNotReadyError);
        }

        if (!serviceRegistry.isProcessRunning(processUrl)) {
            throw new RuntimeException(processNotReadyError);
        }

        /* Set execution details */
        execution.setTotalFailed(0);
        execution.setTotalPassed(0);
        execution.setStatus(CREATED);
        execution.setDate(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        for (CaseExecution caseExecution : execution.getCasesExecutions()) {
            caseExecution.setStatus(PENDING);
        }

        // Create execution in database
        execution = executionsRepository.save(execution);

        // Call process
        runProcess(processUrl, process, execution);

        // Delegate to SelenoidService watch for each case execution in Selenoid
        if (process.getProcessType() != null && "BROWSER".equalsIgnoreCase(process.getProcessType().getName())) {
            selenoidService.watchStatus(execution);
        }

        return execution;
    }

    private void runProcess(String processUrl, Process process, Execution execution) {

        // Enrich caseExecution
        io.zahori.model.process.Configuration execConfig = getExecutionConfiguration(execution);
        for (CaseExecution caseExecution : execution.getCasesExecutions()) {
            // Configuration
            caseExecution.setConfiguration(execConfig);
            // TMS
            Map<String, String> caseData = caseExecution.getCas().getDataMap();
            caseData.put("TMS_TE_ID", execution.getTmsTestExecutionId());
            caseExecution.getCas().setData(caseData);
        }

        List<CaseExecution> casesExecuted = new ArrayList<>();
        long startExecutionTime = System.currentTimeMillis();

        // Run process
        Flux.fromIterable(execution.getCasesExecutions()).parallel().runOn(Schedulers.boundedElastic()) //
                .flatMap( //
                        caseExecution -> //
                        WebClient.create().post().uri(processUrl + "/run").bodyValue(caseExecution).retrieve().bodyToMono(CaseExecution.class) //
                                .doOnRequest(req -> {
                                    String processCase = process.getName() + "' -> case '" + caseExecution.getCas().getName() + "' ("
                                            + caseExecution.getBrowser().getBrowserName() + ")";
                                    LOG.info("[-->] Start call to process '{}'", processCase);
                                    caseExecution.setStatus(RUNNING);
                                    caseExecutionsRepository.save(caseExecution);
                                }) //
                                .doOnError(err -> {
                                    String processCase = process.getName() + "' -> case '" + caseExecution.getCas().getName() + "' ("
                                            + caseExecution.getBrowser().getBrowserName() + ")";
                                    LOG.info("[<--] Process error '{}'", processCase);

                                    casesExecuted.add(caseExecution);
                                    execution.setTotalFailed(execution.getTotalFailed() + 1);
                                    caseExecution.setStatus(FAILED);
                                    caseExecution.setNotes(err.getMessage());
                                    caseExecutionsRepository.save(caseExecution);
                                }) //
                                .doOnSuccess(resultCaseExecution -> {
                                    String processCase = process.getName() + "' -> case '" + caseExecution.getCas().getName() + "' ("
                                            + caseExecution.getBrowser().getBrowserName() + ")";
                                    LOG.info("[<--] Process finished '{}'", processCase);
                                    CaseExecution savedCaseExecution = updateCaseExecution(resultCaseExecution);
                                    updateTotals(execution, savedCaseExecution);
                                    casesExecuted.add(savedCaseExecution);
                                }) //
                ) //
                .sequential() //
                .doFirst(() -> {
                    LOG.info("[...] Start calls to process -> '{}'", process.getName());
                    execution.setStatus(RUNNING);
                    executionsRepository.save(execution);
                }) //
                .onErrorContinue( //
                        (e, v) -> {
                        }) //
                .doOnComplete( //
                        () -> {
                            LOG.info("[<--] All requests completed");

                            // Status
                            if (execution.getTotalFailed() > 0) {
                                execution.setStatus(FAILURE);
                            } else {
                                execution.setStatus(SUCCESS);
                            }

                            // Cases executed
                            execution.setCasesExecutions(casesExecuted);

                            // Duration
                            long testDurationLong = System.currentTimeMillis() - startExecutionTime;
                            int executionDuration = (Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(testDurationLong))).intValue();
                            execution.setDurationSeconds(executionDuration);

                            executionsRepository.save(execution);
                        })//
                .subscribe();
    }

    private io.zahori.model.process.Configuration getExecutionConfiguration(Execution execution) {
        Long configurationId = execution.getConfiguration().getConfigurationId();
        Optional<Configuration> configOpt = configurationRepository.findById(configurationId);
        Configuration configuration = configOpt.get();

        // Create configuration DTO for the process
        io.zahori.model.process.Configuration execConfiguration = new io.zahori.model.process.Configuration();
        execConfiguration.setName(configuration.getName());
        execConfiguration.setEnvironmentName(configuration.getClientEnvironment().getName());
        execConfiguration.setEnvironmentUrl(configuration.getClientEnvironment().getUrl());
        execConfiguration.setRetries(configuration.getRetry().getRetryId());
        execConfiguration.setTimeout(configuration.getTimeout().getTimeoutId());

        execConfiguration.setGenerateEvidences(configuration.getEvidenceCase().getName());
        for (EvidenceType evidenceType : configuration.getEvidenceTypes()) {
            execConfiguration.getGenerateEvidencesTypes().add(evidenceType.getName());
        }

        execConfiguration.setUploadResults(configuration.getUploadResults());
        if (execConfiguration.isUploadResults()) {
            execConfiguration.setUploadRepositoryName(configuration.getTestRepository().getName());
            if (configuration.getTestRepository().getClientTestRepos() != null && !configuration.getTestRepository().getClientTestRepos().isEmpty()) {
                Iterator<ClientTestRepo> iterator = configuration.getTestRepository().getClientTestRepos().iterator();
                ClientTestRepo firstClientTestRepo = iterator.next();

                execConfiguration.setUploadRepositoryUrl(firstClientTestRepo.getUrl());
                execConfiguration.setUploadRepositoryUser(firstClientTestRepo.getUser());
                execConfiguration.setUploadRepositoryPass(firstClientTestRepo.getPassword());
            }
        }

        return execConfiguration;

    }

    private CaseExecution updateCaseExecution(CaseExecution resultCaseExecution) {
        Optional<CaseExecution> caseExecutionDBOpt = caseExecutionsRepository.findById(resultCaseExecution.getCaseExecutionId());
        if (caseExecutionDBOpt.isPresent()) {
            CaseExecution caseExecutionDB = caseExecutionDBOpt.get();
            resultCaseExecution.setSelenoidId(caseExecutionDB.getSelenoidId());
            resultCaseExecution.setBrowserVersion(caseExecutionDB.getBrowserVersion());
        }
        return caseExecutionsRepository.save(resultCaseExecution);
    }

    private void updateTotals(Execution execution, CaseExecution caseExecution) {
        if (PASSED.equalsIgnoreCase(caseExecution.getStatus())) {
            execution.setTotalPassed(execution.getTotalPassed() + 1);
        } else {
            execution.setTotalFailed(execution.getTotalFailed() + 1);
        }
    }

}
