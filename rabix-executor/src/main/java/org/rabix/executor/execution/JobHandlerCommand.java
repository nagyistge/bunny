package org.rabix.executor.execution;

import java.util.Map;

import org.rabix.bindings.model.Job;
import org.rabix.bindings.model.Job.JobStatus;
import org.rabix.executor.handler.JobHandler;
import org.rabix.executor.model.JobData;
import org.rabix.executor.service.JobDataService;
import org.rabix.executor.transport.TransportQueueConfig;
import org.rabix.executor.transport.TransportStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Job execution command abstraction. 
 */
public abstract class JobHandlerCommand {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * Command types 
   */
  public enum JobHandlerCommandType {
    START, STOP, STATUS
  }

  protected final JobDataService jobDataService;

  protected TransportQueueConfig mqConfig;
  protected final TransportStub mqTransportStub;
  
  public JobHandlerCommand(JobDataService jobDataService, TransportStub mqTransportStub, TransportQueueConfig mqConfig) {
    this.jobDataService = jobDataService;
    
    this.mqConfig = mqConfig;
    this.mqTransportStub = mqTransportStub;
  }

  /**
   * Find {@link JobData} and run command 
   */
  public Result run(String id, String contextId, JobHandler handler) {
    JobData data = null;
    try {
      data = jobDataService.find(id, contextId);
      if (data == null) {
        throw new RuntimeException("No JobData assocated for ID = " + id);
      }
      return run(data, handler, contextId);
    } catch (Exception e) {
      failed(data, "Executor faced a runtime exception.", e);
      jobDataService.save(data, "Executor faced a runtime exception.", JobStatus.FAILED, contextId);
      throw e;
    }
  }

  /**
   * Run command using the {@link JobData} 
   */
  public abstract Result run(JobData jobData, JobHandler handler, String contextId);

  /**
   * Get repeat information. By default, the command is not repeatable.
   */
  public Repeat getRepeat() {
    return null;
  }

  /**
   * Send notification to master about STARTED event 
   */
  protected void started(JobData jobData, String message) {
    logger.info(message);

    Job job = Job.cloneWithStatus(jobData.getJob(), JobStatus.RUNNING);
    jobData.setJob(job);
    mqTransportStub.send(mqConfig.getFromBackendQueue(), job);
  }

  /**
   * Send notification to master about FAILED event 
   */
  protected void failed(JobData jobData, String message, Throwable e) {
    logger.error(message, e);

    Job job = Job.cloneWithStatus(jobData.getJob(), JobStatus.FAILED);
    jobData.setJob(job);
    mqTransportStub.send(mqConfig.getFromBackendQueue(), job);
  }

  /**
   * Send notification to master about STOPPED event 
   */
  protected void stopped(JobData jobData, String message) {
    logger.info(message);

    Job job = Job.cloneWithStatus(jobData.getJob(), JobStatus.ABORTED);
    jobData.setJob(job);
    mqTransportStub.send(mqConfig.getFromBackendQueue(), job);
  }

  /**
   * Send notification to master about COMPLETED event 
   */
  protected void completed(JobData jobData, String message, Map<String, Object> result) {
    logger.info(message);

    Job job = Job.cloneWithStatus(jobData.getJob(), JobStatus.COMPLETED);
    job = Job.cloneWithOutputs(job, result);
    jobData.setJob(job);
    mqTransportStub.send(mqConfig.getFromBackendQueue(), job);
  }

  /**
   * Get command type 
   */
  public abstract JobHandlerCommandType getType();
  
  @Override
  public String toString() {
    return getType().name();
  }
  
  /**
   * Simple wrapper for command result
   */
  public static class Result {
    public final boolean isLastCommand;

    public Result(boolean isLastCommand) {
      this.isLastCommand = isLastCommand;
    }
  }

  /**
   * Command repeat information
   */
  public static class Repeat {
    public final long delay;

    public Repeat(long delay) {
      this.delay = delay;
    }
  }
}
