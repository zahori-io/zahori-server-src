package io.zahori.server.model;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

public abstract interface PeriodicExecutionView {

    UUID getUuid();

    @Value("#{'0 ' + target.minute + ' ' + target.hour + ' ? * ' + target.days}")
    String getCronExpression();

//    @Override
//    public String toString() {
//        return "Task [uuid: " + uuid + " --> cron: " + cronExpression + "]";
//    }
}
