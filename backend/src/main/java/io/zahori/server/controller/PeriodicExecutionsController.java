package io.zahori.server.controller;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 - 2022 PANEL SISTEMAS INFORMATICOS,S.L
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
import io.zahori.server.model.Execution;
import io.zahori.server.model.PeriodicExecution;
import io.zahori.server.model.Process;
import io.zahori.server.model.Task;
import io.zahori.server.repository.ExecutionsRepository;
import io.zahori.server.security.JWTUtils;
import io.zahori.server.service.ExecutionService;
import io.zahori.server.service.PeriodicExecutionService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * The type Execution controller.
 */
@RestController
@RequestMapping()
public class PeriodicExecutionsController {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicExecutionsController.class);

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private ExecutionsRepository executionsRepository;

    @Autowired
    private PeriodicExecutionService periodicExecutionService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${zahori.scheduler.url:}")
    private String zahoriSchedulerUrl;

    @GetMapping(path = "/api/process/{processId}/periodic-executions")
    public ResponseEntity<Object> getPeriodicExecutions(@PathVariable Long processId, HttpServletRequest request) {
        try {
            Iterable<Execution> periodicExecutions = periodicExecutionService.getPeriodicExecutions(JWTUtils.getClientId(request), processId);
            return new ResponseEntity<>(periodicExecutions, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/api/process/{processId}/periodic-executions")
    public ResponseEntity<Object> savePeriodicExecutions(@PathVariable Long processId, @RequestBody List<Execution> executions, HttpServletRequest request) {
        try {
            LOG.info("save periodic executions for process {}", processId);

            //TODO: no fiarse del processId que venga, y también tener en cuenta el cliente
            if (!executions.isEmpty()) {

                // TODO validar contra el scheduler
                for (Execution execution : executions) {
                    Process process = new Process();
                    process.setProcessId(processId);
                    execution.setProcess(process);

//                    for (PeriodicExecution periodicExecution : execution.getPeriodicExecutions()) {
//                        Execution associatedExecution = new Execution();
//                        associatedExecution.setExecutionId(execution.getExecutionId());
//                        periodicExecution.setExecution(associatedExecution);
//                    }
                }
            }
            Iterable<Execution> periodicExecutions = executionsRepository.saveAll(executions);

            //////////////////////
            // Update tasks in scheduler
            for (Execution execution : executions) {
                if (execution.getPeriodicExecutions() != null) {
                    for (PeriodicExecution periodicExecution : execution.getPeriodicExecutions()) {
                        Task task = new Task(periodicExecution.getUuid(), execution.getPeriodicExecutions().get(0).getCronExpression());

                        if (periodicExecution.getActive()) {
                            // 1. Add
                            //      1.a OK
                            //      1.b Error ya existe --> update
                            try {
                                // 1.a
                                ResponseEntity<String> response = restTemplate.postForEntity(zahoriSchedulerUrl, task, String.class);
                                LOG.info("TASK SCHEDULER response --> " + response.getStatusCode());
                                LOG.info("TASK ADDED to SCHEDULER");
                            } catch (Exception e) {
                                // 1.b
                                LOG.error("Error adding task {} to scheduler: {}", periodicExecution.getUuid(), e.getMessage());
                                try {
                                    restTemplate.put(zahoriSchedulerUrl, task);
                                    LOG.info("TASK UPDATED in SCHEDULER");

                                } catch (Exception ex) {
                                    // TODO: que pasa si el shceduler está caído, se captura aquí?
                                    LOG.error("Error updating task {} in scheduler: {}", periodicExecution.getUuid(), e.getMessage());
                                }
                            }
                        } else {
                            // 2. Delete
                            //      2.a OK
                            //      2.b Error no existe --> nada que hacer
                            try {
                                // 2.a
                                restTemplate.delete(zahoriSchedulerUrl + "/" + periodicExecution.getUuid());
                                LOG.info("TASK DELETED from SCHEDULER");
                            } catch (Exception e) {
                                // 2.b
                                LOG.error("Error deleting task {} from scheduler: {}", periodicExecution.getUuid(), e.getMessage());
                            }
                        }

                    }
                }
            }
            //////////////////////

            return new ResponseEntity<>(periodicExecutions, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/api/process/{processId}/periodic-executions/{executionId}")
    public ResponseEntity<Object> deletePeriodicExecution(@PathVariable Long processId, @PathVariable Long executionId, HttpServletRequest request) {
        try {
            Optional<Execution> optionalExecution = executionsRepository.findById(executionId);

            // TODO incluir y validar clientId y processId
            executionsRepository.deleteById(executionId);

            // TODO: Cancel task in scheduler
            if (optionalExecution.isPresent()) {
                Execution execution = optionalExecution.get();

                if (execution.getPeriodicExecutions() != null) {
                    for (PeriodicExecution periodicExecution : execution.getPeriodicExecutions()) {
                        try {
                            restTemplate.delete(zahoriSchedulerUrl + "/" + periodicExecution.getUuid());
                            LOG.info("TASK DELETED from SCHEDULER");

                        } catch (Exception e) {
                            LOG.error("Error deleting task {} from scheduler: {}", periodicExecution.getUuid(), e.getMessage());
                        }
                    }
                }

            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/scheduled/executions/load")
    public ResponseEntity<Object> loadScheduledExecutions() {
        try {
            periodicExecutionService.loadSchedules();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/execution/{executionUUID}/run")
    public ResponseEntity runExecutionByUUID(@PathVariable UUID executionUUID) {
        try {
            LOG.info("run execution controller");

            if (executionUUID == null) {
                return new ResponseEntity<>("UUID is mandatory", HttpStatus.BAD_REQUEST);
            }
            executionService.runPeriodicExecution(executionUUID);

            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
