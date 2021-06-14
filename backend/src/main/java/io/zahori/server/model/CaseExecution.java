package io.zahori.server.model;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.zahori.model.process.Step;

/**
 * The type Case execution.
 */
@Entity
@Table(name = "cases_executions")
public class CaseExecution implements Serializable {

    private static final long serialVersionUID = -3115372415270134079L;
    private static final Logger LOG = LoggerFactory.getLogger(CaseExecution.class);

    @Id
    @Column(name = "case_execution_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseExecutionId;

    private String date;

    private String log;

    private String notes;

    private String status;

    private String steps;

    @Transient
    private List<Step> stepsJson;

    private String video;

    private String doc;

    private String har;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "selenoid_id")
    private String selenoidId;

    @Column(name = "screen_resolution")
    private String screenResolution;

    @Column(name = "browser_version")
    private String browserVersion;

    // unidirectional many-to-one association to Browser
    @ManyToOne
    @JoinColumn(name = "browser_name")
    private Browser browser;

    // bi-directional many-to-one association to Cas
    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case cas;

    /**
     * Instantiates a new Case execution.
     */
    public CaseExecution() {
    }

    /**
     * Gets case execution id.
     *
     * @return the case execution id
     */
    public Long getCaseExecutionId() {
        return caseExecutionId;
    }

    /**
     * Sets case execution id.
     *
     * @param caseExecutionId the case execution id
     */
    public void setCaseExecutionId(Long caseExecutionId) {
        this.caseExecutionId = caseExecutionId;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets log.
     *
     * @return the log
     */
    public String getLog() {
        return this.log;
    }

    /**
     * Sets log.
     *
     * @param log the log
     */
    public void setLog(String log) {
        this.log = log;
    }

    /**
     * Gets notes.
     *
     * @return the notes
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Sets notes.
     *
     * @param notes the notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets steps.
     *
     * @return the steps
     */
    public String getSteps() {
        return this.steps;
    }

    /**
     * Sets steps.
     *
     * @param steps the steps
     */
    public void setSteps(String steps) {
        this.steps = steps;
    }

    /**
     * Gets steps json.
     *
     * @return the steps json
     */
    public List<Step> getStepsJson() {
        try {
            if (this.steps != null) {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<List<Step>> typeReference = new TypeReference<>() {
                };
                return mapper.readValue(this.steps, typeReference);
            }
        } catch (IOException e) {
            LOG.error("Error getting jsonSteps: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Sets steps json.
     *
     * @param stepsJson the steps json
     */
    public void setStepsJson(List<Step> stepsJson) {
        this.stepsJson = stepsJson;
    }

    /**
     * Gets video.
     *
     * @return the video
     */
    public String getVideo() {
        return this.video;
    }

    /**
     * Sets video.
     *
     * @param video the video
     */
    public void setVideo(String video) {
        this.video = video;
    }

    /**
     * Gets doc.
     *
     * @return the doc
     */
    public String getDoc() {
        return doc;
    }

    /**
     * Sets doc.
     *
     * @param doc the doc
     */
    public void setDoc(String doc) {
        this.doc = doc;
    }

    /**
     * Gets har.
     *
     * @return the har
     */
    public String getHar() {
        return har;
    }

    /**
     * Sets har.
     *
     * @param har the har
     */
    public void setHar(String har) {
        this.har = har;
    }

    /**
     * Gets duration seconds.
     *
     * @return the duration seconds
     */
    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    /**
     * Sets duration seconds.
     *
     * @param durationSeconds the duration seconds
     */
    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    /**
     * Gets selenoid id.
     *
     * @return the selenoid id
     */
    public String getSelenoidId() {
        return selenoidId;
    }

    /**
     * Sets selenoid id.
     *
     * @param selenoidId the selenoid id
     */
    public void setSelenoidId(String selenoidId) {
        this.selenoidId = selenoidId;
    }

    /**
     * Gets screen resolution.
     *
     * @return the screen resolution
     */
    public String getScreenResolution() {
        return screenResolution;
    }

    /**
     * Sets screen resolution.
     *
     * @param screenResolution the screen resolution
     */
    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }

    /**
     * Gets browser version.
     *
     * @return the browser version
     */
    public String getBrowserVersion() {
        return browserVersion;
    }

    /**
     * Sets browser version.
     *
     * @param browserVersion the browser version
     */
    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    /**
     * Gets browser.
     *
     * @return the browser
     */
    public Browser getBrowser() {
        return this.browser;
    }

    /**
     * Sets browser.
     *
     * @param browser the browser
     */
    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    /**
     * Gets cas.
     *
     * @return the cas
     */
    public Case getCas() {
        return this.cas;
    }

    /**
     * Sets cas.
     *
     * @param cas the cas
     */
    public void setCas(Case cas) {
        this.cas = cas;
    }
}
