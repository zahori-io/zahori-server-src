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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Execution.
 */
@Entity
@Table(name = "executions")
@NamedQuery(name = "Execution.findAll", query = "SELECT e FROM Execution e")
public class Execution implements Serializable {

    private static final long serialVersionUID = -8470048586494011954L;

    @Id
    @Column(name = "execution_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;

    private String date;

    private String name;

    private String status;

    @Column(name = "total_failed")
    private Integer totalFailed;

    @Column(name = "total_passed")
    private Integer totalPassed;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @ManyToOne
    @JoinColumn(name = "configuration_id")
    private Configuration configuration;

    // uni-directional many-to-one association to CasesExecution
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "execution_id", nullable = false)
    private List<CaseExecution> casesExecutions;

    // bi-directional many-to-one association to processSchedule
    @ManyToOne
    @JoinColumn(name = "process_schedule_id")
    private ProcessSchedule processSchedule;

    // bi-directional many-to-one association to Process
    @JsonBackReference(value = "process")
    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;

    // bi-directional many-to-one association to User
    //	@ManyToOne
    //	@JoinColumn(name="user_id")
    //	private User user;

    @Column(name = "jenkins_build")
    private String jenkinsBuild;

    /**
     * Instantiates a new Execution.
     */
    public Execution() {
    }

    /**
     * Gets execution id.
     *
     * @return the execution id
     */
    public Long getExecutionId() {
        return this.executionId;
    }

    /**
     * Sets execution id.
     *
     * @param executionId the execution id
     */
    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
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
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets total failed.
     *
     * @return the total failed
     */
    public Integer getTotalFailed() {
        return this.totalFailed;
    }

    /**
     * Sets total failed.
     *
     * @param totalFailed the total failed
     */
    public void setTotalFailed(Integer totalFailed) {
        this.totalFailed = totalFailed;
    }

    /**
     * Gets total passed.
     *
     * @return the total passed
     */
    public Integer getTotalPassed() {
        return this.totalPassed;
    }

    /**
     * Sets total passed.
     *
     * @param totalPassed the total passed
     */
    public void setTotalPassed(Integer totalPassed) {
        this.totalPassed = totalPassed;
    }

    /**
     * Gets duration seconds.
     *
     * @return the duration seconds
     */
    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    /**
     * Sets duration seconds.
     *
     * @param durationSeconds the duration seconds
     */
    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    /**
     * Gets cases executions.
     *
     * @return the cases executions
     */
    public List<CaseExecution> getCasesExecutions() {
        return this.casesExecutions;
    }

    /**
     * Sets cases executions.
     *
     * @param casesExecutions the cases executions
     */
    public void setCasesExecutions(List<CaseExecution> casesExecutions) {
        this.casesExecutions = casesExecutions;
    }

    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * Sets configuration.
     *
     * @param configuration the configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ProcessSchedule getProcessSchedule() {
        return processSchedule;
    }

    public void setProcessSchedule(ProcessSchedule processSchedule) {
        this.processSchedule = processSchedule;
    }

    /**
     * Gets process.
     *
     * @return the process
     */
    public Process getProcess() {
        return this.process;
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
     * Gets jenkins build.
     *
     * @return the jenkins build
     */
    public String getJenkinsBuild() {
        return jenkinsBuild;
    }

    /**
     * Sets jenkins build.
     *
     * @param jenkinsBuild the jenkins build
     */
    public void setJenkinsBuild(String jenkinsBuild) {
        this.jenkinsBuild = jenkinsBuild;
    }

    //	public User getUser() {
    //		return this.user;
    //	}
    //
    //	public void setUser(User user) {
    //		this.user = user;
    //	}

}
