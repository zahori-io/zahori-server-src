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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Client test repo.
 */
@Entity
@Table(name = "client_test_repos")
//@NamedQuery(name="ClientTestRepo.findAll", query="SELECT c FROM ClientTestRepo c")
public class ClientTestRepo implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ClientTestRepoPK id;

    @Column(name = "password")
    private String password;

    @Column(name = "url")
    private String url;

    @Column(name = "\"user\"")
    private String user;

    // bi-directional many-to-one association to Client
    @JsonBackReference(value = "client")
    @ManyToOne
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    // bi-directional many-to-one association to TestRepository
    @JsonBackReference(value = "testRepository")
    @ManyToOne
    @JoinColumn(name = "test_repo_id", insertable = false, updatable = false)
    private TestRepository testRepository;

    /**
     * Instantiates a new Client test repo.
     */
    public ClientTestRepo() {
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public ClientTestRepoPK getId() {
        return this.id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(ClientTestRepoPK id) {
        this.id = id;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public String getUser() {
        return this.user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     */
    public void setUser(String user) {
        this.user = user;
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
     * Gets test repository.
     *
     * @return the test repository
     */
    public TestRepository getTestRepository() {
        return this.testRepository;
    }

    /**
     * Sets test repository.
     *
     * @param testRepository the test repository
     */
    public void setTestRepository(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

}
