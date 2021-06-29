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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.zahori.server.model.ServerVersions;

/**
 * The type Browsers controller.
 */
@RestController
@RequestMapping("/api/version")
public class VersionController {

    private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

    @Value("${server.version}")
    private String currentVersion;

    @Value("${zahori.evidences.dir}")
    private String evidencesDir;

    private static final String SERVER_VERSION_UUID = "server-version-uuid";

    /**
     * Gets current and latest version of zahori server.
     *
     * @return the versions
     */
    @GetMapping()
    public ResponseEntity<Object> getVersions() {
        try {
            // Get latest available version
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://be02zf87b0.execute-api.eu-west-3.amazonaws.com/default/latest-server-version/1.0/" + getUUID() + "/" + currentVersion;
            ResponseEntity<ServerVersions> serverVersions = restTemplate.getForEntity(url, ServerVersions.class);

            if (serverVersions.getStatusCode() != HttpStatus.OK) {
                // Make request to backup version service
                String backupUrl = "https://webservicesapi.zahori.io/latest-server-version/1.0/" + getUUID() + "/" + currentVersion;
                serverVersions = restTemplate.getForEntity(backupUrl, ServerVersions.class);
            }

            return new ResponseEntity<>(serverVersions.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getUUID() {
        Path filePath = Path.of(evidencesDir + SERVER_VERSION_UUID);
        String uuid = "";
        try {
            if (Files.exists(filePath)) {
                uuid = Files.readString(filePath, StandardCharsets.UTF_8);
            }

            if (StringUtils.isBlank(uuid)) {
                uuid = UUID.randomUUID().toString();
                Files.writeString(filePath, uuid, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
        }

        return uuid;
    }

}
