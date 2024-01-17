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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * The type Client test repo pk.
 */
@Embeddable
public class ClientTestRepoPK implements Serializable {

    private static final long serialVersionUID = -6494104813807221747L;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "test_repo_id")
    private Long testRepoId;

    /**
     * Instantiates a new Client test repo pk.
     */
    public ClientTestRepoPK() {
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
     * Gets test repo id.
     *
     * @return the test repo id
     */
    public Long getTestRepoId() {
        return this.testRepoId;
    }

    /**
     * Sets test repo id.
     *
     * @param testRepoId the test repo id
     */
    public void setTestRepoId(Long testRepoId) {
        this.testRepoId = testRepoId;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClientTestRepoPK)) {
            return false;
        }
        ClientTestRepoPK castOther = (ClientTestRepoPK) other;
        return this.clientId.equals(castOther.clientId) && this.testRepoId.equals(castOther.testRepoId);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.clientId.hashCode();
        hash = hash * prime + this.testRepoId.hashCode();

        return hash;
    }
}
