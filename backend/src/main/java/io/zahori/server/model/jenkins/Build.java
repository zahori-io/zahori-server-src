package io.zahori.server.model.jenkins;

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
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * The type Build.
 */
public class Build {

    private static final String XLSX = "xlsx";
    private static final String XLS = "xls";
    private static final String DOCX = "docx";

    private boolean building;
    private int number;
    private String id;
    private String result;
    private long timestamp;
    private String url;
    private String urlExcelOut;
    private String urlWordOut;
    private List<Artifact> artifacts = new ArrayList<>();

    /**
     * Instantiates a new Build.
     */
    public Build() {

    }

    /**
     * Instantiates a new Build.
     *
     * @param building  the building
     * @param number    the number
     * @param id        the id
     * @param result    the result
     * @param timestamp the timestamp
     * @param url       the url
     * @param artifacts the artifacts
     */
    public Build(boolean building, int number, String id, String result, long timestamp, String url, List<Artifact> artifacts) {
        super();
        this.building = building;
        this.number = number;
        this.id = id;
        this.result = result;
        this.timestamp = timestamp;
        this.url = url;
        this.artifacts = artifacts;
    }

    @Override
    public String toString() {
        return "Build [building=" + building + ", number=" + number + ", id=" + id + ", result=" + result + ", timestamp=" + timestamp + ", url=" + url
                + ", urlExcelOut=" + urlExcelOut + ", urlWordOut=" + urlWordOut + ", artifacts=" + artifacts + "]";
    }

    /**
     * Is building boolean.
     *
     * @return the boolean
     */
    public boolean isBuilding() {
        return building;
    }

    /**
     * Sets building.
     *
     * @param building the building
     */
    public void setBuilding(boolean building) {
        this.building = building;
    }

    /**
     * Gets number.
     *
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets number.
     *
     * @param number the number
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets artifacts.
     *
     * @return the artifacts
     */
    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    /**
     * Sets artifacts.
     *
     * @param artifacts the artifacts
     */
    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    /**
     * Add artifact.
     *
     * @param artifact the artifact
     */
    public void addArtifact(Artifact artifact) {
        this.artifacts.add(artifact);
    }

    /**
     * Gets url excel out.
     *
     * @return the url excel out
     */
    public String getUrlExcelOut() {
        return urlExcelOut;
    }

    /**
     * Sets url excel out.
     *
     * @param urlExcelOut the url excel out
     */
    public void setUrlExcelOut(String urlExcelOut) {
        this.urlExcelOut = urlExcelOut;
    }

    /**
     * Sets url excel out.
     */
    public void setUrlExcelOut() {
        for (Artifact artifact : this.artifacts) {
            if (StringUtils.startsWith(artifact.getFileName(), "output-")
                    && (StringUtils.endsWith(artifact.getFileName(), XLSX) || StringUtils.endsWith(artifact.getFileName(), XLS))) {
                urlExcelOut = url + "artifact/" + artifact.getRelativePath();
            }
        }
    }

    /**
     * Gets url word out.
     *
     * @return the url word out
     */
    public String getUrlWordOut() {
        return urlWordOut;
    }

    /**
     * Sets url word out.
     *
     * @param urlWordOut the url word out
     */
    public void setUrlWordOut(String urlWordOut) {
        this.urlWordOut = urlWordOut;
    }

    /**
     * Sets url word out.
     */
    public void setUrlWordOut() {
        for (Artifact artifact : this.artifacts) {
            if (StringUtils.startsWith(artifact.getFileName(), "output-") && StringUtils.endsWith(artifact.getFileName(), DOCX)) {
                urlWordOut = url + "artifact/" + artifact.getRelativePath();
            }
        }
    }

}
