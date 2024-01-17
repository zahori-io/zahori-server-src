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
 * The type Retry.
 */
@Entity
@Table(name = "retries")
//@NamedQuery(name = "Retry.findAll", query = "SELECT r FROM Retry r")
public class Retry implements Serializable {

    private static final long serialVersionUID = 4854155657321806908L;

    @Id
    @Column(name = "retry_id")
    private Integer retryId;

    private Boolean active;

    //bi-directional many-to-many association to Client
    @JsonBackReference(value = "client")
    @ManyToMany
    @JoinTable(name = "client_retries", joinColumns = { @JoinColumn(name = "retry_id") }, inverseJoinColumns = { @JoinColumn(name = "client_id") })
    private Set<Client> clients;

    //bi-directional many-to-one association to Configuration
    @JsonBackReference(value = "configurations")
    @OneToMany(mappedBy = "retry")
    private Set<Configuration> configurations;

    public Retry() {
    }

    public Integer getRetryId() {
        return this.retryId;
    }

    public void setRetryId(Integer retryId) {
        this.retryId = retryId;
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

    public Configuration addConfiguration(Configuration configuration) {
        getConfigurations().add(configuration);
        configuration.setRetry(this);

        return configuration;
    }

    public Configuration removeConfiguration(Configuration configuration) {
        getConfigurations().remove(configuration);
        configuration.setRetry(null);

        return configuration;
    }

}