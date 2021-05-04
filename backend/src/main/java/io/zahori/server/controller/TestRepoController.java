package io.zahori.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zahori.server.model.TestRepository;
import io.zahori.server.repository.TestRepoRepository;

@RestController
@RequestMapping("/api/repositories")
public class TestRepoController {

    private static final Logger LOG = LoggerFactory.getLogger(TestRepoController.class);

    @Autowired
    private TestRepoRepository trRepository;

    /**
     * Gets repositories.
     *
     * @return the test repositories
     */
    @GetMapping()
    public ResponseEntity<Object> getRepositories() {
        try {
            LOG.info("get cases");

            Iterable<TestRepository> repos = trRepository.findAll();

            return new ResponseEntity<>(repos, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
