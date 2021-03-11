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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Retry.
 */
@Entity
@Table(name = "retries")
//@NamedQuery(name = "Retry.findAll", query = "SELECT r FROM Retry r")
public class Retry implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "retry_id")
    private Integer retryId;

    private Boolean active;

    // bi-directional many-to-many association to Client
    @JsonBackReference(value = "clients")
    @ManyToMany
    @JoinTable(name = "client_retries", joinColumns = { @JoinColumn(name = "retry_id") }, inverseJoinColumns = { @JoinColumn(name = "client_id") })
    private Set<Client> clients;

    // bi-directional many-to-one association to Configuration
    @JsonBackReference(value = "configurations")
    @OneToMany(mappedBy = "retry")
    private Set<Configuration> configurations;

    /**
     * Instantiates a new Retry.
     */
    public Retry() {
    }

    /**
     * Gets retry id.
     *
     * @return the retry id
     */
    public Integer getRetryId() {
        return this.retryId;
    }

    /**
     * Sets retry id.
     *
     * @param retryId the retry id
     */
    public void setRetryId(Integer retryId) {
        this.retryId = retryId;
    }

    /**
     * Gets active.
     *
     * @return the active
     */
    public Boolean getActive() {
        return this.active;
    }

    /**
     * Sets active.
     *
     * @param active the active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Gets clients.
     *
     * @return the clients
     */
    public Set<Client> getClients() {
        return this.clients;
    }

    /**
     * Sets clients.
     *
     * @param clients the clients
     */
    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    /**
     * Gets configurations.
     *
     * @return the configurations
     */
    public Set<Configuration> getConfigurations() {
        return this.configurations;
    }

    /**
     * Sets configurations.
     *
     * @param configurations the configurations
     */
    public void setConfigurations(Set<Configuration> configurations) {
        this.configurations = configurations;
    }

    /**
     * Add configuration configuration.
     *
     * @param configuration the configuration
     * @return the configuration
     */
    public Configuration addConfiguration(Configuration configuration) {
        getConfigurations().add(configuration);
        configuration.setRetry(this);

        return configuration;
    }

    /**
     * Remove configuration configuration.
     *
     * @param configuration the configuration
     * @return the configuration
     */
    public Configuration removeConfiguration(Configuration configuration) {
        getConfigurations().remove(configuration);
        configuration.setRetry(null);

        return configuration;
    }

}
