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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Configuration.
 */
@Entity
@Table(name = "configurations")
//@NamedQuery(name="Configuration.findAll", query="SELECT c FROM Configuration c")
public class Configuration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "configuration_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long configurationId;

    private Boolean active;

    private String name;

    @Column(name = "upload_results")
    private Boolean uploadResults;

    // bi-directional many-to-one association to ClientEnvironment
    @ManyToOne
    @JoinColumn(name = "environment_id")
    private ClientEnvironment clientEnvironment;

    // bi-directional many-to-one association to EvidenceCas
//    @ManyToOne
//    @JoinColumn(name = "evi_case_id")
//    private EvidenceCas evidenceCas;

    // bi-directional many-to-one association to Process
    @JsonBackReference(value = "process")
    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;

    // bi-directional many-to-one association to Retry
    @ManyToOne
    @JoinColumn(name = "retry_id")
    private Retry retry;

    // bi-directional many-to-many association to EvidenceType
    @ManyToMany
    @JoinTable(name = "configurations_evidence_types", joinColumns = { @JoinColumn(name = "configuration_id") }, inverseJoinColumns = {
            @JoinColumn(name = "evi_type_id") })
    private Set<EvidenceType> evidenceTypes;

    // bi-directional many-to-many association to TestRepository
    @ManyToMany
    @JoinTable(name = "configurations_test_repositories", joinColumns = { @JoinColumn(name = "configuration_id") }, inverseJoinColumns = {
            @JoinColumn(name = "test_repo_id") })
    private Set<TestRepository> testRepositories;

    /**
     * Instantiates a new Configuration.
     */
    public Configuration() {
    }

    /**
     * Gets configuration id.
     *
     * @return the configuration id
     */
    public Long getConfigurationId() {
        return this.configurationId;
    }

    /**
     * Sets configuration id.
     *
     * @param configurationId the configuration id
     */
    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
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
     * Gets upload results.
     *
     * @return the upload results
     */
    public Boolean getUploadResults() {
        return this.uploadResults;
    }

    /**
     * Sets upload results.
     *
     * @param uploadResults the upload results
     */
    public void setUploadResults(Boolean uploadResults) {
        this.uploadResults = uploadResults;
    }

    /**
     * Gets client environment.
     *
     * @return the client environment
     */
    public ClientEnvironment getClientEnvironment() {
        return this.clientEnvironment;
    }

    /**
     * Sets client environment.
     *
     * @param clientEnvironment the client environment
     */
    public void setClientEnvironment(ClientEnvironment clientEnvironment) {
        this.clientEnvironment = clientEnvironment;
    }

//    public EvidenceCas getEvidenceCas() {
//        return this.evidenceCas;
//    }
//
//    public void setEvidenceCas(EvidenceCas evidenceCas) {
//        this.evidenceCas = evidenceCas;
//    }

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
     * Gets retry.
     *
     * @return the retry
     */
    public Retry getRetry() {
        return this.retry;
    }

    /**
     * Sets retry.
     *
     * @param retry the retry
     */
    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    /**
     * Gets evidence types.
     *
     * @return the evidence types
     */
    public Set<EvidenceType> getEvidenceTypes() {
        return this.evidenceTypes;
    }

    /**
     * Sets evidence types.
     *
     * @param evidenceTypes the evidence types
     */
    public void setEvidenceTypes(Set<EvidenceType> evidenceTypes) {
        this.evidenceTypes = evidenceTypes;
    }

    /**
     * Gets test repositories.
     *
     * @return the test repositories
     */
    public Set<TestRepository> getTestRepositories() {
        return this.testRepositories;
    }

    /**
     * Sets test repositories.
     *
     * @param testRepositories the test repositories
     */
    public void setTestRepositories(Set<TestRepository> testRepositories) {
        this.testRepositories = testRepositories;
    }

}
