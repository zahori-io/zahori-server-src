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
import io.zahori.server.model.Resolution;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The interface resolutions repository.
 */
@RepositoryRestResource(path = "resolutions")
public interface ResolutionsRepository extends CrudRepository<Resolution, Long> {

    /**
     * Find resolutions by clientId and processId.
     *
     * @param clientId the client id
     * @param processId the process id
     * @return the set
     */
    @Query("SELECT r FROM Resolution r WHERE r.process.client.clientId = :clientId and r.process.processId = :processId and r.active = true")
    Set<Resolution> findByClientIdAndProcessId(@Param("clientId") Long clientId, @Param("processId") Long processId);
}
