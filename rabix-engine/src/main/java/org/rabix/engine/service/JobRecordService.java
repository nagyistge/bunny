package org.rabix.engine.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.rabix.engine.model.JobRecord;

public class JobRecordService {

  public static enum JobState {
    PENDING,
    READY,
    RUNNING,
    COMPLETED,
    FAILED
  }

  private ConcurrentMap<String, List<JobRecord>> jobRecordsPerRootId = new ConcurrentHashMap<String, List<JobRecord>>();

  public static String generateUniqueId() {
    return UUID.randomUUID().toString();
  }
  
  public void create(JobRecord jobRecord) {
    getJobRecords(jobRecord.getRootId()).add(jobRecord);
  }

  public void delete(String rootId) {
    jobRecordsPerRootId.remove(rootId);
  }
  
  public void update(JobRecord jobRecord) {
    for (JobRecord jr : getJobRecords(jobRecord.getRootId())) {
      if (jr.getId().equals(jobRecord.getId())) {
        jr.setState(jobRecord.getState());
        jr.setContainer(jobRecord.isContainer());
        jr.setScattered(jobRecord.isScattered());
        jr.setInputCounters(jobRecord.getInputCounters());
        jr.setOutputCounters(jobRecord.getOutputCounters());
        jr.setScatterWrapper(jobRecord.isScatterWrapper());
        jr.setScatterStrategy(jobRecord.getScatterStrategy());
        return;
      }
    }
  }
  
  public List<JobRecord> find(String rootId) {
    return getJobRecords(rootId);
  }
  
  public List<JobRecord> findReady(String rootId) {
    List<JobRecord> result = new ArrayList<>();
    
    for (JobRecord jr : getJobRecords(rootId)) {
      if (jr.getState().equals(JobState.READY) && jr.getRootId().equals(rootId)) {
        result.add(jr);
      }
    }
    return result;
  }
  
  public List<JobRecord> findByParent(String parentId, String rootId) {
    List<JobRecord> result = new ArrayList<>();

    for (JobRecord jr : getJobRecords(rootId)) {
      if (jr.getParentId() != null && jr.getParentId().equals(parentId)) {
        result.add(jr);
      }
    }
    return result;
  }
  
  public JobRecord find(String id, String rootId) {
    for (JobRecord jr : getJobRecords(rootId)) {
      if (jr.getId().equals(id) && jr.getRootId().equals(rootId)) {
        return jr;
      }
    }
    return null;
  }
  
  public JobRecord findRoot(String rootId) {
    for (JobRecord jr : getJobRecords(rootId)) {
      if (jr.isMaster() && jr.getRootId().equals(rootId)) {
        return jr;
      }
    }
    return null;
  }
  
  private List<JobRecord> getJobRecords(String rootId) {
    List<JobRecord> jobRecordList = jobRecordsPerRootId.get(rootId);
    if (jobRecordList == null) {
      jobRecordList = new ArrayList<>();
      jobRecordsPerRootId.put(rootId, jobRecordList);
    }
    return jobRecordList;
  }
  
}
