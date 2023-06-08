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
import io.zahori.server.model.PeriodicExecutionView;
import io.zahori.server.repository.ExecutionsRepository;
import io.zahori.server.repository.PeriodicExecutionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    @Value("${zahori.scheduler.url:}")
    private String zahoriSchedulerUrl;

    private RestTemplate restTemplate;

    @Autowired
    public PeriodicExecutionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Iterable<Execution> getPeriodicExecutions(Long clientId, Long processId) {
        return executionsRepository.findByClientIdAndProcessIdAndStatus(clientId, processId, "Scheduled");
    }

    public void loadSchedules() throws Exception {
        Iterable<PeriodicExecutionView> tasks = periodicExecutionsRepository.findAllProjectedBy();
        for (PeriodicExecutionView task : tasks) {
            ResponseEntity<String> response = null;
            try {
                response = restTemplate.postForEntity(zahoriSchedulerUrl, task, String.class);
                LOG.info("Task added to scheduler {} --> {}", task, response.getStatusCode());
                // TODO si alguna falla devolver error?
            } catch (Exception e) {
                // TODO: que pasa si el shceduler está caído, se captura aquí?
                if (response != null) {
                    LOG.error("Task not added to scheduler {} --> {}", task, response.getStatusCode());
                }
            }
        }
    }

}
