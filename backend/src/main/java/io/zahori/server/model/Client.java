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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.zahori.server.security.Account;

/**
 * The type Client.
 */
@Entity
@Table(name = "clients")
// @NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c")
public class Client implements Serializable {

    private static final long serialVersionUID = 6495648001968903873L;

    @Id
    @Column(name = "client_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    private Boolean active;

    @Column(name = "css")
    private String css;

    @Column(name = "logo")
    private String logo;

    @Column(name = "name")
    private String name;

    @Column(name = "num_parallel")
    private Integer numParallel;

    // bi-directional many-to-one association to ClientTeam
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    @OrderBy("name ASC")
    private Set<ClientTeam> clientTeams;

    @JsonIgnore
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Set<Account> accounts;

    /**
     * Instantiates a new Client.
     */
    public Client() {
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public Long getClientId() {
        return this.clientId;
    }

    /**
     * Sets client id.
     *
     * @param clientId the client id
     */
    public void setClientId(Long clientId) {
        this.clientId = clientId;
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
     * Gets css.
     *
     * @return the css
     */
    public String getCss() {
        return this.css;
    }

    /**
     * Sets css.
     *
     * @param css the css
     */
    public void setCss(String css) {
        this.css = css;
    }

    /**
     * Gets logo.
     *
     * @return the logo
     */
    public String getLogo() {
        return this.logo;
    }

    /**
     * Sets logo.
     *
     * @param logo the logo
     */
    public void setLogo(String logo) {
        this.logo = logo;
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
     * Gets num parallel.
     *
     * @return the num parallel
     */
    public Integer getNumParallel() {
        return this.numParallel;
    }

    /**
     * Sets num parallel.
     *
     * @param numParallel the num parallel
     */
    public void setNumParallel(Integer numParallel) {
        this.numParallel = numParallel;
    }

    /**
     * Gets client teams.
     *
     * @return the client teams
     */
    public Set<ClientTeam> getClientTeams() {
        return this.clientTeams;
    }

    /**
     * Sets client teams.
     *
     * @param clientTeams the client teams
     */
    public void setClientTeams(Set<ClientTeam> clientTeams) {
        this.clientTeams = clientTeams;
    }

    /**
     * Gets accounts.
     *
     * @return the accounts
     */
    public Set<Account> getAccounts() {
        return accounts;
    }

    /**
     * Sets accounts.
     *
     * @param accounts the accounts
     */
    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

}
