package io.zahori.server.service;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2022 PANEL SISTEMAS INFORMATICOS,S.L
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

import io.zahori.server.controller.EvidencesController;
import io.zahori.server.utils.FilePath;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class EvidencesService {

    private static final Logger LOG = LoggerFactory.getLogger(EvidencesController.class);

    @Value("${zahori.evidences.dir}")
    private String evidencesDir;

    /**
     * Remove evidences  directories from server
     * @param path  basic path to evidences
     * @param dates date to delete
     * @return null if ok or error string
     */
    public String removeEvidences(String path, List<String> dates){
        if (!StringUtils.endsWith(evidencesDir, File.separator)) {
            evidencesDir = evidencesDir + File.separator;
        }
        Path removeDir = Paths.get(FilePath.normalize(evidencesDir + path));
        if (Files.exists(removeDir)){
            try{
                for(String date: dates){
                    Path pathToRemove  = Paths.get(removeDir+ File.separator +date);
                    FileUtils.deleteDirectory(pathToRemove.toFile());
                }
            }catch (Exception  e){
                LOG.error("Error removing  evidences: "+e.getMessage());
                return "Error removing  evidences: "+e.getMessage();
            }
            LOG.info("Evidences  removed: " + path);
            return null;
        }  else  {
            LOG.error("Error removing  evidences: Files not exist");
            return "Error removing  evidences: Files not exist";
        }
    }

    /**
     * Remove server evidences for a one execution
     * @param path path to evidences folder
     * @param date date of the execution
     * @param executionId execution id
     * @return  null if ok or error string
     */
    public String removeEvidenceByExecutionId(String path, String date , String executionId){
        if (!StringUtils.endsWith(evidencesDir, File.separator)) {
            evidencesDir = evidencesDir + File.separator;
        }
        Path removeDir = Paths.get(FilePath.normalize(evidencesDir + path));
        if (Files.exists(removeDir)){
            try{
                    Path pathToRemove  = Paths.get(removeDir+ File.separator +date+ File.separator + executionId);
                    FileUtils.deleteDirectory(pathToRemove.toFile());
            }catch (Exception  e){
                LOG.error("Error removing  evidences: "+e.getMessage());
                return "Error removing  evidences: "+e.getMessage();
            }
            LOG.info("Evidences  removed: " + path);
            return null;
        }  else  {
            LOG.error("Error removing  evidences: Files not exist");
            return "Error removing  evidences: Files not exist";
        }
    }
}
