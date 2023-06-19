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

    public void validateSchedulerIsUp() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(zahoriSchedulerUrl + "healthcheck", String.class);
            LOG.info("Scheduler is up --> {}", response.getStatusCode());
        } catch (Exception e) {
            LOG.error("Scheduler is offline");
            throw new RuntimeException("Scheduler is offline");
        }
    }

    public boolean isTaskScheduled(UUID uuid) {
        try {
            Task task = get(uuid);
            LOG.debug("{} is present in the scheduler", task);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Task get(UUID uuid) {
        try {
            ResponseEntity<Task> response = restTemplate.getForEntity(zahoriSchedulerUrl + uuid, Task.class);
            LOG.info("Get task {} from scheduler --> {}", uuid, response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error getting task " + uuid + " from scheduler: " + e.getMessage());
        }
    }

    public void create(Task task) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(zahoriSchedulerUrl, task, String.class);
            LOG.info("Task added to scheduler {} --> {}", task, response.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Error creating task in scheduler: " + e.getMessage());
        }
    }

    public void update(Task task) {
        try {
            restTemplate.put(zahoriSchedulerUrl, task);
            LOG.info("Task updated in scheduler {}", task);
        } catch (Exception e) {
            throw new RuntimeException("Error updating task in scheduler: " + e.getMessage());
        }
    }

    public void delete(UUID uuid) {
        try {
            restTemplate.delete(zahoriSchedulerUrl + "/" + uuid);
            LOG.info("Task deleted from scheduler {}", uuid);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting task in scheduler: " + e.getMessage());
        }
    }
}
