package io.zahori.server.service;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.zahori.server.model.jenkins.Build;
import io.zahori.server.model.jenkins.Builds;
import io.zahori.server.model.jenkins.JobFileParameter;

/**
 * The type Jenkins service.
 */
@Service
public class JenkinsService {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

    private static final String API_JSON = "/api/json";

    private JenkinsService() {
    }

    /**
     * Trigger job.
     *
     * @param jobUrl         the job url
     * @param authToken      the auth token
     * @param fileParameters the file parameters
     */
    public void triggerJob(String jobUrl, String authToken, List<JobFileParameter> fileParameters) {
        jobUrl = jobUrl + "/buildWithParameters";
        LOG.info("trigger job: {}", jobUrl);

        HttpHeaders headers = getHeaders(authToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (!fileParameters.isEmpty()) {
            for (JobFileParameter fileParameter : fileParameters) {
                FileSystemResource file = new FileSystemResource(fileParameter.getFilePath() + fileParameter.getFileName());
                body.add(fileParameter.getName(), file);
            }
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(jobUrl, requestEntity, String.class);

        printStatus(response.getStatusCodeValue());
    }

    /**
     * Trigger job.
     *
     * @param jobUrl        the job url
     * @param authToken     the auth token
     * @param fileParameter the file parameter
     */
    public void triggerJob(String jobUrl, String authToken, JobFileParameter fileParameter) {
        List<JobFileParameter> fileParameters = new ArrayList<>();
        fileParameters.add(fileParameter);
        triggerJob(jobUrl, authToken, fileParameters);
    }

    /**
     * Trigger job.
     *
     * @param jobUrl    the job url
     * @param authToken the auth token
     * @param body      the body
     */
    public void triggerJob(String jobUrl, String authToken, MultiValueMap<String, Object> body) {
        jobUrl = jobUrl + "/buildWithParameters";
        LOG.info("trigger job: {}", jobUrl);

        HttpHeaders headers = getHeaders(authToken);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(jobUrl, requestEntity, String.class);

        printStatus(response.getStatusCodeValue());
    }

    /**
     * Gets builds.
     *
     * @param jobUrl    the job url
     * @param authToken the auth token
     * @return the builds
     */
    public List<Build> getBuilds(String jobUrl, String authToken) {
        List<Build> builds = new ArrayList<>();

        if (StringUtils.isBlank(jobUrl) || StringUtils.isBlank(authToken)) {
            return builds;
        }

        String url = jobUrl + API_JSON;
        LOG.info("get builds: {}", url);

        HttpHeaders headers = getHeaders(authToken);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Builds> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Builds.class);

        printStatus(response.getStatusCodeValue());

        Builds responseBuilds = response.getBody();
        for (Build build : responseBuilds.getBuilds()) {
            builds.add(getBuild(jobUrl, authToken, build.getNumber()));
        }

        return builds;
    }

    /**
     * Gets build.
     *
     * @param jobUrl      the job url
     * @param authToken   the auth token
     * @param buildNumber the build number
     * @return the build
     */
    public Build getBuild(String jobUrl, String authToken, int buildNumber) {
        jobUrl = jobUrl + "/" + buildNumber + API_JSON;
        return getBuild(jobUrl, authToken);
    }

    /**
     * Gets last build.
     *
     * @param jobUrl    the job url
     * @param authToken the auth token
     * @return the last build
     */
    public Build getLastBuild(String jobUrl, String authToken) {
        jobUrl = jobUrl + "/lastBuild" + API_JSON;
        return getBuild(jobUrl, authToken);
    }

    private Build getBuild(String url, String authToken) {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(authToken)) {
            return null;
        }

        LOG.info("get build: {}", url);

        HttpHeaders headers = getHeaders(authToken);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Build> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Build.class);

        printStatus(response.getStatusCodeValue());

        Build build = response.getBody();
        build.setUrlExcelOut();
        build.setUrlWordOut();

        return build;
    }

    /**
     * Get file byte [ ].
     *
     * @param url       the url
     * @param authToken the auth token
     * @return the byte [ ]
     */
    public byte[] getFile(String url, String authToken) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        HttpHeaders headers = getHeaders(authToken);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class, "1");

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return new byte[0];
        }
    }

    private HttpHeaders getHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "*/*");
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + authToken);
        return headers;
    }

    private void printStatus(int statusCode) {
        LOG.info("--> status: {}", statusCode);
    }
}
