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
import java.sql.Time;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The type Periodic execution.
 */
@Entity
@Table(name = "periodic_executions")
@NamedQuery(name = "PeriodicExecution.findAll", query = "SELECT p FROM PeriodicExecution p")
public class PeriodicExecution implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "periodic_execution_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long periodicExecutionId;

    private Boolean active;

    private String days;

    private Time time;

    // bi-directional many-to-one association to Execution
    @OneToMany(mappedBy = "periodicExecution")
    private List<Execution> executions;

    /**
     * Instantiates a new Periodic execution.
     */
    public PeriodicExecution() {
    }

    /**
     * Gets periodic execution id.
     *
     * @return the periodic execution id
     */
    public Long getPeriodicExecutionId() {
        return this.periodicExecutionId;
    }

    /**
     * Sets periodic execution id.
     *
     * @param periodicExecutionId the periodic execution id
     */
    public void setPeriodicExecutionId(Long periodicExecutionId) {
        this.periodicExecutionId = periodicExecutionId;
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
     * Gets days.
     *
     * @return the days
     */
    public String getDays() {
        return this.days;
    }

    /**
     * Sets days.
     *
     * @param days the days
     */
    public void setDays(String days) {
        this.days = days;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public Time getTime() {
        return this.time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(Time time) {
        this.time = time;
    }

    /**
     * Gets executions.
     *
     * @return the executions
     */
    public List<Execution> getExecutions() {
        return this.executions;
    }

    /**
     * Sets executions.
     *
     * @param executions the executions
     */
    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }

    /**
     * Add execution execution.
     *
     * @param execution the execution
     * @return the execution
     */
    public Execution addExecution(Execution execution) {
        getExecutions().add(execution);
        execution.setPeriodicExecution(this);

        return execution;
    }

    /**
     * Remove execution execution.
     *
     * @param execution the execution
     * @return the execution
     */
    public Execution removeExecution(Execution execution) {
        getExecutions().remove(execution);
        execution.setPeriodicExecution(null);

        return execution;
    }

}
