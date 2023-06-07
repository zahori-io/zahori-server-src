package io.zahori.server.repository;

import io.zahori.server.model.PeriodicExecution;
import io.zahori.server.model.PeriodicExecutionView;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodicExecutionsRepository extends CrudRepository<PeriodicExecution, Long> {

    @Query("select pe from PeriodicExecution pe inner join pe.process p where p.processId = :processId and p.client.clientId = :clientId ORDER BY pe.periodicExecutionId DESC")
    Iterable<PeriodicExecution> findByClientIdAndProcessId(@Param("clientId") Long clientId, @Param("processId") Long processId);

    List<PeriodicExecutionView> findAllProjectedBy();
}
