package io.zahori.server.repository;

import io.zahori.server.model.Process;
import io.zahori.server.model.ProcessSchedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;

/**
 * The interface Processes repository.
 */
@RepositoryRestResource(path = "process_schedule")
public interface ProcessScheduleRepository extends CrudRepository<ProcessSchedule, Long> {
    /**
     * get a list of scheduled executions for the day
     * @param startDate  The date to start the search
     * @param endDate The date to end the search
     * @return List of executions
     */
    @Query("select ps from ProcessSchedule ps where ps.nextExecution between :startDate and :endDate")
    List<ProcessSchedule> findProcessScheduleByDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    /**
     * get a list of scheduled executions
     * @param processId  process id
     * @return List of executions
     */
    @Query("select ps from ProcessSchedule ps where ps.process.processId = :processId")
    List<ProcessSchedule> findProcessScheduleByProcessId(@Param("processId") Long processId);
}
