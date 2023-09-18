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
import io.zahori.server.model.CaseExecution;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The interface Case executions repository.
 */
@RepositoryRestResource(path = "caseExecutions")
public interface CaseExecutionsRepository extends CrudRepository<CaseExecution, Long> {

    @Query("select ce from CaseExecution ce where ce.cas.caseId = :caseId and ce.cas.process.client.clientId = :clientId and ce.cas.process.processId = :processId ORDER BY ce.dateTimestamp ASC")
    Iterable<CaseExecution> findByCaseIdAndClientIdAndProcessId(@Param("caseId") Long caseId, @Param("clientId") Long clientId, @Param("processId") Long processId);
}
