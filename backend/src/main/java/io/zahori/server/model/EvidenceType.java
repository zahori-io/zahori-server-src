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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Evidence type.
 */
@Entity
@Table(name = "evidence_types")
//@NamedQuery(name = "EvidenceType.findAll", query = "SELECT e FROM EvidenceType e")
public class EvidenceType implements Serializable {

    private static final long serialVersionUID = -4801642444331577192L;

    @Id
    @Column(name = "evi_type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eviTypeId;

    private Boolean active;

    @Column(name = "name")
    private String name;

    @Column(name = "order")
    private Long order;

    // bi-directional many-to-many association to Client
    @JsonBackReference(value = "clients")
    @ManyToMany
    @JoinTable(name = "client_evidence_types", joinColumns = { @JoinColumn(name = "evi_type_id") }, inverseJoinColumns = { @JoinColumn(name = "client_id") })
    private Set<Client> clients;

    // bi-directional many-to-many association to Configuration
    @JsonBackReference(value = "configurations")
    @ManyToMany(mappedBy = "evidenceTypes")
    private Set<Configuration> configurations;

    /**
     * Instantiates a new Evidence type.
     */
    public EvidenceType() {
    }

    /**
     * Gets evi type id.
     *
     * @return the evi type id
     */
    public Long getEviTypeId() {
        return this.eviTypeId;
    }

    /**
     * Sets evi type id.
     *
     * @param eviTypeId the evi type id
     */
    public void setEviTypeId(Long eviTypeId) {
        this.eviTypeId = eviTypeId;
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

}
