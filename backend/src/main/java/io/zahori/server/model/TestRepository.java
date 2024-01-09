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
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The type Test repository.
 */
@Entity
@Table(name = "test_repositories")
//@NamedQuery(name = "TestRepository.findAll", query = "SELECT t FROM TestRepository t")
public class TestRepository implements Serializable {

    private static final long serialVersionUID = 121192983245034817L;

    @Id
    @Column(name = "test_repo_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testRepoId;

    private Boolean active;

    @Column(name = "name")
    private String name;

    @Column(name = "\"order\"")
    private Long order;

    // bi-directional many-to-one association to Configuration
    @JsonBackReference(value = "configurations")
    @OneToMany(mappedBy = "clientEnvironment")
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
