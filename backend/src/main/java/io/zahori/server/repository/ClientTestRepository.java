package io.zahori.server.repository;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 - 2022 PANEL SISTEMAS INFORMATICOS,S.L
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
import io.zahori.server.model.ClientTestRepo;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClientTestRepository extends CrudRepository<ClientTestRepo, Long> {

    @Query("SELECT c FROM ClientTestRepo c WHERE c.id.clientId = :clientId")
    Set<ClientTestRepo> findByClientId(@Param("clientId") Long clientId);

    @Query("SELECT c FROM ClientTestRepo c WHERE c.id.clientId = :clientId and c.id.testRepoId = :testRepoId")
    ClientTestRepo findByClientIdAndTestRepoId(@Param("clientId") Long clientId, @Param("testRepoId") Long testRepoId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ClientTestRepo c WHERE c.id.clientId = :clientId AND c.id.testRepoId = :testRepoId")
    void deleteByClientIdAndTestRepoId(@Param("clientId") Long clientId, @Param("testRepoId") Long testRepoId);

}
