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

import io.zahori.server.model.Case;
import io.zahori.server.model.Client;
import io.zahori.server.model.ClientEnvironment;
import io.zahori.server.model.ClientTag;
import io.zahori.server.model.Configuration;
import io.zahori.server.model.Execution;
import io.zahori.server.model.Process;
import io.zahori.server.repository.CasesRepository;
import io.zahori.server.repository.ClientEnvironmentsRepository;
import io.zahori.server.repository.ClientTagsRepository;
import io.zahori.server.repository.ConfigurationRepository;
import io.zahori.server.repository.ProcessesRepository;
import io.zahori.server.security.JWTUtils;
import io.zahori.server.service.ExecutionService;
import io.zahori.server.service.JenkinsService;
import io.zahori.server.utils.FilePath;
import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Processes controller.
 */
@RestController
@RequestMapping("/api/process")
public class ProcessesController {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessesController.class);

    @Autowired
    private ProcessesRepository processRepository;

    @Autowired
    private CasesRepository casesRepository;

    @Autowired
    private ClientEnvironmentsRepository environmentsRepository;
       
    @Autowired
    private ClientTagsRepository tagsRepository;
    
    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private JenkinsService jenkinsService;

    @Value("${zahori.evidences.dir}")
    private String evidencesDir;


    /**
     * Gets processes.
     *
     * @return the processes
     */
    @GetMapping()
    public ResponseEntity<Object> getProcesses() {
        try {
            LOG.info("get processes controller");

            Iterable<Process> processes = processRepository.findAll();

            return new ResponseEntity<>(processes, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets cases.
     *
     * @param processId the process id
     * @param request
     * @return the cases
     */
    @GetMapping(path = "/{processId}/cases")
    public ResponseEntity<Object> getCases(@PathVariable Long processId, HttpServletRequest request) {
        try {
            LOG.info("get cases for process: " + processId);

            Iterable<Case> cases = casesRepository.findByClientIdAndProcessId(JWTUtils.getClientId(request), processId);

            return new ResponseEntity<>(cases, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/{processId}/cases")
    public ResponseEntity<Object> postCases(@PathVariable Long processId, @RequestBody List<Case> cases, HttpServletRequest request) {
        try {
            LOG.info("save cases for process {}", processId);
            
            if (!cases.isEmpty()){
                for (Case cas: cases){
                    Process process = new Process();
                    process.setProcessId(processId);
                    cas.setProcess(process);
                }
            }
            Iterable<Case> savedCases = casesRepository.saveAll(cases);

            return new ResponseEntity<>(savedCases, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Gets configuration.
     *
     * @param processId the process id
     * @return the configuration
     */
    @GetMapping(path = "/{processId}/configurations")
    public ResponseEntity<Object> getConfiguration(@PathVariable Long processId) {
        try {
            LOG.info("get configuration for process: " + processId);

            Iterable<Configuration> configurations = configurationRepository.findByProcessId(processId);

            return new ResponseEntity<>(configurations, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
  
   /**
    *  
    * @param processId
    * @param configurations
    * @param request
    * @return
    */

    @PostMapping(path = "/{processId}/configurations")
    public ResponseEntity<Object> postConfiguration(@PathVariable Long processId, @RequestBody List<Configuration> configurations, HttpServletRequest request) {
        try {
            LOG.info("save conf for process {}", processId);
            
            if (configurations == null){
                return new ResponseEntity<>(configurations, HttpStatus.BAD_REQUEST);
            }
            
            // processId
            for (Configuration configuration: configurations){
                Process process = new Process();
                process.setProcessId(processId);
                configuration.setProcess(process);
            }
            
            Iterable<Configuration> envs = configurationRepository.saveAll(configurations);

            return new ResponseEntity<>(envs, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets executions.
     *
     * @param processId the process id
     * @param request   the request
     * @return the executions
     */
    @GetMapping(path = "/{processId}/executions")
    public ResponseEntity<Object> getExecutions(@PathVariable Long processId, HttpServletRequest request) {
        try {
            LOG.info("get executions controller");

            Iterable<Execution> executions = executionService.getExecutions(JWTUtils.getClientId(request), processId);

            return new ResponseEntity<>(executions, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Post executions response entity.
     *
     * @param execution the execution
     * @param request   the request
     * @return the response entity
     */
    @PostMapping(path = "/{processId}/executions")
    public ResponseEntity<Object> postExecutions(@RequestBody Execution execution, HttpServletRequest request) {
        try {
            LOG.info("create execution controller");
            execution = executionService.create(execution);

            return new ResponseEntity<>(execution, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets evidence file.
     *
     * @param processId the process id
     * @param path      the path
     * @param request   the request
     * @return the evidence file
     */
    @GetMapping(path = "/{processId}/file")
    public ResponseEntity<Object> getEvidenceFile(@PathVariable Long processId, @RequestParam String path, HttpServletRequest request) {
        try {
            LOG.info("get evidence file {} for process {}", path, processId);

            Long clientId = JWTUtils.getClientId(request);

            String pathNormalized = FilePath.normalize(evidencesDir + path);
            File evidenceFile = new File(pathNormalized);
            byte[] fileBytes = FileUtils.readFileToByteArray(evidenceFile);
            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            String filenameToDownload = StringUtils.substringAfterLast(pathNormalized, File.separator);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filenameToDownload);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok().headers(headers).contentLength(fileBytes.length)
                    .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE)).body(resource);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>("Error getting evidence file " + path + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets jenkins file.
     *
     * @param processId the process id
     * @param url       the url
     * @param request   the request
     * @return the jenkins file
     */
    @GetMapping(path = "/{processId}/artifact")
    public ResponseEntity<Object> getJenkinsFile(@PathVariable Long processId, @RequestParam String url, HttpServletRequest request) {
        try {
            LOG.info("get jenkins artifact {} for process {}", url, processId);

            Process process = processRepository.findByClientIdAndProcessId(JWTUtils.getClientId(request), processId);

            byte[] fileBytes = jenkinsService.getFile(process.getJenkinsJob() + url, process.getJenkinsToken());
            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            String filenameToDownload = StringUtils.substringAfterLast(url, "/");

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filenameToDownload);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok().headers(headers).contentLength(fileBytes.length)
                    .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE)).body(resource);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>("Error downloading file " + url + " for process " + processId + ": " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets environments.
     *
     * @param processId the process id
     * @param request   the request
     * @return the environments
     */
    @GetMapping(path = "/{processId}/environments")
    public ResponseEntity<Object> getEnvironments(@PathVariable Long processId, HttpServletRequest request) {
        try {
            LOG.info("get environments");
            Iterable<ClientEnvironment> environments = environmentsRepository.findByClientIdAndProcessId(JWTUtils.getClientId(request), processId);
            return new ResponseEntity<>(environments, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Post environments response entity.
     *
     * @param processId    the process id
     * @param environments the environments
     * @param request      the request
     * @return the response entity
     */
    @PostMapping(path = "/{processId}/environments")
    public ResponseEntity<Object> postEnvironments(@PathVariable Long processId, @RequestBody List<ClientEnvironment> environments, HttpServletRequest request) {
        try {
            LOG.info("save environments for process {}", processId);
            Long clientId = JWTUtils.getClientId(request);
            
            if (environments == null){
                return new ResponseEntity<>(environments, HttpStatus.BAD_REQUEST);
            }
            
            // Set clientId and processId
            for (ClientEnvironment environment: environments){
                Process process = new Process();
                process.setProcessId(processId);
                environment.setProcess(process);
                
                Client client = new Client();
                client.setClientId(clientId);
                environment.setClient(client);
            }
            
            Iterable<ClientEnvironment> envs = environmentsRepository.saveAll(environments);

            return new ResponseEntity<>(envs, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets tags.
     *
     * @param processId the process id
     * @param request   the request
     * @return the tags
     */
    @GetMapping(path = "/{processId}/tags")
    public ResponseEntity<Object> getTags(@PathVariable Long processId, HttpServletRequest request) {
        try {
            LOG.info("get tags for process {}", processId);
            Iterable<ClientTag> tags = tagsRepository.findByClientIdAndProcessId(JWTUtils.getClientId(request), processId);
            return new ResponseEntity<>(tags, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Post tags response entity.
     *
     * @param processId the process id
     * @param tags      the tags
     * @param request   the request
     * @return the response entity
     */
    @PostMapping(path = "/{processId}/tags")
    public ResponseEntity<Object> postTags(@PathVariable Long processId, @RequestBody List<ClientTag> tags, HttpServletRequest request) {
        try {
            LOG.info("save tags for process {}", processId);
            Long clientId = JWTUtils.getClientId(request);
            
            if (tags == null){
                return new ResponseEntity<>(tags, HttpStatus.BAD_REQUEST);
            }
            
            // Set clientId and processId
            for (ClientTag tag: tags){
                Process process = new Process();
                process.setProcessId(processId);
                tag.setProcess(process);
                
                Client client = new Client();
                client.setClientId(clientId);
                tag.setClient(client);
            }
            
            Iterable<ClientTag> tagsSaved = tagsRepository.saveAll(tags);

            return new ResponseEntity<>(tagsSaved, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
