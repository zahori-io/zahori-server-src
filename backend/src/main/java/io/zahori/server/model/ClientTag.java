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

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The type Client tag.
 */
@Entity
@Table(name = "client_tags")
@NamedQuery(name = "ClientTag.findAll", query = "SELECT c FROM ClientTag c")
public class ClientTag implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    private Boolean active;
    
    // Note scape \" characters due to order is a JPA reserved word
    @Column(name = "\"order\"")
    private Long order;

    @Column(name = "name")
    private String name;
    
    @Column(name = "color")
    private String color;

    // bi-directional many-to-many association to Cas
    @ManyToMany(mappedBy = "clientTags")
    @JsonBackReference(value = "clientTags")
    private List<Case> cases;

    // bi-directional many-to-one association to Client
    @JsonBackReference(value = "client")
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @JsonBackReference(value = "process")
    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;
        
    /**
     * Instantiates a new Client tag.
     */
    public ClientTag() {
    }

    /**
     * Gets tag id.
     *
     * @return the tag id
     */
    public Long getTagId() {
        return this.tagId;
    }

    /**
     * Sets tag id.
     *
     * @param tagId the tag id
     */
    public void setTagId(Long tagId) {
        this.tagId = tagId;
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
     * Gets name.
     *
     * @return the name
     */
    public String getcolor() {
        return this.color;
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setcolor(String color) {
        this.color = color;
    }
    /**
     * Gets cases.
     *
     * @return the cases
     */
    public List<Case> getCases() {
        return this.cases;
    }

    /**
     * Sets cases.
     *
     * @param cases the cases
     */
    public void setCases(List<Case> cases) {
        this.cases = cases;
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
    
}
