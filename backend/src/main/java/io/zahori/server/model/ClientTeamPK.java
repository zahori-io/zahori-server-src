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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The type Client team pk.
 */
@Embeddable
public class ClientTeamPK implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "team_id")
    private Long teamId;

    /**
     * Instantiates a new Client team pk.
     */
    public ClientTeamPK() {
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public Long getClientId() {
        return this.clientId;
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
        return this.teamId;
    }

    /**
     * Sets team id.
     *
     * @param teamId the team id
     */
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClientTeamPK)) {
            return false;
        }
        ClientTeamPK castOther = (ClientTeamPK) other;
        return this.clientId.equals(castOther.clientId) && this.teamId.equals(castOther.teamId);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.clientId.hashCode();
        hash = hash * prime + this.teamId.hashCode();

        return hash;
    }
}
