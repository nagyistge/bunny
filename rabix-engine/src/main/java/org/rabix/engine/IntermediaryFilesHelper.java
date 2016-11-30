package org.rabix.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.rabix.bindings.helper.FileValueHelper;
import org.rabix.bindings.model.FileValue;
import org.rabix.bindings.model.dag.DAGLinkPort.LinkPortType;
import org.rabix.engine.model.JobRecord;
import org.rabix.engine.model.LinkRecord;
import org.rabix.engine.model.VariableRecord;
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

  public static Map<FileValue, Integer> getOutputFiles(JobRecord job, String portId, Object output, LinkRecordService linkRecordService) {
    Map<FileValue, Integer> result = new HashMap<FileValue, Integer>();
    List<FileValue> outputFiles = FileValueHelper.getFilesFromValue(output);
    List<LinkRecord> links = linkRecordService.findBySource(job.getId(), portId, job.getRootId());
    for(Iterator<LinkRecord> i = links.iterator(); i.hasNext();) {
      if(i.next().getDestinationJobId().equals("root")) {
        i.remove();  
      }
    }
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
    return result;
  }
  
  
}
