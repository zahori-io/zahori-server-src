package io.zahori.server.repository;

import io.zahori.server.model.PeriodicExecution;
import io.zahori.server.model.PeriodicExecutionView;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodicExecutionsRepository extends CrudRepository<PeriodicExecution, Long> {

    List<PeriodicExecutionView> findAllProjectedByActive(boolean active);
}
