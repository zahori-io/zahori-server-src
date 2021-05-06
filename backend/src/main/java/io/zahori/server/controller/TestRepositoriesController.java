package io.zahori.server.controller;

import io.zahori.server.model.Case;
import io.zahori.server.model.ClientTestRepo;
import io.zahori.server.model.Configuration;
import io.zahori.server.model.TestRepository;
import io.zahori.server.repository.ClientTestRepository;
import io.zahori.server.repository.TestRepositoriesRepository;
import io.zahori.server.security.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The type test repository controller.
 */
@RestController
@RequestMapping("/api/testrepo/")
public class TestRepositoriesController {
    private static final Logger LOG = LoggerFactory.getLogger(TestRepositoriesController.class);

    @Autowired
    private TestRepositoriesRepository testRepositoriesRepository;

    @GetMapping("{testId}")
    public ResponseEntity<Object> getTestRepository(@PathVariable Long testId,HttpServletRequest request) {
        try{
            LOG.info("get test repository");

            Optional<TestRepository> testRepository = testRepositoriesRepository.findById(testId);
            return new ResponseEntity<>(testRepository, HttpStatus.OK);
        }catch (Exception e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("upgrade")
    public ResponseEntity<Object> saveTestRepository (@RequestBody TestRepository testRepository, HttpServletRequest request){
        try{
            LOG.info("upgarde test repository");
            if(!testRepository.getActive())
                testRepository.setActive(true);
            testRepository = testRepositoriesRepository.save(testRepository);
            return new ResponseEntity<>(testRepository, HttpStatus.OK);
        }catch (Exception e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("delete")
    public ResponseEntity<Object> deleteTestRepository(@RequestBody TestRepository testRepository, HttpServletRequest request){
        try{
            testRepository.setActive(false);
            testRepository = testRepositoriesRepository.save(testRepository);
            return new ResponseEntity<>(testRepository, HttpStatus.OK);
        }catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
