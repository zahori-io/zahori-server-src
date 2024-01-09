package io.zahori.server.service;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 - 2023 PANEL SISTEMAS INFORMATICOS,S.L
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
import io.zahori.model.process.Tms;
import io.zahori.server.model.CaseExecution;
import io.zahori.server.model.ClientTestRepo;
import io.zahori.server.model.Configuration;
import io.zahori.server.model.EvidenceType;
import io.zahori.server.model.Execution;
import io.zahori.server.model.PeriodicExecution;
import io.zahori.server.model.Process;
import io.zahori.server.model.Task;
import io.zahori.server.notifications.NotificationsService;
import io.zahori.server.repository.CaseExecutionsRepository;
import io.zahori.server.repository.ClientTestRepository;
import io.zahori.server.repository.ConfigurationRepository;
import io.zahori.server.repository.ExecutionsRepository;
import io.zahori.server.repository.ProcessesRepository;
import io.zahori.server.security.JWTUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * The type Execution service.
 */
@Service
public class ExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionService.class);

    public static final String PASSED = "PASSED";
    public static final String FAILED = "FAILED";
    public static final String PENDING = "Pending";
    public static final String CREATED = "Created";
    public static final String SCHEDULED = "Scheduled";
    public static final String RUNNING = "Running";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String TRIGGER_MANUAL = "Manual";
    public static final String TRIGGER_SCHEDULER = "Scheduler";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private ExecutionsRepository executionsRepository;
    private ProcessesRepository processesRepository;
    private ServiceRegistry serviceRegistry;
    private SelenoidService selenoidService;
    private CaseExecutionsRepository caseExecutionsRepository;
    private ConfigurationRepository configurationRepository;
    private ClientTestRepository clientTestRepository;
    private ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancer;
    private SchedulerService schedulerService;
    private NotificationsService notificationsService;

    @Autowired
    public ExecutionService(ExecutionsRepository executionsRepository, ProcessesRepository processesRepository, ServiceRegistry serviceRegistry,
            SelenoidService selenoidService, CaseExecutionsRepository caseExecutionsRepository, ConfigurationRepository configurationRepository, ClientTestRepository clientTestRepository,
            ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancer, SchedulerService schedulerService, NotificationsService notificationsService) {
        this.executionsRepository = executionsRepository;
        this.processesRepository = processesRepository;
        this.serviceRegistry = serviceRegistry;
        this.selenoidService = selenoidService;
        this.caseExecutionsRepository = caseExecutionsRepository;
        this.configurationRepository = configurationRepository;
        this.clientTestRepository = clientTestRepository;
        this.reactorLoadBalancer = reactorLoadBalancer;
        this.schedulerService = schedulerService;
        this.notificationsService = notificationsService;
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

    public Execution runManualExecution(Execution execution) {
        execution.setTrigger(TRIGGER_MANUAL);
        return create(execution);
    }

    public Execution runPeriodicExecution(UUID uuid) {
        Execution executionFromDB = executionsRepository.findByPeriodicExecutionUuid(uuid);
        if (executionFromDB == null) {
            throw new RuntimeException("Periodic execution not found");
        }

        Execution newExecution = new Execution();
        BeanUtils.copyProperties(executionFromDB, newExecution);

        newExecution.setExecutionId(null);
        newExecution.setDate("");
        newExecution.setTrigger(TRIGGER_SCHEDULER);

        newExecution.setCasesExecutions(new ArrayList<>());
        for (CaseExecution caseExecutionFromDB : executionFromDB.getCasesExecutions()) {
            CaseExecution newCaseExecution = new CaseExecution();
            BeanUtils.copyProperties(caseExecutionFromDB, newCaseExecution);
            newCaseExecution.setCaseExecutionId(null);
            newCaseExecution.setAttachments(null);
            newExecution.getCasesExecutions().add(newCaseExecution);
        }

        Long processId = executionsRepository.getProcessIdByExecutionId(executionFromDB.getExecutionId());
        Process process = new Process();
        process.setProcessId(processId);
        newExecution.setProcess(process);

        return create(newExecution);
    }

    @Transactional
    public Execution createPeriodicExecution(Execution execution) {
        schedulerService.validateSchedulerIsUp();

        List<Task> tasks = new ArrayList<>();
        for (PeriodicExecution periodicExecution : execution.getPeriodicExecutions()) {
            UUID uuid = UUID.randomUUID();
            periodicExecution.setUuid(uuid);
            Task task = new Task(periodicExecution.getUuid(), periodicExecution.getCronExpression());
            tasks.add(task);
        }

        execution.setTotalFailed(0);
        execution.setTotalPassed(0);
        execution.setStatus(SCHEDULED);
        execution.setDate(getTimestamp());
        for (CaseExecution caseExecution : execution.getCasesExecutions()) {
            caseExecution.setStatus(SCHEDULED);
        }

        Execution savedExecution = executionsRepository.save(execution);

        for (Task task : tasks) {
            schedulerService.create(task);
        }

        return savedExecution;
    }

    private Process getProcessFromDB(Long processId) {
        Optional<Process> processOpt = processesRepository.findById(processId);
        if (!processOpt.isPresent()) {
            throw new RuntimeException("Process not found");
        }
        return processOpt.get();
    }

    public Execution create(Execution execution) {
        Process process = getProcessFromDB(execution.getProcess().getProcessId());

        /* Verify if process is registered in the serviceRegistry */
        String serviceId = serviceRegistry.getServiceId(process);
        if (!serviceRegistry.isServiceRegistered(serviceId)) {

            if (isPeriodicExecution(execution)) {
                execution.setDurationSeconds(0);
                execution.setDate(getTimestamp());
                for (CaseExecution caseExecution : execution.getCasesExecutions()) {
                    caseExecution.setDate(getTimestamp());
                    caseExecution.setDateTimestamp(System.currentTimeMillis());
                    caseExecution.setDurationSeconds(0);
                }
                return updateExecutionInDB(execution, FAILURE, FAILED, "Process is offline");
            }

            // TODO: i18n
            String processNotReadyError = "El proceso parece estar offline! Si el proceso se inició recientemente vuelve a intentarlo pasados unos segundos, sino revisa que el proceso esté arrancado.";
            throw new RuntimeException(processNotReadyError);
        }

        // TODO: Verify if process is running: the process is stopped or there is no connectivity
        // Update execution in DB
        updateExecutionInDB(execution, CREATED, PENDING);

        // Call process
        runProcess(serviceId, process, execution);

        // Delegate to SelenoidService watch for each case execution in Selenoid
        if (process.getProcessType() != null && "BROWSER".equalsIgnoreCase(process.getProcessType().getName())) {
            selenoidService.watchStatus(execution);
        }

        return execution;
    }

    private Execution updateExecutionInDB(Execution execution, String executionStatus, String caseStatus) {
        return updateExecutionInDB(execution, executionStatus, caseStatus, "");
    }

    private Execution updateExecutionInDB(Execution execution, String executionStatus, String caseStatus, String caseNotes) {
        /* Set execution details */
        execution.setTotalFailed(0);
        execution.setTotalPassed(0);
        execution.setStatus(executionStatus);
        execution.setDate(getTimestamp());
        execution.setPeriodicExecutions(null);
        if (StringUtils.isBlank(execution.getName())) {
            execution.setName(getTimestamp());
        }

        for (CaseExecution caseExecution : execution.getCasesExecutions()) {
            caseExecution.setStatus(caseStatus);
            caseExecution.setNotes(caseNotes);
        }

        // Create execution in database
        return executionsRepository.save(execution);
    }

    private void runProcess(String serviceId, Process process, Execution execution) {

        // Enrich caseExecutions with configuration and test repository details
        setCaseExecutionsConfiguration(execution);

        List<CaseExecution> casesExecuted = new ArrayList<>();
        long startExecutionTime = System.currentTimeMillis();

        Flux.fromIterable(execution.getCasesExecutions()).parallel().runOn(Schedulers.boundedElastic())
                .flatMap(caseExecution -> {
                    return WebClient.builder().baseUrl("http://" + serviceId).filter(reactorLoadBalancer).build().post().uri("/run").bodyValue(caseExecution)
                            .exchangeToMono(response -> {
                                if (response.statusCode().is2xxSuccessful()) {
                                    return response.bodyToMono(CaseExecution.class)
                                            .doOnSuccess(resultCaseExecution -> {
                                                // Código en caso de éxito
                                                String processCase = process.getName() + "' -> case '" + caseExecution.getCas().getName() + "' ("
                                                        + caseExecution.getBrowser().getBrowserName() + ")";
                                                LOG.info("[<--] Process finished '{}'", processCase);
                                                CaseExecution savedCaseExecution = updateCaseExecution(resultCaseExecution);
                                                updateTotals(execution, savedCaseExecution);
                                                casesExecuted.add(savedCaseExecution);
                                            });
                                } else {
                                    // Error
                                    String processCase = process.getName() + "' -> case '" + caseExecution.getCas().getName() + "' ("
                                            + caseExecution.getBrowser().getBrowserName() + ")";
                                    LOG.info("[<--] Process error '{}'", processCase);
                                    casesExecuted.add(caseExecution);
                                    execution.setTotalFailed(execution.getTotalFailed() + 1);
                                    caseExecution.setStatus(FAILED);
                                    return response.bodyToMono(String.class)
                                            .doOnSuccess(errorBody -> {
                                                caseExecution.setNotes(errorBody);
                                                caseExecutionsRepository.save(caseExecution);
                                            })
                                            .then(Mono.error(new RuntimeException("Error en la respuesta HTTP"))); // Lanzar una excepción
                                }
                            })
                            .doOnRequest(req -> {
                                String processCase = process.getName() + "' -> case '" + caseExecution.getCas().getName() + "' ("
                                        + caseExecution.getBrowser().getBrowserName() + ")";
                                LOG.info("[-->] Start call to process '{}'", processCase);
                                caseExecution.setStatus(RUNNING);
                                caseExecutionsRepository.save(caseExecution);
                            });
                })
                .sequential()
                .doFirst(() -> {
                    LOG.info("[...] Start calls to process -> '{}'", process.getName());
                    execution.setStatus(RUNNING);
                    executionsRepository.save(execution);
                })
                .onErrorContinue((e, v) -> {
                    LOG.error("### Error running execution {}: {}", execution.getExecutionId(), e.getMessage());
                    e.printStackTrace();
                })
                .doOnComplete(() -> {
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
                    notificationsService.sendExecutionNotification(process, execution);
                })
                .subscribe();
    }

    private void setCaseExecutionsConfiguration(Execution execution) {

        Long configurationId = execution.getConfiguration().getConfigurationId();
        Long clientId = JWTUtils.getClientId(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());

        Configuration configuration = configurationRepository.findByIdAndClientId(configurationId, clientId);
        if (configuration == null) {
            throw new RuntimeException("Invalid configuration id");
        }

        ClientTestRepo clientTestRepo = null;
        if (configuration.getTestRepository() != null && configuration.getTestRepository().getTestRepoId() > 0) {
            clientTestRepo = clientTestRepository.findByClientIdAndTestRepoId(clientId, configuration.getTestRepository().getTestRepoId());
        }

        for (CaseExecution caseExecution : execution.getCasesExecutions()) {

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

            Map<String, String> caseData = caseExecution.getCas().getDataMap();
            //caseData.put("CUSTOM_FIELD", "value");
            //caseExecution.getCas().setData(caseData);

            // TMS
            if (clientTestRepo != null) {
                Tms tms = new Tms();
                tms.setUploadResults(clientTestRepo.getTestRepository().getActive() && configuration.getUploadResults());

                if (tms.isUploadResults()) {
                    tms.setName(clientTestRepo.getTestRepository().getName());
                    tms.setUrl(clientTestRepo.getUrl());
                    tms.setUser(clientTestRepo.getUser());
                    tms.setPassword(clientTestRepo.getPassword());
                    tms.setTestExecutionId(execution.getTmsTestExecutionId());
                    tms.setTestExecutionSummary(execution.getTmsTestExecutionSummary());
                    tms.setTestPlanId(execution.getTmsTestPlanId());
                    tms.setTestCaseId(caseData.get("TMS_TC_ID"));
                }
                execConfiguration.setTms(tms);
            }

            // Configuration
            caseExecution.setConfiguration(execConfiguration);

            // Execution Id
            caseExecution.setExecutionId(execution.getExecutionId());
            // Execution total cases
            caseExecution.setExecutionTotalCases(execution.getCasesExecutions().size());
        }

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

    private String getTimestamp() {
        return new SimpleDateFormat(DATE_FORMAT).format(new Date());
    }

    public boolean isPeriodicExecution(Execution execution) {
        return execution.getPeriodicExecutions() != null && !execution.getPeriodicExecutions().isEmpty();
    }
}
