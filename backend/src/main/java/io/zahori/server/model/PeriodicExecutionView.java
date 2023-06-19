package io.zahori.server.model;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

public abstract interface PeriodicExecutionView {

    UUID getUuid();

    @Value("#{'0 ' + target.minutes + ' ' + target.hour + ' ? * ' + target.days}")
    String getCronExpression();

}
