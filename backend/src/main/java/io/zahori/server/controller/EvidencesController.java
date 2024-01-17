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

import io.zahori.server.utils.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The type Evidences controller.
 */
@RestController
@RequestMapping("/evidence")
public class EvidencesController {

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
    public String uploadEvidence(@RequestParam String path, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {

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

}
