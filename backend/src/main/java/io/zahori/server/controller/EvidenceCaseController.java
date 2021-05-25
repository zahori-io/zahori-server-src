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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zahori.server.model.ClientEnvironment;
import io.zahori.server.model.EvidenceCase;
import io.zahori.server.repository.EvidenceCaseRepository;
import io.zahori.server.security.JWTUtils;

/**
 * The type Evidence case controller.
 */
@RestController
@RequestMapping("/api/evidenceCase")
public class EvidenceCaseController {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceCaseController.class);

    @Autowired
    private EvidenceCaseRepository evidenceCasesRepository;

    /**
     * Gets evidence cases.
     *
     * @param request the request
     * @return the evidence cases
     */
    @GetMapping()
    public ResponseEntity<Object> getEvidenceCases(HttpServletRequest request) {
        try {
            LOG.info("get evidence cases");

            Iterable<EvidenceCase> cases = evidenceCasesRepository.findAll();

            return new ResponseEntity<>(cases, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
