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
import io.zahori.server.model.Task;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${zahori.scheduler.url:}")
    private String zahoriSchedulerUrl;

    @Autowired
    public SchedulerService(RestTemplate restTemplate) {

    }

    public Task get(UUID uuid) {
        try {
            ResponseEntity<Task> response = restTemplate.getForEntity(zahoriSchedulerUrl + uuid, Task.class);
            LOG.info("Get task {} from scheduler --> {}", uuid, response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            LOG.error("Error getting task {} from scheduler: {}", uuid, e.getMessage());
            return null;
        }
    }

    public void create(Task task) {
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(zahoriSchedulerUrl, task, String.class);
            LOG.info("Task added to scheduler {} --> {}", task, response.getStatusCode());
            // TODO si alguna falla devolver error?
        } catch (Exception e) {
            // TODO: que pasa si el shceduler está caído, se captura aquí?
            if (response != null) {
                LOG.error("Error creating {} in scheduler --> {}: {}", task, response.getStatusCode(), e.getMessage());
            }
        }
    }

    public void update(Task task) {
        try {
            restTemplate.put(zahoriSchedulerUrl, task);
            LOG.info("Task updated in scheduler {}", task);
            // TODO si alguna falla devolver error?
        } catch (Exception e) {
            // TODO: que pasa si el shceduler está caído, se captura aquí?
            LOG.error("Error updating {} in scheduler --> {}", task, e.getMessage());
        }
    }

    public void delete(UUID uuid) {
        try {
            restTemplate.delete(zahoriSchedulerUrl + "/" + uuid);
            LOG.info("Task deleted from scheduler {}", uuid);
        } catch (Exception e) {
            LOG.error("Error deleting task {} from scheduler: {}", uuid, e.getMessage());
        }
    }
}
