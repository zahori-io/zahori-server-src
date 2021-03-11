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

/**
 * The type Jenkins.
 */
public class Jenkins {

    private String jobUrl;
    private String jobParameter;
    private String authToken;

    /**
     * Instantiates a new Jenkins.
     */
    public Jenkins() {
        super();
    }

    @Override
    public String toString() {
        return "Jenkins [jobUrl=" + jobUrl + ", jobParameter=" + jobParameter + ", authToken=*****]";
    }

    /**
     * Gets job url.
     *
     * @return the job url
     */
    public String getJobUrl() {
        return jobUrl;
    }

    /**
     * Sets job url.
     *
     * @param jobUrl the job url
     */
    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    /**
     * Gets job parameter.
     *
     * @return the job parameter
     */
    public String getJobParameter() {
        return jobParameter;
    }

    /**
     * Sets job parameter.
     *
     * @param jobParameter the job parameter
     */
    public void setJobParameter(String jobParameter) {
        this.jobParameter = jobParameter;
    }

    /**
     * Gets auth token.
     *
     * @return the auth token
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets auth token.
     *
     * @param authToken the auth token
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
