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
@Table(name = "evidence_cases")
public class EvidenceCase {

    @Id
    @Column(name = "evi_case_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eviCaseId;

    private Boolean active;

    @Column(name = "name")
    private String name;

    @Column(name = "order")
    private Long order;

    // bi-directional many-to-many association to Client
    @JsonBackReference(value = "clients")
    @ManyToMany
    @JoinTable(name = "client_evidence_cases", joinColumns = { @JoinColumn(name = "evi_case_id") }, inverseJoinColumns = { @JoinColumn(name = "client_id") })
    private Set<Client> clients;

    public EvidenceCase() {
    }

    public Long getEviCaseId() {
        return eviCaseId;
    }

    public void setEviCaseId(Long eviCaseId) {
        this.eviCaseId = eviCaseId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }
}
