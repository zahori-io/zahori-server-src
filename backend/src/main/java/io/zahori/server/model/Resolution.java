package io.zahori.server.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

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

@Entity
@Table(name = "resolutions")
public class Resolution implements Serializable {

    @Id
    @Column(name = "resolution_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resolutionId;

    private Boolean active;

    // Note scape \" characters due to order is a JPA reserved word
    @Column(name = "\"order\"")
    private Long order;

    // bi-directional many-to-one association to Client
    @JsonBackReference(value = "client")
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @JsonBackReference(value = "process")
    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "name")
    private String name;

    public Long getResolutionId() {
        return resolutionId;
    }

    /**
     * @param resolutionId
     */
    public void setResolutionId(Long resolutionId) {
        this.resolutionId = resolutionId;
    }

    /**
     * @return Is active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return Order position
     */
    public Long getOrder() {
        return order;
    }

    /**
     * @param order
     */
    public void setOrder(Long order) {
        this.order = order;
    }

    /**
     * @return client id
     */
    public Client getClient() {
        return client;
    }

    /**
     * @param client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @return Process Id
     */
    public Process getProcess() {
        return process;
    }

    /**
     * @param process
     */
    public void setProcess(Process process) {
        this.process = process;
    }

    /**
     * @return Screen witdh
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return Screen height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
