package io.zahori.server.repository;

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