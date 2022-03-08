package io.zahori.server.controller;

import io.zahori.server.model.CaseExecution;
import io.zahori.server.model.Execution;
import io.zahori.server.model.Process;
import io.zahori.server.model.ProcessSchedule;
import io.zahori.server.repository.CaseExecutionsRepository;
import io.zahori.server.repository.ExecutionsRepository;
import io.zahori.server.repository.ProcessScheduleRepository;
import io.zahori.server.repository.ProcessesRepository;
import io.zahori.server.utils.scheduling.RunnableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

    @Autowired
    private CaseExecutionsRepository caseExecutionsRepository;


    public ScheduleController () {
        //createExecutionsPool();
    }
    //Saves the configuration of the scheduled process and schedules it if necessary

    @PostMapping(path = "")
    public ResponseEntity<Object> postSchedule(@RequestBody ProcessSchedule processSchedule) {
        try {
            LOG.info("<--create process schedule-->");
            LOG.info(processSchedule.toString());
            processSchedule.setNextExecution(getNextValidTime(processSchedule.getCronExpression()));
            ProcessSchedule ps = processScheduleRepository.save(processSchedule);
            Execution exec = executionsRepository.findById(ps.getExecutionId()).orElse(null);
            assert exec != null;
            if(!processSchedule.getName().equals("") && !exec.getName().equals(processSchedule.getName()))
                exec.setName(processSchedule.getName());
            exec.setProcessSchedule(ps);
            executionsRepository.save(exec);
            insertProcessOnSchedule(processSchedule);
            return new ResponseEntity<>(ps, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping(path = "/update")
    public ResponseEntity<Object> updateSchedule(@RequestBody ProcessSchedule processSchedule) {
        try {
            LOG.info("<--update process schedule-->");
            LOG.info(processSchedule.toString());
            processSchedule.setNextExecution(getNextValidTime(processSchedule.getCronExpression()));
            ProcessSchedule ps = processScheduleRepository.save(processSchedule);
            insertProcessOnSchedule(processSchedule);
            return new ResponseEntity<>(ps, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //Retrieve scheduled task by process Id
    @GetMapping(path = "/list/{processId}")
    public ResponseEntity<Object> getScheduledTasks (@PathVariable Long processId){
        try {
            List<ProcessSchedule> processSchedules = processScheduleRepository.findProcessScheduleByProcessId(processId);
            processSchedules.forEach(processSchedule -> {
                processSchedule.setName(executionsRepository.getNameByProcessScheduleId(processSchedule.getProcessScheduleId()));
                processSchedule.setNumExecutions(executionsRepository.countByProcessScheduleId(processSchedule.getProcessScheduleId()));
            });
            return new ResponseEntity<>(processSchedules, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //Retrieve scheduled task by process Id
    @GetMapping(path = "/{processScheduleId}")
    public ResponseEntity<Object> getScheduledTask (@PathVariable Long processScheduleId){
        try {
            ProcessSchedule processSchedule = processScheduleRepository.findById(processScheduleId).orElse(null);
            assert processSchedule != null;
            processSchedule.setName(executionsRepository.getNameByProcessScheduleId(processSchedule.getProcessScheduleId()));
            processSchedule.setNumExecutions(executionsRepository.countByProcessScheduleId(processSchedule.getProcessScheduleId()));
            return new ResponseEntity<>(processSchedule, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //Save the reference run configuration
    @PostMapping(path = "/execution")
    public ResponseEntity<Object> saveExecution(@RequestBody Execution execution){
        try {
            LOG.info("<--create process schedule reference execution-->");
            Execution exec = executionsRepository.save(execution);
            return new ResponseEntity<>(exec, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Update the reference run configuration
    @PostMapping(path = "/execution/update")
    public ResponseEntity<Object> updateExecution(@RequestBody Execution execution){
        try {
            LOG.info("<--update process schedule reference execution-->");
            Execution exec =executionsRepository.findById(execution.getExecutionId()).orElse(null);
            assert exec != null;
            List<CaseExecution> newCe = new ArrayList<CaseExecution>();;
            Execution finalExec = exec;
            List<CaseExecution> oldCe= finalExec.getCasesExecutions();
            execution.getCasesExecutions().forEach(nc ->{
                oldCe.forEach(oc->{
                    if(ceEqual(nc,oc,execution.getExecutionId()))
                        nc.setCaseExecutionId(oc.getCaseExecutionId());
                });
                newCe.add(nc);
            });
            execution.setCasesExecutions(newCe);
            exec = executionsRepository.save(execution);
            return new ResponseEntity<>(exec, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private boolean ceEqual(CaseExecution nc, CaseExecution oc, Long executionId){
        boolean a,b,c,d;
        a  = b = c = d = false;
        try{
            a = nc.getBrowser().getBrowserName().equals(oc.getBrowser().getBrowserName());
            b = nc.getScreenResolution().equals(oc.getScreenResolution());
            c = nc.getCas().getCaseId().equals(oc.getCas().getCaseId());
            d = oc.getExecution().getExecutionId().equals(executionId);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return a &&  b && c && d;
    }
    //retrieve executions of scheduled task
    @GetMapping(path = "/executions/{processScheduleId}")
    public ResponseEntity<Object> getExecutions (@PathVariable Long processScheduleId){
        try {
            List<Execution> executions = executionsRepository.findByProcessScheduleId(processScheduleId);
            return new ResponseEntity<>(executions, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //retrieve main execution of scheduled task
    @GetMapping(path = "/execution/{processScheduleId}")
    public ResponseEntity<Object> getMainExecution (@PathVariable Long processScheduleId){
        try {
            List<Execution> executions = executionsRepository.findMainExecutionByProcessScheduleId(processScheduleId);
            return new ResponseEntity<>(executions, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private Date getNextValidTime(String cron) {
        Date nextValidTime;
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
        LOG.info("<--Executing pool-->"+ new Date());
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

        Execution exec = executionsRepository.findById(ps.getExecutionId()).orElse(null);
        assert exec != null;
        exec.setExecutionId(0L);
        exec.setDate(new Date().toString());
        exec.setProcessSchedule(ps);
        taskScheduler.schedule(new RunnableTask(exec),ps.getNextExecution());
        ps.setNextExecution(getNextValidTime(ps.getCronExpression()));
        LOG.info("<--Next execution-->"+ ps.getNextExecution());
        processScheduleRepository.save(ps);
    }
    //Calculate if it is necessary to insert the process in the current execution pool
    private void insertProcessOnSchedule(ProcessSchedule processSchedule){
        Date today=new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 58);
        c.set(Calendar.HOUR, 23);
        if (processSchedule.getNextExecution().before(c.getTime()) && processSchedule.getNextExecution().after(today))
            insertExecution(processSchedule);
    }
}

