package io.zahori.server.controller;

import io.zahori.server.model.Client;
import io.zahori.server.model.ClientTestRepo;
import io.zahori.server.model.TestRepository;
import io.zahori.server.repository.ClientTestRepository;
import io.zahori.server.repository.ClientsRepository;
import io.zahori.server.security.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

/**
 * The type Clients test repo controller.
 */
@RestController
@RequestMapping("/api/clienttestrepo/")

public class ClientTestRepoController {
    private static final Logger LOG = LoggerFactory.getLogger(ClientTestRepoController.class);

    @Autowired
    private ClientTestRepository clientTestRepository;

    @GetMapping()
    public ResponseEntity<Object> getClientTestRepo(HttpServletRequest request) {
        try{
            LOG.info("get client test repo");
            Iterable<ClientTestRepo> clientTestRepo = clientTestRepository.findByClientId(JWTUtils.getClientId(request));
            return new ResponseEntity<>(clientTestRepo, HttpStatus.OK);
        }catch (Exception e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("upgrade")
    public ResponseEntity<Object> saveClientTestRepository (@RequestBody ClientTestRepo clientTestRepo, HttpServletRequest request){
        try{
            LOG.info("upgarde client test repository");
            if(clientTestRepo.getId().getClientId()==null)
                clientTestRepo.getId().setClientId(Long.getLong("0"));
            clientTestRepo = clientTestRepository.save(clientTestRepo);
            return new ResponseEntity<>(clientTestRepo, HttpStatus.OK);
        }catch (Exception e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
