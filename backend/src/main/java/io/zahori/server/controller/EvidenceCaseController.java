package io.zahori.server.controller;

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

@RestController
@RequestMapping("/api/evidenceCase")
public class EvidenceCaseController {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceCaseController.class);

    @Autowired
    private EvidenceCaseRepository evidenceCasesRepository;

    /**
     * Gets evidencecases.
     *
     * @return the cases
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
