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

import java.util.ArrayList;
import java.util.List;

/**
 * The type Builds.
 */
public class Builds {

    private List<Build> builds = new ArrayList<>();

    /**
     * Instantiates a new Builds.
     */
    public Builds() {

    }

    /**
     * Instantiates a new Builds.
     *
     * @param builds the builds
     */
    public Builds(List<Build> builds) {
        super();
        this.builds = builds;
    }

    /**
     * Gets builds.
     *
     * @return the builds
     */
    public List<Build> getBuilds() {
        return builds;
    }

    /**
     * Sets builds.
     *
     * @param builds the builds
     */
    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

}
