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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The persistent class for the timeouts database table.
 * 
 */
@Entity
@Table(name = "timeouts")
public class Timeout implements Serializable {

    private static final long serialVersionUID = -8108674227561269699L;

    @Id
    @Column(name = "timeout_id")
    private Long timeoutId;

    private Boolean active;

    //bi-directional many-to-many association to Client
    @JsonBackReference(value = "client")
    @ManyToMany
    @JoinTable(name = "client_timeouts", joinColumns = { @JoinColumn(name = "timeout_id") }, inverseJoinColumns = { @JoinColumn(name = "client_id") })
    private Set<Client> clients;

    //bi-directional many-to-one association to Configuration
    @JsonBackReference(value = "configurations")
    @OneToMany(mappedBy = "timeout")
    private Set<Configuration> configurations;

    public Timeout() {
    }

    public Long getTimeoutId() {
        return this.timeoutId;
    }

    public void setTimeoutId(Long timeoutId) {
        this.timeoutId = timeoutId;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Client> getClients() {
        return this.clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public Set<Configuration> getConfigurations() {
        return this.configurations;
    }

    public void setConfigurations(Set<Configuration> configurations) {
        this.configurations = configurations;
    }

}
