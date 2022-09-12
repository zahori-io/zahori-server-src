package io.zahori.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zahori.server.model.ClientTestRepo;
import io.zahori.server.repository.ClientTestRepository;
import io.zahori.server.security.JWTUtils;
import io.zahori.server.service.ZahoriCipherService;

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
    private ZahoriCipherService zahoriCipherService;

    @GetMapping()
    public ResponseEntity<Object> getClientTestRepo(HttpServletRequest request) {
        try {
            LOG.info("get client test repository");
            Iterable<ClientTestRepo> clientTestRepos = clientTestRepository.findByClientId(JWTUtils.getClientId(request));

            return new ResponseEntity<>(clientTestRepos, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<Object> saveClientTestRepository(@RequestBody ClientTestRepo clientTestRepo, HttpServletRequest request) {
        try {
            LOG.info("update client test repository");
            if (clientTestRepo.getId().getClientId() == null) {
                clientTestRepo.getId().setClientId(JWTUtils.getClientId(request));
            }

            // Retrieve old password and compare if user has changed it. If password has changed, encrypt it.
            ClientTestRepo clientTestRepoDB = clientTestRepository.findByClientIdAndTestRepoId(JWTUtils.getClientId(request),
                    clientTestRepo.getId().getTestRepoId());
            if (!clientTestRepoDB.getPassword().equals(clientTestRepo.getPassword())) {
                clientTestRepo.setPassword(zahoriCipherService.encode(clientTestRepo.getPassword()));
            }

            clientTestRepo = clientTestRepository.save(clientTestRepo);
            return new ResponseEntity<>(clientTestRepo, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
