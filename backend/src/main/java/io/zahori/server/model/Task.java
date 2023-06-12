package io.zahori.server.model;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

public class Task {

    private UUID uuid;
    private String cronExpression;

    public Task() {

    }

    public Task(UUID uuid, String cronExpression) {
        this.uuid = uuid;
        this.cronExpression = cronExpression;
    }

    @Override
    public String toString() {
        return "Task [uuid: " + uuid + " --> cron: " + cronExpression + "]";
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Value("#{'0 ' + target.minute + ' ' + target.hour + ' ? * ' + target.days}")
    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

}
