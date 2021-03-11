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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The type Selenoid session.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SelenoidSession {

    private String id;
    private SelenoidCaps caps;

    /**
     * Instantiates a new Selenoid session.
     */
    public SelenoidSession() {
        super();
    }

    @Override
    public String toString() {
        return "SelenoidSession [id=" + id + ", caps=" + caps + "]";
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
     * Gets caps.
     *
     * @return the caps
     */
    public SelenoidCaps getCaps() {
        return caps;
    }

    /**
     * Sets caps.
     *
     * @param caps the caps
     */
    public void setCaps(SelenoidCaps caps) {
        this.caps = caps;
    }

}
