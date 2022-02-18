package io.zahori.server.utils.scheduling;

import io.zahori.server.model.Execution;
import io.zahori.server.service.ExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RunnableTask implements Runnable{
    private static final Logger LOG = LoggerFactory.getLogger(RunnableTask.class);

    private final Execution execution;

    @Autowired
    private ExecutionService executionService;

    public RunnableTask(Execution execution){
        this.execution = execution;
    }

    @Override
    public void run() {
        try {
            LOG.info("<--create execution controller-->");
            executionService.create(execution);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
