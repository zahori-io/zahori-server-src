package io.zahori.server.model;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Client environment.
 */
@Entity
@Table(name = "client_environments")
//@NamedQuery(name = "ClientEnvironment.findAll", query = "SELECT c FROM ClientEnvironment c")
public class ClientEnvironment implements Serializable {

    private static final long serialVersionUID = 5742276307732716759L;

    @Id
    @Column(name = "environment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long environmentId;

    private Boolean active;

    @Column(name = "name")
    private String name;

    // Note scape \" characters due to order is a JPA reserved word
    @Column(name = "\"order\"")
    private Long order;

    @Column(name = "url")
    private String url;

    // bi-directional many-to-one association to Client
    @JsonBackReference(value = "client")
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @JsonBackReference(value = "process")
    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;

    // bi-directional many-to-one association to Configuration
    @JsonBackReference(value = "configurations")
    @OneToMany(mappedBy = "clientEnvironment")
    private Set<Configuration> configurations;

    /**
     * Instantiates a new Client environment.
     */
    public ClientEnvironment() {
    }

    /**
     * Gets environment id.
     *
     * @return the environment id
     */
    public Long getEnvironmentId() {
        return this.environmentId;
    }

    /**
     * Sets environment id.
     *
     * @param environmentId the environment id
     */
    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
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
     * Gets order.
     *
     * @return the order
     */
    public Long getOrder() {
        return this.order;
    }

    /**
     * Sets order.
     *
     * @param order the order
     */
    public void setOrder(Long order) {
        this.order = order;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
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
     * Gets process.
     *
     * @return the process
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Sets process.
     *
     * @param process the process
     */
    public void setProcess(Process process) {
        this.process = process;
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

}
