package io.zahori.server.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * The type Process.
 */
@Entity
@Table(name = "process_schedule")
public class ProcessSchedule implements Serializable {

    @Id
    @Column(name = "process_schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processScheduleId;

    @JsonBackReference(value = "process")
    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;

    @JoinColumn(name = "execution_id")
    private Long executionId;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "next_execution")
    private Date nextExecution;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "maximum_executions")
    private Long maxExecutions;

    @Transient
    private String name;
    @Transient
    private Long numExecutions;

    public Long getProcessScheduleId() {
        return processScheduleId;
    }

    public void setProcessScheduleId(Long processScheduleId) {
        this.processScheduleId = processScheduleId;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Date getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(Date nextExecution) {
        this.nextExecution = nextExecution;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumExecutions() {
        return numExecutions;
    }

    public void setNumExecutions(Long numExecutions) {
        this.numExecutions = numExecutions;
    }

    public Long getMaxExecutions() {
        return maxExecutions;
    }

    public void setMaxExecutions(Long maxExecutions) {
        this.maxExecutions = maxExecutions;
    }
}
