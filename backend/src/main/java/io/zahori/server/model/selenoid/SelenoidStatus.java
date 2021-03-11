package io.zahori.server.model.selenoid;

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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The type Selenoid status.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SelenoidStatus {

    private Map<String, SelenoidSession> sessions = new HashMap<>();
    private SelenoidState state;

    /**
     * Instantiates a new Selenoid status.
     */
    public SelenoidStatus() {
        super();
    }

    @Override
    public String toString() {
        return "SelenoidStatus [sessions=" + sessions + ", state=" + state + "]";
    }

    /**
     * Gets sessions.
     *
     * @return the sessions
     */
    public Map<String, SelenoidSession> getSessions() {
        return sessions;
    }

    /**
     * Sets sessions.
     *
     * @param sessions the sessions
     */
    public void setSessions(Map<String, SelenoidSession> sessions) {
        this.sessions = sessions;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public SelenoidState getState() {
        return state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(SelenoidState state) {
        this.state = state;
    }

}
