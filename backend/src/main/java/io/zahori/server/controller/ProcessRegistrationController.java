package io.zahori.server.controller;

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

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zahori.server.model.Process;
import io.zahori.server.model.ProcessRegistration;
import io.zahori.server.service.ProcessService;

/**
 * The type Process registration controller.
 */
@RestController
@RequestMapping("/process")
public class ProcessRegistrationController {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessRegistrationController.class);

    @Autowired
    private ProcessService processService;

    /**
     * Post executions response entity.
     *
     * @param processRegistration the process registration
     * @param request             the request
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<Object> postExecutions(@RequestBody ProcessRegistration processRegistration, HttpServletRequest request) {
        try {

            Process process = processService.findProcessRegistered(processRegistration);
            if (process != null) {
                processRegistration.setProcessId(process.getProcessId());
                LOG.info("Process registered with id {}: {}", processRegistration.getProcessId(), processRegistration);
                return new ResponseEntity<>(processRegistration, HttpStatus.OK);
            }

            process = processService.register(processRegistration);
            processRegistration.setProcessId(process.getProcessId());

            LOG.info("New process registered with id {}: {}", processRegistration.getProcessId(), processRegistration);
            return new ResponseEntity<>(processRegistration, HttpStatus.CREATED);

        } catch (Exception e) {
            String error = "Process registration error: " + e.getMessage();
            LOG.error(error);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
