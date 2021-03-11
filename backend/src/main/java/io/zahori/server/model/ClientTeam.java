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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Client team.
 */
@Entity
@Table(name = "client_teams")
// @NamedQuery(name = "ClientTeam.findAll", query = "SELECT c FROM ClientTeam
// c")
public class ClientTeam implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ClientTeamPK id;

    @Column(name = "admin")
    private Boolean admin;

    @Column(name = "favorite")
    private Boolean favorite;

    @Column(name = "name")
    private String name;

    // bi-directional many-to-one association to Client
    // @JsonIgnore
    @JsonBackReference(value = "client")
    @ManyToOne
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    // bi-directional many-to-one association to Process
    @OneToMany(mappedBy = "clientTeam", fetch = FetchType.EAGER)
    @OrderBy("processId ASC")
    private Set<Process> processes;

    /**
     * Instantiates a new Client team.
     */
    public ClientTeam() {
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public ClientTeamPK getId() {
        return this.id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(ClientTeamPK id) {
        this.id = id;
    }

    /**
     * Gets admin.
     *
     * @return the admin
     */
    public Boolean getAdmin() {
        return this.admin;
    }

    /**
     * Sets admin.
     *
     * @param admin the admin
     */
    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    /**
     * Gets favorite.
     *
     * @return the favorite
     */
    public Boolean getFavorite() {
        return this.favorite;
    }

    /**
     * Sets favorite.
     *
     * @param favorite the favorite
     */
    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets client.
     *
     * @return the client
     */
    public Client getClient() {
        return this.client;
    }

    /**
     * Sets client.
     *
     * @param client the client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Gets processes.
     *
     * @return the processes
     */
    public Set<Process> getProcesses() {
        return this.processes;
    }

    /**
     * Sets processes.
     *
     * @param processes the processes
     */
    public void setProcesses(Set<Process> processes) {
        this.processes = processes;
    }

}
