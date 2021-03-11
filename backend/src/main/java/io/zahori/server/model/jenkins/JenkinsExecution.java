package io.zahori.server.model.jenkins;

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

import java.util.HashSet;
import java.util.Set;

import io.zahori.server.model.Case;

/**
 * The type Jenkins execution.
 */
public class JenkinsExecution {

    private Set<String> browsers = new HashSet<>();
    private Set<Case> cases = new HashSet<>();

    /**
     * Instantiates a new Jenkins execution.
     */
    public JenkinsExecution() {
        super();
    }

    @Override
    public String toString() {
        return "JenkinsExecution [browsers=" + browsers + ", cases=" + cases + "]";
    }

    /**
     * Gets browsers.
     *
     * @return the browsers
     */
    public Set<String> getBrowsers() {
        return browsers;
    }

    /**
     * Sets browsers.
     *
     * @param browsers the browsers
     */
    public void setBrowsers(Set<String> browsers) {
        this.browsers = browsers;
    }

    /**
     * Add browser.
     *
     * @param browser the browser
     */
    public void addBrowser(String browser) {
        this.browsers.add(browser);
    }

    /**
     * Gets cases.
     *
     * @return the cases
     */
    public Set<Case> getCases() {
        return cases;
    }

    /**
     * Sets cases.
     *
     * @param cases the cases
     */
    public void setCases(Set<Case> cases) {
        this.cases = cases;
    }

    /**
     * Add case.
     *
     * @param cas the cas
     */
    public void addCase(Case cas) {
        this.cases.add(cas);
    }

}
