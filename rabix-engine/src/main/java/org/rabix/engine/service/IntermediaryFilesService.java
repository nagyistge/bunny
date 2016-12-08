package org.rabix.engine.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rabix.bindings.model.FileValue;
import org.rabix.common.logging.VerboseLogger;
import org.rabix.engine.IntermediaryFilesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediaryFilesService {
  
  private final static Logger logger = LoggerFactory.getLogger(IntermediaryFilesService.class);
  
  private Map<String, Integer> files = new HashMap<String, Integer>();
  
  public synchronized void addOrIncrement(FileValue file, Integer usage) {
    Set<String> paths = new HashSet<String>();
    IntermediaryFilesHelper.extractPathsFromFileValue(paths, file);
    
    for(String path: paths) {
      if(files.containsKey(path)) {
        logger.debug("Increment file usage counter: " + path + ": " + ((Integer) files.get(path) + usage));
        files.put(path, files.get(path) + usage);
      }
      else {
        logger.debug("Adding file usage counter: " + path + ": " + usage);
        files.put(path, usage);
      }
    }
    // dumpFiles();
  }
  
  public synchronized void addOrSet(FileValue file, Integer usage) {
    Set<String> paths = new HashSet<String>();
    IntermediaryFilesHelper.extractPathsFromFileValue(paths, file);
    
    for(String path: paths) {
      if(files.containsKey(path)) {
        logger.debug("Increment file usage counter: " + path + ": " + usage);
        files.put(path, usage);
      }
    }
  }
  
  public synchronized Set<String> getUnusedFiles() {
    Set<String> unusedFiles = new HashSet<String>();
    for(Iterator<Map.Entry<String, Integer>> it = files.entrySet().iterator(); it.hasNext();) {
      Entry<String, Integer> entry = it.next();
      if(entry.getValue() == 0) {
        unusedFiles.add(entry.getKey());
        it.remove();
      }
    }
    return unusedFiles;
  }
  
  public synchronized void decrementFiles(Set<String> checkFiles) {
    for(String path: checkFiles) {
      files.put(path, files.get(path) - 1);
    }
  }
  
  public void dumpFiles() {
    VerboseLogger.log("Intermediary files table");
    for(Iterator<Map.Entry<String, Integer>> it = files.entrySet().iterator(); it.hasNext();) {
      Entry<String, Integer> entry = it.next();
      VerboseLogger.log(entry.getKey() + ": " + entry.getValue());
    }
  }
  
}
