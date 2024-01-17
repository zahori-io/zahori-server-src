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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Browser.
 */
@Entity
@Table(name = "browsers")
//@NamedQuery(name = "Browser.findAll", query = "SELECT b FROM Browser b")
public class Browser implements Serializable {

    private static final long serialVersionUID = 8341351366554054141L;

    @Id
    @Column(name = "browser_name")
    private String browserName;

    private Boolean active;

    @Column(name = "default_version")
    private String defaultVersion;

    @Column(name = "icon")
    private String icon;

    @Column(name = "order")
    private Long order;

    // bi-directional many-to-one association to CasesExecution
    @JsonBackReference(value = "casesExecutions")
    @OneToMany(mappedBy = "browser")
    private List<CaseExecution> casesExecutions;

    // bi-directional many-to-one association to PlatformBrowsersVersion
    //	@OneToMany(mappedBy="browser")
    //	private List<PlatformBrowsersVersion> platformBrowsersVersions;

    /**
     * Instantiates a new Browser.
     */
    public Browser() {
    }

    /**
     * Gets browser name.
     *
     * @return the browser name
     */
    public String getBrowserName() {
        return this.browserName;
    }

    /**
     * Sets browser name.
     *
     * @param browserName the browser name
     */
    public void setBrowserName(String browserName) {
        this.browserName = browserName;
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
     * Gets default version.
     *
     * @return the default version
     */
    public String getDefaultVersion() {
        return this.defaultVersion;
    }

    /**
     * Sets default version.
     *
     * @param defaultVersion the default version
     */
    public void setDefaultVersion(String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    /**
     * Gets icon.
     *
     * @return the icon
     */
    public String getIcon() {
        return this.icon;
    }

    /**
     * Sets icon.
     *
     * @param icon the icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
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
     * Add cases execution case execution.
     *
     * @param casesExecution the cases execution
     * @return the case execution
     */
    public CaseExecution addCasesExecution(CaseExecution casesExecution) {
        getCasesExecutions().add(casesExecution);
        casesExecution.setBrowser(this);

        return casesExecution;
    }

    /**
     * Remove cases execution case execution.
     *
     * @param casesExecution the cases execution
     * @return the case execution
     */
    public CaseExecution removeCasesExecution(CaseExecution casesExecution) {
        getCasesExecutions().remove(casesExecution);
        casesExecution.setBrowser(null);

        return casesExecution;
    }

    //	public List<PlatformBrowsersVersion> getPlatformBrowsersVersions() {
    //		return this.platformBrowsersVersions;
    //	}
    //
    //	public void setPlatformBrowsersVersions(List<PlatformBrowsersVersion> platformBrowsersVersions) {
    //		this.platformBrowsersVersions = platformBrowsersVersions;
    //	}

}
