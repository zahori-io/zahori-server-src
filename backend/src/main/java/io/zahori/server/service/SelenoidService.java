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

import io.zahori.server.model.CaseExecution;
import io.zahori.server.model.Execution;
import io.zahori.server.model.selenoid.SelenoidSession;
import io.zahori.server.model.selenoid.SelenoidStatus;
import io.zahori.server.repository.CaseExecutionsRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * The type Selenoid service.
 */
@Service
public class SelenoidService {

    private static final Logger LOG = LoggerFactory.getLogger(SelenoidService.class);

    @Value("${ZAHORI_SELENOID_UI_INTERNAL_HOST}")
    private String selenoidUiHost;

    @Value("${ZAHORI_SELENOID_UI_INTERNAL_PORT}")
    private String selenoidUiPort;

    private CaseExecutionsRepository caseExecutionsRepository;

    /**
     * Instantiates a new Selenoid service.
     *
     * @param caseExecutionsRepository the case executions repository
     */
    @Autowired
    public SelenoidService(CaseExecutionsRepository caseExecutionsRepository) {
        this.caseExecutionsRepository = caseExecutionsRepository;
    }

    /**
     * Watch status.
     *
     * @param execution the execution
     */
    @Async
    public void watchStatus(Execution execution) {
        LOG.info("Watching selenoid sessions for execution {}", execution.getExecutionId());

        Map<Long, CaseExecution> caseExecutions = getMap(execution);

        // Timeout cuando aún queda algún caso que no se ha llegado ha ejecutar
        int maxTimeoutRetries = 20;
        int timeoutRetry = 0;
        int waitSeconds = 2;
        Set<Long> casesExecuted = new HashSet<>();
        List<CaseExecution> caseExecutionsToUpdate;

        while (true) {
            if (casesExecuted.size() == execution.getCasesExecutions().size()) {
                LOG.info("All cases executed");
                return;
            }

            if (timeoutRetry > maxTimeoutRetries) {
                LOG.info("Timeout retries reached, finish watching selenoid");
                return;
            }

            SelenoidStatus status = getSelenoidStatus();

            if (status.getSessions().isEmpty() && casesExecuted.size() < execution.getCasesExecutions().size()) {
                LOG.info("No sessions and there are still cases pending to be executed");
                wait(waitSeconds);
                timeoutRetry++;
                continue;
            }

            caseExecutionsToUpdate = new ArrayList<>();
            for (SelenoidSession session : status.getSessions().values()) {
                Long caseExecutionIdInSession = Long.valueOf(session.getCaps().getName());

                // Validate that executionId in this session is from a case in this execution
                if (!caseExecutions.containsKey(caseExecutionIdInSession)) {
                    continue;
                }

                // If not running previously -> set selenoid sessionId, browser version and screen resolution
                if (!casesExecuted.contains(caseExecutionIdInSession)) {
                    LOG.info("CaseExecution '{}' is running", caseExecutionIdInSession);

                    CaseExecution caseExecution = caseExecutions.get(caseExecutionIdInSession);
                    caseExecution.setSelenoidId(session.getId());
                    caseExecution.setBrowserVersion(session.getCaps().getVersion());
                    caseExecution.setScreenResolution(session.getCaps().getScreenResolution());

                    caseExecutionsToUpdate.add(caseExecution);
                    casesExecuted.add(caseExecutionIdInSession);
                }
            }

            // Update caseExecutions that changed its state
            caseExecutionsRepository.saveAll(caseExecutionsToUpdate);
            caseExecutionsToUpdate.clear();

            if (casesExecuted.size() < execution.getCasesExecutions().size() || !casesExecuted.isEmpty()) {
                LOG.debug("There are still cases running or pending to be executed");
                wait(waitSeconds);
            }
        }
    }

    private SelenoidStatus getSelenoidStatus() {
        String selenoidUiUrl = "http://" + selenoidUiHost + ":" + selenoidUiPort + "/status";
        LOG.info("get selenoid status: {}", selenoidUiUrl);
        HttpHeaders headers = getHeaders();

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SelenoidStatus> response = restTemplate.exchange(selenoidUiUrl, HttpMethod.GET, requestEntity, SelenoidStatus.class);

        printStatus(response.getStatusCodeValue());

        SelenoidStatus status = response.getBody();

        LOG.info("selenoid status: {}", status);
        return status;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "*/*");
        return headers;
    }

    private void printStatus(int statusCode) {
        LOG.info("--> status: {}", statusCode);
    }

    private Map<Long, CaseExecution> getMap(Execution execution) {
        Map<Long, CaseExecution> map = new HashMap<>();
        if (execution != null && execution.getCasesExecutions() != null) {
            for (CaseExecution caseExecution : execution.getCasesExecutions()) {
                map.put(caseExecution.getCaseExecutionId(), caseExecution);
            }
        }
        return map;
    }

    private void wait(int seconds) {
        LOG.info("Waiting {} seconds", seconds);
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            LOG.error("Error executing sleep for {} seconds", seconds);
        }
    }
}
