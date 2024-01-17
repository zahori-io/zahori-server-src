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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The type Process.
 */
@Entity
@Table(name = "processes")
// @NamedQuery(name = "Process.findAll", query = "SELECT p FROM Process p")
public class Process implements Serializable {

    private static final long serialVersionUID = -1323398708606466689L;

    @Id
    @Column(name = "process_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "proc_type_id")
    private ProcessType processType;

    // bi-directional many-to-one association to Client
    @JsonBackReference(value = "client")
    @ManyToOne
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    // bi-directional many-to-one association to ClientTeam
    @JsonBackReference(value = "clientTeam")
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "client_id", referencedColumnName = "client_id"), @JoinColumn(name = "team_id", referencedColumnName = "team_id") })
    private ClientTeam clientTeam;

    // bi-directional many-to-one association to Execution
    //    @OneToMany(mappedBy = "process", fetch = FetchType.LAZY)
    //    @OrderBy("executionId DESC")
    //    private List<Execution> executions;

    @JsonIgnore
    @Column(name = "jenkins_token")
    private String jenkinsToken;

    @JsonIgnore
    @Column(name = "jenkins_job")
    private String jenkinsJob;

    @JsonIgnore
    @Column(name = "jenkins_job_parameter_name")
    private String jenkinsJobParameterName;

    /**
     * Instantiates a new Process.
     */
    public Process() {
    }

    /**
     * Gets process id.
     *
     * @return the process id
     */
    public Long getProcessId() {
        return this.processId;
    }

    /**
     * Sets process id.
     *
     * @param processId the process id
     */
    public void setProcessId(Long processId) {
        this.processId = processId;
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

    // public Long getClientId() {
    // return clientId;
    // }
    //
    // public void setClientId(Long clientId) {
    // this.clientId = clientId;
    // }

    // public Long getTeamId() {
    // return teamId;
    // }
    //
    // public void setTeamId(Long teamId) {
    // this.teamId = teamId;
    // }

    /**
     * Gets client team.
     *
     * @return the client team
     */
    public ClientTeam getClientTeam() {
        return clientTeam;
    }

    /**
     * Gets process type.
     *
     * @return the process type
     */
    public ProcessType getProcessType() {
        return processType;
    }

    /**
     * Sets process type.
     *
     * @param processType the process type
     */
    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    /**
     * Gets serialversionuid.
     *
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * Sets client team.
     *
     * @param clientTeam the client team
     */
    public void setClientTeam(ClientTeam clientTeam) {
        this.clientTeam = clientTeam;
    }

    /**
     * Gets client.
     *
     * @return the client
     */
    public Client getClient() {
        return client;
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
     * Gets executions.
     *
     * @return the executions
     */
    //public List<Execution> getExecutions() {
    //    return this.executions;
    //}

    /**
     * Sets executions.
     *
     * @param executions the executions
     */
    //    public void setExecutions(List<Execution> executions) {
    //        this.executions = executions;
    //    }

    /**
     * Gets jenkins token.
     *
     * @return the jenkins token
     */
    public String getJenkinsToken() {
        return jenkinsToken;
    }

    /**
     * Sets jenkins token.
     *
     * @param jenkinsToken the jenkins token
     */
    public void setJenkinsToken(String jenkinsToken) {
        this.jenkinsToken = jenkinsToken;
    }

    /**
     * Gets jenkins job.
     *
     * @return the jenkins job
     */
    public String getJenkinsJob() {
        return jenkinsJob;
    }

    /**
     * Sets jenkins job.
     *
     * @param jenkinsJob the jenkins job
     */
    public void setJenkinsJob(String jenkinsJob) {
        this.jenkinsJob = jenkinsJob;
    }

    /**
     * Gets jenkins job parameter name.
     *
     * @return the jenkins job parameter name
     */
    public String getJenkinsJobParameterName() {
        return jenkinsJobParameterName;
    }

    /**
     * Sets jenkins job parameter name.
     *
     * @param jenkinsJobParameterName the jenkins job parameter name
     */
    public void setJenkinsJobParameterName(String jenkinsJobParameterName) {
        this.jenkinsJobParameterName = jenkinsJobParameterName;
    }

}
