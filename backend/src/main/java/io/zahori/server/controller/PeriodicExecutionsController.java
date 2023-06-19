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
import io.zahori.server.security.JWTUtils;
import io.zahori.server.service.ExecutionService;
import io.zahori.server.service.PeriodicExecutionService;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class PeriodicExecutionsController {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicExecutionsController.class);

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private PeriodicExecutionService periodicExecutionService;

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

            if (executions == null || executions.isEmpty()) {
                return new ResponseEntity<>("A list of executions is mandatory", HttpStatus.BAD_REQUEST);
            }

            //TODO: validate processId and clientId
            Iterable<Execution> periodicExecutions = periodicExecutionService.save(processId, executions);

            return new ResponseEntity<>(periodicExecutions, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/api/process/{processId}/periodic-executions/{executionId}")
    public ResponseEntity<Object> deletePeriodicExecution(@PathVariable Long processId, @PathVariable Long executionId, HttpServletRequest request) {
        try {
            periodicExecutionService.delete(executionId);
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
