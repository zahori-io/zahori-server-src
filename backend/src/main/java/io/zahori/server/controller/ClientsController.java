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

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zahori.server.model.Client;
import io.zahori.server.repository.ClientsRepository;
import io.zahori.server.security.JWTUtils;

/**
 * The type Clients controller.
 */
@RestController
@RequestMapping("/api/client")
public class ClientsController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientsController.class);

    @Autowired
    private ClientsRepository clientRepository;

    /**
     * Gets client.
     *
     * @param request the request
     * @return the client
     */
    @GetMapping
    public ResponseEntity<Object> getClient(HttpServletRequest request) {
        try {
            LOG.info("get client");

            Optional<Client> client = clientRepository.findById(JWTUtils.getClientId(request));

            return new ResponseEntity<>(client, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
