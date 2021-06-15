package io.zahori.server.repository;

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

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.zahori.server.model.ClientEnvironment;

/**
 * The interface Configuration repository.
 */
//@Repository
@RepositoryRestResource(path = "environments")
public interface EnvironmentsRepository extends CrudRepository<ClientEnvironment, Long> {

    /**
     * Find by process id set.
     *
     * @param processId the process id
     * @return the set
     */
    //@Query("SELECT e FROM ClientEnvironment e WHERE e.process.processId = :processId")
    //Set<ClientEnvironment> findByEnvironmentId(@Param("environmentId") Long processId);
}
