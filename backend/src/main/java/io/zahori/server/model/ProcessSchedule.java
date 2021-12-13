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

    @JsonBackReference(value = "execution")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "execution_id")
    private List<Execution> executions;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "next_execution")
    private Date nextExecution;

    @Column(name = "active")
    private Boolean active;

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

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }
}
