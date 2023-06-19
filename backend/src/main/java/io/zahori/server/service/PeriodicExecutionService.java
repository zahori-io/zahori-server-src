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
import io.zahori.server.model.Execution;
import io.zahori.server.model.PeriodicExecution;
import io.zahori.server.model.PeriodicExecutionView;
import io.zahori.server.model.Process;
import io.zahori.server.model.Task;
import io.zahori.server.repository.ExecutionsRepository;
import io.zahori.server.repository.PeriodicExecutionsRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type Execution service.
 */
@Service
public class PeriodicExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicExecutionService.class);

    @Autowired
    private PeriodicExecutionsRepository periodicExecutionsRepository;

    @Autowired
    private ExecutionsRepository executionsRepository;

    @Autowired
    private SchedulerService schedulerService;

    public PeriodicExecutionService() {
    }

    public Iterable<Execution> getPeriodicExecutions(Long clientId, Long processId) {
        return executionsRepository.findByClientIdAndProcessIdAndStatus(clientId, processId, "Scheduled");
    }

    public void loadSchedules() throws Exception {
        Iterable<PeriodicExecutionView> tasks = periodicExecutionsRepository.findAllProjectedByActive(true);
        for (PeriodicExecutionView task : tasks) {
            schedulerService.create(new Task(task.getUuid(), task.getCronExpression()));
        }
    }

    @Transactional
    public Iterable<Execution> save(Long processId, List<Execution> executions) {
        if (executions == null || executions.isEmpty()) {
            return executions;
        }

        // Validate scheduler is up
        schedulerService.validateSchedulerIsUp();

        // TODO validate processId and clientId
        for (Execution execution : executions) {
            Process process = new Process();
            process.setProcessId(processId);
            execution.setProcess(process);
        }

        Iterable<Execution> executionsSavedInDB = executionsRepository.saveAll(executions);

        // Update tasks in scheduler
        for (Execution execution : executionsSavedInDB) {
            if (execution.getPeriodicExecutions() != null) {
                for (PeriodicExecution periodicExecution : execution.getPeriodicExecutions()) {
                    Task task = new Task(periodicExecution.getUuid(), periodicExecution.getCronExpression());

                    boolean taskIsInScheduler = schedulerService.isTaskScheduled(task.getUuid());
                    // Update
                    if (periodicExecution.isActive() && taskIsInScheduler) {
                        schedulerService.update(task);
                    }
                    // Create
                    if (periodicExecution.isActive() && !taskIsInScheduler) {
                        schedulerService.create(task);
                    }
                    // Delete
                    if (!periodicExecution.isActive() && taskIsInScheduler) {
                        schedulerService.delete(task.getUuid());
                    }
                    //
                    if (!periodicExecution.isActive() && !taskIsInScheduler) {
                        // nothing to do
                    }
                }
            }
        }

        return executionsSavedInDB;
    }

    @Transactional
    public void delete(Long executionId) {
        // TODO include and validate processId and clientId

        // Validate scheduler is up
        schedulerService.validateSchedulerIsUp();

        // get execution from db
        Optional<Execution> optionalExecution = executionsRepository.findById(executionId);

        if (optionalExecution.isEmpty()) {
            // TODO throw exception 404
            throw new RuntimeException("Execution not present");
        }
        Execution execution = optionalExecution.get();

        // delete execution from DB
        executionsRepository.deleteById(execution.getExecutionId());

        // delete periodic executions from scheduler
        if (execution.getPeriodicExecutions() != null) {
            for (PeriodicExecution periodicExecution : execution.getPeriodicExecutions()) {
                if (schedulerService.isTaskScheduled(periodicExecution.getUuid())) {
                    schedulerService.delete(periodicExecution.getUuid());
                }
            }
        }
    }

}
