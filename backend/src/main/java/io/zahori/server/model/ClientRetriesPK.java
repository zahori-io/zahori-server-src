package io.zahori.server.model;

import java.io.Serializable;
import java.util.Objects;

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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * The type Client team pk.
 */
@Embeddable
public class ClientRetriesPK implements Serializable {

    private static final long serialVersionUID = -8803153208831143322L;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "retry_id")
    private Integer retryId;

    /**
     * Instantiates a new Client team pk.
     */
    public ClientRetriesPK() {
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Integer getRetryId() {
        return retryId;
    }

    public void setRetryId(Integer retryId) {
        this.retryId = retryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClientRetriesPK that = (ClientRetriesPK) o;
        return clientId.equals(that.clientId) && retryId.equals(that.retryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, retryId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientRetriesPK{");
        sb.append("clientId=").append(clientId);
        sb.append(", retryId=").append(retryId);
        sb.append('}');
        return sb.toString();
    }
}