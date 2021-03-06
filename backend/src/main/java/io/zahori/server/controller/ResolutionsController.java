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

import io.zahori.server.model.*;
import io.zahori.server.model.Process;
import io.zahori.server.repository.ResolutionsRepository;
import io.zahori.server.security.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/api/resolutions")
public class ResolutionsController {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessesController.class);

    @Autowired
    private ResolutionsRepository resolutionsRepository;

    @GetMapping(path = "/{processId}")
    public ResponseEntity<Object> getResolutions(@PathVariable Long processId, HttpServletRequest request) {
        try {
            LOG.info("get resolutions for process: " + processId);
            Iterable<Resolution> resolutions = resolutionsRepository.findByClientIdAndProcessId(JWTUtils.getClientId(request), processId);
            return new ResponseEntity<>(resolutions, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Post configuration response entity.
     *
     * @param processId      the process id
     * @param request        the request
     * @return response entity
     */
    @PostMapping(path = "/{processId}")
    public ResponseEntity<Object> postEnvironments(@PathVariable Long processId, @RequestBody List<Resolution> resolutions,
                                                   HttpServletRequest request) {
        try {
            LOG.info("save environments for process {}", processId);
            Long clientId = JWTUtils.getClientId(request);

            if (resolutions == null) {
                return new ResponseEntity<>(resolutions, HttpStatus.BAD_REQUEST);
            }

            // Set clientId and processId
            for (Resolution resolution : resolutions) {
                Process process = new Process();
                process.setProcessId(processId);
                resolution.setProcess(process);

                Client client = new Client();
                client.setClientId(clientId);
                resolution.setClient(client);
            }

            Iterable<Resolution> res = resolutionsRepository.saveAll(resolutions);

            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
