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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Test repository.
 */
@Entity
@Table(name = "test_repositories")
//@NamedQuery(name = "TestRepository.findAll", query = "SELECT t FROM TestRepository t")
public class TestRepository implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "test_repo_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testRepoId;

    private Boolean active;

    @Column(name = "name")
    private String name;

    @Column(name = "\"order\"")
    private Long order;

    // bi-directional many-to-one association to ClientTestRepo
    @OneToMany(mappedBy = "testRepository")
    private Set<ClientTestRepo> clientTestRepos;

    // bi-directional many-to-many association to Configuration
    @JsonBackReference(value = "configurations")
    @ManyToMany(mappedBy = "testRepositories")
    private Set<Configuration> configurations;

    /**
     * Instantiates a new Test repository.
     */
    public TestRepository() {
    }

    /**
     * Gets test repo id.
     *
     * @return the test repo id
     */
    public Long getTestRepoId() {
        return this.testRepoId;
    }

    /**
     * Sets test repo id.
     *
     * @param testRepoId the test repo id
     */
    public void setTestRepoId(Long testRepoId) {
        this.testRepoId = testRepoId;
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
     * Gets client test repos.
     *
     * @return the client test repos
     */
    public Set<ClientTestRepo> getClientTestRepos() {
        return this.clientTestRepos;
    }

    /**
     * Sets client test repos.
     *
     * @param clientTestRepos the client test repos
     */
    public void setClientTestRepos(Set<ClientTestRepo> clientTestRepos) {
        this.clientTestRepos = clientTestRepos;
    }

    /**
     * Add client test repo client test repo.
     *
     * @param clientTestRepo the client test repo
     * @return the client test repo
     */
    public ClientTestRepo addClientTestRepo(ClientTestRepo clientTestRepo) {
        getClientTestRepos().add(clientTestRepo);
        clientTestRepo.setTestRepository(this);

        return clientTestRepo;
    }

    /**
     * Remove client test repo client test repo.
     *
     * @param clientTestRepo the client test repo
     * @return the client test repo
     */
    public ClientTestRepo removeClientTestRepo(ClientTestRepo clientTestRepo) {
        getClientTestRepos().remove(clientTestRepo);
        clientTestRepo.setTestRepository(null);

        return clientTestRepo;
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
