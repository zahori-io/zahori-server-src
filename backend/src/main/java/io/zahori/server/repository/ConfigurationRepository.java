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
import io.zahori.server.model.Configuration;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The interface Configuration repository.
 */
//@Repository
@RepositoryRestResource(path = "configurations")
public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {

    /**
     * Find by process id set.
     *
     * @param processId the process id
     * @return the set
     */
    @Query("SELECT c FROM Configuration c WHERE c.process.processId = :processId and c.active = true ORDER BY c.name ASC")
    Set<Configuration> findByProcessId(@Param("processId") Long processId);

    @Query("SELECT c FROM Configuration c WHERE c.configurationId = :configurationId and c.process.client.clientId = :clientId")
    Configuration findByIdAndClientId(@Param("configurationId") Long configurationId, @Param("clientId") Long clientId);

    @Query("SELECT c FROM Configuration c WHERE c.process.client.clientId = :clientId and c.clientTestRepo.repoInstanceId = :repoInstanceId")
    Set<Configuration> findByClientIdAndRepoInstanceId(@Param("clientId") Long clientId, @Param("repoInstanceId") Long repoInstanceId);

}
