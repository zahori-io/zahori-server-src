package io.zahori.server.controller;

import io.zahori.server.model.Execution;
import io.zahori.server.model.ProcessSchedule;
import io.zahori.server.repository.ExecutionsRepository;
import io.zahori.server.repository.ProcessScheduleRepository;
import io.zahori.server.utils.scheduling.RunnableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private ProcessScheduleRepository processScheduleRepository;

    @Autowired
    private ExecutionsRepository executionsRepository;

    public ScheduleController () {

    }

    @PostMapping(path = "/schedule")
    public ResponseEntity<Object> postSchedule(@RequestBody ProcessSchedule processSchedule, HttpServletRequest request) {
        try {
            LOG.info("create process schedule");
            processScheduleRepository.save(processSchedule);
            Date today=new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(today);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 58);
            c.set(Calendar.HOUR, 23);
            if (processSchedule.getNextExecution().before(c.getTime()) && processSchedule.getNextExecution().after(today))
                insertExecution(processSchedule);
            return new ResponseEntity<>(processSchedule, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Para el frontend
    //String cronExp= "0 "+processSchedule.getMinute()+" "+processSchedule.getHour()+" ? * " + days.substring(0,days.length()-2) + " *";
    private Date getNextValidTime(String cron) {
        Date nextValidTime = null;
        String[] cronTokens = cron.split(" ");
        final CronSequenceGenerator generator = new CronSequenceGenerator(cron);
        Calendar cal = Calendar.getInstance();
        nextValidTime = generator.next(cal.getTime());
        cal.setTime(nextValidTime);
        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(cronTokens[2]));
        cal.set(Calendar.MINUTE,Integer.parseInt(cronTokens[1]));
        LOG.info(cal.getTime().toString());
        return cal.getTime();
    }
    //generate daily run list at 23:58 every day-> 0 58 23 ? * *
    @Scheduled(cron = "0 58 23 ? * *")
    private void createExecutionsPool(){
        LOG.info("<--Executing pool-->"+ new Date().toString());
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE,1);
        List<ProcessSchedule> processList = processScheduleRepository.findProcessScheduleByDates(today,c.getTime());
        if(!processList.isEmpty())
            processList.forEach(this::insertExecution);

    }
    //Insert execution task on pool
    private void insertExecution(ProcessSchedule ps){
        Execution exec = ps.getExecutions().get(0);
        exec.setExecutionId(0L);
        exec.setDate(new Date().toString());
        exec.setProcessSchedule(ps);
        taskScheduler.schedule(new RunnableTask(exec),ps.getNextExecution());
        ps.setNextExecution(getNextValidTime(ps.getCronExpression()));
        LOG.info("<--Next execution-->"+ ps.getNextExecution());
        processScheduleRepository.save(ps);
    }
}
