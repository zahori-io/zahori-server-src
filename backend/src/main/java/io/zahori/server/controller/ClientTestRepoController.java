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
import io.zahori.server.model.Client;
import io.zahori.server.model.ClientTestRepo;
import io.zahori.server.model.Configuration;
import io.zahori.server.repository.ClientTestRepository;
import io.zahori.server.repository.ConfigurationRepository;
import io.zahori.server.security.JWTUtils;
import io.zahori.server.service.ZahoriCipherService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
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

/**
 * The type Clients test repo controller.
 */
@RestController
@RequestMapping("/api/clientTestRepos")

public class ClientTestRepoController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientTestRepoController.class);

    @Autowired
    private ClientTestRepository clientTestRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ZahoriCipherService zahoriCipherService;

    @GetMapping
    public ResponseEntity<Object> getClientTestRepositories(HttpServletRequest request) {
        try {
            LOG.info("get client test repositories");
            Iterable<ClientTestRepo> clientTestRepos = clientTestRepository.findByClientId(JWTUtils.getClientId(request));

            return new ResponseEntity<>(clientTestRepos, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Object> saveClientTestRepository(@RequestBody ClientTestRepo clientTestRepo, HttpServletRequest request) {
        try {
            LOG.info("update client test repository");
            Long clientId = JWTUtils.getClientId(request);

            // Retrieve old password and compare if user has changed it. If password has changed, encrypt it.
            ClientTestRepo clientTestRepoDB = clientTestRepository.findByClientIdAndRepoInstanceId(clientId, clientTestRepo.getRepoInstanceId());
            if (clientTestRepoDB != null) {
                clientTestRepo.setRepoInstanceId(clientTestRepoDB.getRepoInstanceId());
                clientTestRepo.setClient(clientTestRepoDB.getClient());
                if (StringUtils.isBlank(clientTestRepo.getPassword())) {
                    clientTestRepo.setPassword(clientTestRepoDB.getPassword());
                } else {
                    clientTestRepo.setPassword(zahoriCipherService.encode(clientTestRepo.getPassword()));
                }
            }
            if (clientTestRepoDB == null) {
                Client client = new Client();
                client.setClientId(clientId);
                clientTestRepo.setClient(client);
                clientTestRepo.setPassword(zahoriCipherService.encode(clientTestRepo.getPassword()));
            }

            clientTestRepo = clientTestRepository.save(clientTestRepo);
            return new ResponseEntity<>(clientTestRepo, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{repoInstanceId}")
    public ResponseEntity<Object> deleteClientTestRepository(@PathVariable Long repoInstanceId, HttpServletRequest request) {
        try {
            LOG.info("delete client test repository");
            Long clientId = JWTUtils.getClientId(request);

            Set<Configuration> configurations = configurationRepository.findByClientIdAndRepoInstanceId(clientId, repoInstanceId);
            if (!configurations.isEmpty()) {
                return new ResponseEntity<>("", HttpStatus.CONFLICT);
            }

            clientTestRepository.deleteByClientIdAndRepoInstanceId(clientId, repoInstanceId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
