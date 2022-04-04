package io.zahori.server.controller;

import io.zahori.server.model.Execution;
import io.zahori.server.service.ExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * The type Execution controller.
 */
@RestController
@RequestMapping("/api/execution")
public class ExecutionsController {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionsController.class);

    @Autowired
    private ExecutionService executionService;

    /**
     * Gets execution by id
     *
     * @param executionId  the execution id
     * @return the execution
     */
    @GetMapping(path = "/{executionId}")
    public ResponseEntity<Object> getExecutionById(@PathVariable Long executionId) {
        try {
            LOG.info("get execution controller");

            Optional<Execution> execution = executionService.getExecutionById(executionId);

            return new ResponseEntity<>(execution, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}