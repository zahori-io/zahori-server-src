package io.zahori.server.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.zahori.model.process.Step;
import io.zahori.model.process.Test;
import io.zahori.server.model.CaseExecution;
import io.zahori.server.model.ClientTestRepo;
import io.zahori.server.model.Configuration;
import io.zahori.server.model.EvidenceType;
import io.zahori.server.model.Execution;
import io.zahori.server.model.Process;
import io.zahori.server.model.jenkins.Artifact;
import io.zahori.server.model.jenkins.Build;
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

    private static final String NOT_EXECUTED = "Not executed";
    private static final String PASSED = "Passed";
    private static final String FAILED = "Failed";
    private static final String PENDING = "Pending";
    private static final String CREATED = "Created";
    private static final String RUNNING = "Running";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";
    private static final String DATE_FORMAT_JENKINS = "yyyyMMdd-HHmmss";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private ExecutionsRepository executionsRepository;
    private ProcessesRepository processesRepository;
    private EurekaService eurekaService;
    private JenkinsService jenkinsService;
    private SelenoidService selenoidService;
    private CaseExecutionsRepository caseExecutionsRepository;
    private ConfigurationRepository configurationRepository;

    /**
     * Instantiates a new Execution service.
     *
     * @param executionsRepository     the executions repository
     * @param processesRepository      the processes repository
     * @param eurekaService            the eureka service
     * @param jenkinsService           the jenkins service
     * @param selenoidService          the selenoid service
     * @param caseExecutionsRepository the case executions repository
     */
    @Autowired
    public ExecutionService(ExecutionsRepository executionsRepository, ProcessesRepository processesRepository, EurekaService eurekaService,
            JenkinsService jenkinsService, SelenoidService selenoidService, CaseExecutionsRepository caseExecutionsRepository,
            ConfigurationRepository configurationRepository) {
        this.executionsRepository = executionsRepository;
        this.processesRepository = processesRepository;
        this.eurekaService = eurekaService;
        this.jenkinsService = jenkinsService;
        this.selenoidService = selenoidService;
        this.caseExecutionsRepository = caseExecutionsRepository;
        this.configurationRepository = configurationRepository;
    }

    /**
     * Gets executions.
     *
     * @param clientId  the client id
     * @param processId the process id
     * @return the executions
     */
    public Iterable<Execution> getExecutions(Long clientId, Long processId) {
        // log.info("#### getExecutions");
        Iterable<Execution> executions = executionsRepository.findByClientIdAndProcessId(clientId, processId);
        Process process = null;

        List<Execution> executionsChanged = new ArrayList<>();
        for (Execution execution : executions) {
            // log.info("#### execution status: " + execution.getStatus());
            if (RUNNING.equalsIgnoreCase(execution.getStatus())) {

                if (process == null) {
                    Optional<Process> processOpt = processesRepository.findById(execution.getProcess().getProcessId());
                    process = processOpt.get();
                }

                if (process != null && !StringUtils.isBlank(process.getJenkinsJob()) && !StringUtils.isBlank(process.getJenkinsToken())) {
                    Build build = jenkinsService.getBuild(process.getJenkinsJob(), process.getJenkinsToken(), Integer.valueOf(execution.getJenkinsBuild()));
                    if (!build.isBuilding()) {
                        executionsChanged.add(execution);

                        int numCasesPassed = 0;
                        int numCasesFailed = 0;

                        String artifactUrlPrefix = "/" + build.getId() + "/artifact/";
                        for (CaseExecution caseExecution : execution.getCasesExecutions()) {
                            for (Artifact artifact : build.getArtifacts()) {
                                LOG.debug("#### Case: " + caseExecution.getCas().getName());
                                LOG.debug("#### Browser: " + caseExecution.getBrowser().getBrowserName());
                                if (artifact.getRelativePath().contains("/test-results/" + caseExecution.getCas().getName() + "/") //
                                        && StringUtils.containsIgnoreCase(artifact.getRelativePath(),
                                                "/" + caseExecution.getBrowser().getBrowserName() + "/")) {

                                    LOG.debug("#### Path exists in Jenkins");
                                    String fileName = artifact.getFileName();
                                    // log
                                    if (fileName.endsWith(".log")) {
                                        caseExecution.setLog(artifactUrlPrefix + artifact.getRelativePath());
                                    }
                                    // video
                                    if (fileName.endsWith(".avi")) {
                                        caseExecution.setVideo(artifactUrlPrefix + artifact.getRelativePath());
                                    }
                                    // word
                                    if (fileName.endsWith(".docx")) {
                                        caseExecution.setDoc(artifactUrlPrefix + artifact.getRelativePath());
                                    }
                                    // har
                                    if (fileName.endsWith(".har")) {
                                        caseExecution.setHar(artifactUrlPrefix + artifact.getRelativePath());
                                    }
                                    // screenshots
                                    if (fileName.endsWith(".png")) {

                                    }
                                    // case steps
                                    if ("testSteps.json".equalsIgnoreCase(fileName)) {
                                        byte[] fileBytes = jenkinsService.getFile(process.getJenkinsJob() + artifactUrlPrefix + artifact.getRelativePath(),
                                                process.getJenkinsToken());

                                        ObjectMapper mapper = new ObjectMapper();
                                        try {
                                            Test test = mapper.readValue(fileBytes, Test.class);
                                            caseExecution.setStatus(test.getTestStatus());
                                            caseExecution.setDate(getDate(test.getExecutionDate()));
                                            caseExecution.setNotes(test.getExecutionNotes());
                                            caseExecution.setDurationSeconds(test.getDurationSeconds());

                                            if (PASSED.equalsIgnoreCase(test.getTestStatus())) {
                                                numCasesPassed += 1;
                                            } else if (FAILED.equalsIgnoreCase(test.getTestStatus())) {
                                                numCasesFailed += 1;
                                            } else {
                                                caseExecution.setStatus(NOT_EXECUTED);
                                                numCasesFailed += 1;
                                            }

                                            for (Step step : test.getSteps()) {
                                                updateJenkinsAttachmentUrl(step, artifactUrlPrefix, artifact.getRelativePath());

                                                // Substeps
                                                for (Step substep : step.getSubSteps()) {
                                                    updateJenkinsAttachmentUrl(substep, artifactUrlPrefix, artifact.getRelativePath());
                                                }
                                            }
                                            String jsonSteps = mapper.writeValueAsString(test.getSteps());
                                            caseExecution.setSteps(jsonSteps);
                                        } catch (IOException e) {
                                            LOG.error("Error reading testSteps.json: " + e.getMessage());
                                        }
                                    }
                                }
                            }

                            // "testSteps.json" not present, set case as "Not executed" and increase failed
                            // cases
                            if (StringUtils.isBlank(caseExecution.getSteps())) {
                                caseExecution.setStatus(NOT_EXECUTED);
                                numCasesFailed += 1;
                            }
                        }

                        execution.setTotalPassed(numCasesPassed);
                        execution.setTotalFailed(numCasesFailed);
                        if (numCasesPassed > 0 && numCasesFailed == 0) {
                            execution.setStatus(SUCCESS);
                        } else {
                            execution.setStatus(FAILURE);
                        }
                    }
                }
            }
        }

        if (!executionsChanged.isEmpty()) {
            executionsRepository.saveAll(executionsChanged);
        }

        return executions;
    }

    /**
     * Create execution.
     *
     * @param execution the execution
     * @return the execution
     * @throws Exception the exception
     */
    public Execution create(Execution execution) throws Exception {
        try {
            // Set server properties
            execution.setTotalFailed(0);
            execution.setTotalPassed(0);
            execution.setStatus(CREATED);
            execution.setDate(new SimpleDateFormat(DATE_FORMAT).format(new Date()));

            for (CaseExecution caseExecution : execution.getCasesExecutions()) {
                caseExecution.setStatus(PENDING);
            }

            Optional<Process> processOpt = processesRepository.findById(execution.getProcess().getProcessId());
            if (processOpt.isPresent()) {

                Process process = processOpt.get();

                if (StringUtils.isBlank(process.getJenkinsJob()) || StringUtils.isBlank(process.getJenkinsToken())) {
                    invokeProcess(process, execution);
                    return execution;
                }

                // Create execution in database
                execution = executionsRepository.save(execution);

                // Invoke Jenkins job
                // Prepare parameter data for the Jenkins job
                String caseExecutionsJson = new ObjectMapper().writeValueAsString(execution.getCasesExecutions());
                MultiValueMap<String, Object> bodyHttp = new LinkedMultiValueMap<>();
                bodyHttp.add(process.getJenkinsJobParameterName(), caseExecutionsJson);

                // Run Jenkins job
                jenkinsService.triggerJob(process.getJenkinsJob(), process.getJenkinsToken(), bodyHttp);

                // Retrieve jenkins build id when job has started running
                Build lastBuild = null;
                int retry = 0;
                while (true) {
                    retry++;

                    lastBuild = jenkinsService.getLastBuild(process.getJenkinsJob(), process.getJenkinsToken());
                    if (lastBuild.isBuilding()) {
                        break;
                    }

                    if (retry > 10) {
                        break;
                    }

                    TimeUnit.SECONDS.sleep(2);
                }

                // Update execution in database with Jenkins build Id and status
                execution.setJenkinsBuild(lastBuild.getId());
                if (lastBuild.isBuilding()) {
                    execution.setStatus(RUNNING);
                } else {
                    execution.setStatus(lastBuild.getResult());
                }
                execution = executionsRepository.save(execution);

                // Delegate to SelenoidService watch for each case execution in Selenoid
                selenoidService.watchStatus(execution);
            }

            return execution;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private void updateJenkinsAttachmentUrl(Step step, String artifactUrlPrefix, String artifactRelativePath) {
        if (!step.getAttachments().isEmpty()) {
            String attachmentUrl = step.getAttachments().get(0);
            String file = StringUtils.substringAfterLast(attachmentUrl, "\\");
            String fileRelativePath = StringUtils.substringBeforeLast(artifactRelativePath, "/") + "/";
            step.getAttachments().clear();
            step.addAttachment(artifactUrlPrefix + fileRelativePath + file);
        }
    }

    private String getDate(String date) {
        Date formattedDate;
        try {
            formattedDate = new SimpleDateFormat(DATE_FORMAT_JENKINS).parse(date);
            return new SimpleDateFormat(DATE_FORMAT).format(formattedDate);
        } catch (ParseException e) {
            LOG.error("Error parsing jenkins date '" + date + "': " + e.getMessage());
            return "";
        }
    }

    private Execution invokeProcess(Process process, Execution execution) {
        String processUrl = eurekaService.getProcessUrl(process);

        if (StringUtils.isBlank(processUrl)) {
            // TODO retry???
            throw new RuntimeException(
                    "El proceso parece estar offline! Si el proceso se inició recientemente vuelve a intentarlo pasados unos segundos, sino revisa que el proceso esté arrancado.");
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

    private void runProcess(String processUrl, Process process, Execution execution) {

        // Set configuration for each caseExecution
        io.zahori.model.process.Configuration execConfig = getExecutionConfiguration(execution);
        for (CaseExecution caseExecution : execution.getCasesExecutions()) {
            caseExecution.setConfiguration(execConfig);
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
                                    casesExecuted.add(resultCaseExecution);
                                    updateTotals(execution, resultCaseExecution);
                                    updateCaseExecution(resultCaseExecution);
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

    private void updateCaseExecution(CaseExecution resultCaseExecution) {
        Optional<CaseExecution> caseExecutionDBOpt = caseExecutionsRepository.findById(resultCaseExecution.getCaseExecutionId());
        if (caseExecutionDBOpt.isPresent()) {
            CaseExecution caseExecutionDB = caseExecutionDBOpt.get();
            resultCaseExecution.setSelenoidId(caseExecutionDB.getSelenoidId());
            resultCaseExecution.setBrowserVersion(caseExecutionDB.getBrowserVersion());
            resultCaseExecution.setScreenResolution(caseExecutionDB.getScreenResolution());
        }
        caseExecutionsRepository.save(resultCaseExecution);
    }

    private void updateTotals(Execution execution, CaseExecution caseExecution) {
        if (PASSED.equalsIgnoreCase(caseExecution.getStatus())) {
            execution.setTotalPassed(execution.getTotalPassed() + 1);
        } else {
            execution.setTotalFailed(execution.getTotalFailed() + 1);
        }
    }

}
