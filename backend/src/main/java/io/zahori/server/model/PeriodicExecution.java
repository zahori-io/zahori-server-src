package io.zahori.server.model;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;

/**
 * The type Periodic execution.
 */
@Entity
@Table(name = "periodic_executions")
@NamedQuery(name = "PeriodicExecution.findAll", query = "SELECT p FROM PeriodicExecution p")
public class PeriodicExecution implements Serializable {

    private static final long serialVersionUID = -4460535018526307592L;

    @Id
    @Column(name = "periodic_execution_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long periodicExecutionId;

    private boolean active;

    @Column(columnDefinition = "text[]")
    private String[] days = {};

//    @valid expression regular
    private String time;

//    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
//    @GeneratedValue(generator = "UUIDGenerator")
//    // TODO: https://thorben-janssen.com/generate-uuids-primary-keys-hibernate/
    private UUID uuid;

//    @JsonBackReference(value = "execution")
//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "execution_id", nullable = false)
//    private Execution execution;
//    @JsonBackReference(value = "process")
//    @ManyToOne
//    @JoinColumn(name = "process_id")
//    private Process process;
    /**
     * Instantiates a new Periodic execution.
     */
    public PeriodicExecution() {
    }

    public Long getPeriodicExecutionId() {
        return this.periodicExecutionId;
    }

    public void setPeriodicExecutionId(Long periodicExecutionId) {
        this.periodicExecutionId = periodicExecutionId;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String[] getDays() {
        return this.days;
    }

    @JsonIgnore
    public String getDays(String[] daysArray) {
        if (daysArray == null) {
            throw new RuntimeException("No days");
        }
        String daysSplitted = StringUtils.replace(String.join(",", daysArray), " ", "");
        if (StringUtils.isAllBlank(daysSplitted)) {
            throw new RuntimeException("No days");
        }

        // TODO validar días: MON, TUE, ...
        return daysSplitted;
    }

    public void setDays(String[] days) {
        this.days = days;
    }

    public String getTime() {
        return this.time;
    }

    @JsonIgnore
    public int getHour() {
        String[] timeSplit = StringUtils.split(time, ':');
        if (timeSplit == null) {
            throw new RuntimeException("Invalid hour");
        }

        Integer hour;
        if (timeSplit.length < 2) {
            throw new RuntimeException("Invalid hour");
        }

        try {
            hour = Integer.valueOf(timeSplit[0]);
            if (hour < 0 || hour > 23) {
                throw new RuntimeException("Invalid hour");
            }
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid hour");
        }
        return hour;
    }

    @JsonIgnore
    public int getMinute() {
        String[] timeSplit = StringUtils.split(time, ':');
        if (timeSplit == null) {
            throw new RuntimeException("Invalid minutes");
        }

        Integer minutes;
        if (timeSplit.length < 2) {
            throw new RuntimeException("Invalid minutes");
        }

        try {
            minutes = Integer.valueOf(timeSplit[1]);
            if (minutes < 0 || minutes > 59) {
                throw new RuntimeException("Invalid minutes");
            }
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid minutes");
        }
        return minutes;
    }

    @JsonIgnore
    public String getCronExpression() {
        return "0 " + getMinute() + " " + getHour() + " ? * " + getDays(days);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

//    public Execution getExecution() {
//        return execution;
//    }
//
//    public void setExecution(Execution execution) {
//        this.execution = execution;
//    }
//    public Process getProcess() {
//        return process;
//    }
//
//    public void setProcess(Process process) {
//        this.process = process;
//    }
}
