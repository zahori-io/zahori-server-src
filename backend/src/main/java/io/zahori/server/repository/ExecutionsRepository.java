package io.zahori.server.repository;

import io.zahori.server.model.Execution;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The interface Executions repository.
 */
@RepositoryRestResource(path = "executions")
public interface ExecutionsRepository extends PagingAndSortingRepository<Execution, Long> {

    /**
     * Find by client id and process id iterable.
     *
     * @param clientId the client id
     * @param processId the process id
     * @return the iterable
     */
    @Query("select e from Execution e inner join e.process p where p.processId = :processId and p.client.clientId = :clientId and e.status <> 'Scheduled' ORDER BY e.executionId DESC")
    Iterable<Execution> findByClientIdAndProcessId(@Param("clientId") Long clientId, @Param("processId") Long processId);

    @Query("select e from Execution e inner join e.process p where p.processId = :processId and p.client.clientId = :clientId and e.status <> 'Scheduled' ORDER BY e.executionId DESC")
    Page<Execution> findByClientIdAndProcessId(@Param("clientId") Long clientId, @Param("processId") Long processId, Pageable pageable);

    @Query("select e from Execution e inner join e.periodicExecution pe where pe.uuid = :uuid")
    Execution findByPeriodicExecutionUuid(@Param("uuid") UUID uuid);

    @Query("select e.process.processId from Execution e where e.executionId = :executionId")
    Long getProcessIdByExecutionId(@Param("executionId") Long executionId);

}
