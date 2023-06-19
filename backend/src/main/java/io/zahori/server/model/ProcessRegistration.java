package io.zahori.server.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

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
 * The type Process registration.
 */
public class ProcessRegistration {

    private Long processId;
    private String name;
    private Long clientId;
    private Long teamId;
    private Long procTypeId;

    @Override
    public String toString() {
        return "ProcessRegistration [name=" + name + ", clientId=" + clientId + ", teamId=" + teamId + ", procTypeId=" + procTypeId + "]";
    }

    /**
     * Gets process id.
     *
     * @return the process id
     */
    public Long getProcessId() {
        return processId;
    }

    /**
     * Sets process id.
     *
     * @param processId the process id
     */
    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        if (name == null){
            name = "";
        }
        
        this.name = name.trim();
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public Long getClientId() {
        return clientId;
    }

    /**
     * Sets client id.
     *
     * @param clientId the client id
     */
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets team id.
     *
     * @return the team id
     */
    public Long getTeamId() {
        return teamId;
    }

    /**
     * Sets team id.
     *
     * @param teamId the team id
     */
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    /**
     * Gets proc type id.
     *
     * @return the proc type id
     */
    public Long getProcTypeId() {
        return procTypeId;
    }

    /**
     * Sets proc type id.
     *
     * @param procTypeId the proc type id
     */
    public void setProcTypeId(Long procTypeId) {
        this.procTypeId = procTypeId;
    }

    /*
        Zahori process name:
        - is case sensitive
        - must not be empty
        - must start with a letter,
        - must end with a letter or digit
        - and have as interior characters only letters, digits, blank spaces and hyphens
    */
    public boolean hasValidName() {
        final String regExp = "^[a-zA-Z](([a-zA-Z0-9\\-\\s])*[a-zA-Z0-9])*$";
        
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(StringUtils.trim(name));
        return matcher.matches();
    }
}
