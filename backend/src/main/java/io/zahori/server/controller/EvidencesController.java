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

import io.zahori.server.repository.ExecutionsRepository;
import io.zahori.server.service.EvidencesService;
import io.zahori.server.utils.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The type Evidences controller.
 */
@RestController
@RequestMapping("/evidence")
public class EvidencesController {

    @Autowired
    private EvidencesService evidencesService;

    @Autowired
    private ExecutionsRepository executionsRepository;

    private static final Logger LOG = LoggerFactory.getLogger(EvidencesController.class);

    @Value("${zahori.evidences.dir}")
    private String evidencesDir;

    /**
     * Upload evidence string.
     *
     * @param path               the path
     * @param file               the file
     * @param redirectAttributes the redirect attributes
     * @return the string
     */
    // TODO review security issue using path as request param
    @PostMapping
    public String uploadEvidence(@RequestParam String path, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {
            if (!StringUtils.endsWith(evidencesDir, File.separator)) {
                evidencesDir = evidencesDir + File.separator;
            }

            Path saveDir = Paths.get(FilePath.normalize(evidencesDir + path));
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }
            String filePath = FilePath.normalize(Paths.get(file.getOriginalFilename()).toString());
            String fileName = StringUtils.substringAfterLast(filePath, File.separator);
            Path pathToSave = Paths.get(saveDir + File.separator + fileName);
            
            Files.write(pathToSave, file.getBytes());
            LOG.info("File uploaded: " + pathToSave.toString());
            redirectAttributes.addFlashAttribute("message", "File uploaded: '" + fileName + "'");

        } catch (IOException e) {
            LOG.error("Error uploading evidence file: " + e.getMessage());
        }

        return "redirect:/uploadStatus";
    }

    /**
     * Upload status string.
     *
     * @return the string
     */
    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    /**
     * Delete the evidence files from the server
     * @param path path of the files on the server (evidences/clientId/processId/date)
     * @return Evidence files  remove status
     * @exception Exception
     */
    @GetMapping("/remove")
    public ResponseEntity<Object> removeEvidences(@RequestParam String path, @RequestParam String[] dates) {
        List<String> fileDates= new ArrayList<String>();
        for(String date: dates){
            Calendar exeDate= Calendar.getInstance();
            exeDate.setTime(new Date(date));
            fileDates.add(String.valueOf(exeDate.get(Calendar.DAY_OF_MONTH))+String.valueOf(exeDate.get(Calendar.MONTH))+String.valueOf(exeDate.get(Calendar.YEAR)));
        }
        String response = evidencesService.removeEvidences(path,fileDates);
       if(response==null) {
           String processId = path.split("/")[1];
           for(String date: dates){
               executionsRepository.deleteExecutionsByDate(Long.valueOf(processId),new Date(date));
           }
           return new ResponseEntity<>("OK", HttpStatus.OK);
       }else
           return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @GetMapping("/remove/{executionId}")
    public ResponseEntity<Object> removeEvidencesByExecution(@RequestParam String path, @RequestParam String date,@PathVariable Long executionId) {
            Calendar exeDate= Calendar.getInstance();
            exeDate.setTime(new Date(date));
            String fileDate =String.valueOf(exeDate.get(Calendar.DAY_OF_MONTH))+String.valueOf(exeDate.get(Calendar.MONTH))+String.valueOf(exeDate.get(Calendar.YEAR));
        String response = evidencesService.removeEvidenceByExecutionId(path,fileDate,executionId.toString());
        if(response==null) {
            executionsRepository.deleteById(executionId);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }else
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
