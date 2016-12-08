package org.rabix.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rabix.bindings.helper.FileValueHelper;
import org.rabix.bindings.model.FileValue;
import org.rabix.bindings.model.dag.DAGLinkPort.LinkPortType;
import org.rabix.engine.model.JobRecord;
import org.rabix.engine.model.LinkRecord;
import org.rabix.engine.model.VariableRecord;
import org.rabix.engine.service.JobRecordService;
import org.rabix.engine.service.LinkRecordService;
import org.rabix.engine.service.VariableRecordService;

public class IntermediaryFilesHelper {
  
  public static Map<FileValue, Integer> getInputFiles(JobRecord job, VariableRecordService variableRecordService, LinkRecordService linkRecordService) {
    Map<FileValue, Integer> result = new HashMap<FileValue, Integer>();
    List<VariableRecord> inputVariables = variableRecordService.find(job.getId(), LinkPortType.INPUT, job.getRootId());
    for(VariableRecord input: inputVariables) {
      List<LinkRecord> links = linkRecordService.findBySource(job.getId(), input.getPortId(), job.getRootId());
      for(Iterator<LinkRecord> i = links.iterator(); i.hasNext();) {
        if(i.next().getDestinationJobId().equals("root")) {
          i.remove();  
        }
      }
      List<FileValue> inputFiles = FileValueHelper.getFilesFromValue(input.getValue());
      for(FileValue file: inputFiles) {
        if(result.containsKey(file)) {
          result.put(file, result.get(file) + links.size());
        }
        else {
          result.put(file, links.size());
        }
      }
    }
    return result;
  }
  
  public static Map<FileValue, Integer> getInputFilesForScatter(JobRecord job, VariableRecordService variableRecordService, Integer numberOfScatter, String portId) {
    Map<FileValue, Integer> result = new HashMap<FileValue, Integer>();
    List<VariableRecord> inputVariables = variableRecordService.find(job.getId(), LinkPortType.INPUT, job.getRootId());
    for(VariableRecord input: inputVariables) {
      if(input.getPortId().equals(portId)) {
        List<FileValue> inputFiles = FileValueHelper.getFilesFromValue(input.getValue());
        for(FileValue file: inputFiles) {
          result.put(file, 0);
        }
      }
      else {
        List<FileValue> inputFiles = FileValueHelper.getFilesFromValue(input.getValue());
        for(FileValue file: inputFiles) {
          result.put(file, 1);
        }
      }
    }
    return result;
  }

  public static Map<FileValue, Integer> getOutputFiles(JobRecord job, Map<String, Object> output, LinkRecordService linkRecordService) {
    Map<FileValue, Integer> result = new HashMap<FileValue, Integer>();
    for(Map.Entry<String, Object> entry : output.entrySet()) {
      List<FileValue> outputFiles = FileValueHelper.getFilesFromValue(entry.getValue());
      List<LinkRecord> links = linkRecordService.findBySource(job.getId(), entry.getKey(), job.getRootId());
//      for(Iterator<LinkRecord> i = links.iterator(); i.hasNext();) {
//        if(i.next().getDestinationJobId().equals("root")) {
//          i.remove();  
//        }
//      }
      if(links.size() > 0) {
        for(FileValue file: outputFiles) {
          if(result.containsKey(file)) {
            result.put(file, result.get(file) + links.size());
          }
          else {
            result.put(file, links.size());
          }
        }
      }
    }
    return result;
  }
  
  public static boolean isScatterComplete(JobRecord jobRecord, JobRecordService jobRecordService) {
    boolean completed = true;
    List<JobRecord> scattered = jobRecordService.findByParent(jobRecord.getParentId(), jobRecord.getRootId());
    for(JobRecord scatterJob: scattered) {
      if(!scatterJob.equals(jobRecord) && !scatterJob.isCompleted()) {
        completed = false;
        break;
      }
    }
    return completed;
  }
  
  public static void extractPathsFromFileValue(Set<String> paths, FileValue file) {
    paths.add(file.getPath());
    for(FileValue f: file.getSecondaryFiles()) {
      extractPathsFromFileValue(paths, f);
    }
  }
  
}
