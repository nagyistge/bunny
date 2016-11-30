package org.rabix.engine.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rabix.bindings.model.FileValue;
import org.rabix.common.logging.VerboseLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediaryFilesService {
  
  private final static Logger logger = LoggerFactory.getLogger(IntermediaryFilesService.class);
  
  private Map<String, Integer> files = new HashMap<String, Integer>();
  
  public synchronized void addOrIncrement(FileValue file, Integer usage) {
    Set<String> paths = new HashSet<String>();
    extractPathsFromFileValue(paths, file);
    
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
    dumpFiles();
  }
  
  public synchronized void addOrSet(FileValue file, Integer usage) {
    Set<String> paths = new HashSet<String>();
    extractPathsFromFileValue(paths, file);
    
    for(String path: paths) {
      if(files.containsKey(path)) {
        logger.debug("Increment file usage counter: " + path + ": " + usage);
        files.put(path, usage);
      }
    }
    dumpFiles();
  }
  
  public synchronized Set<String> getUnusedFiles(Set<FileValue> exclude) {
    Set<String> unusedFiles = new HashSet<String>();
    for(Iterator<Map.Entry<String, Integer>> it = files.entrySet().iterator(); it.hasNext();) {
      Entry<String, Integer> entry = it.next();
      if(entry.getValue() == 0) {
        unusedFiles.add(entry.getKey());
        it.remove();
      }
    }
    Set<String> excludeFiles = new HashSet<String>();
    for(FileValue file: exclude) {
      extractPathsFromFileValue(excludeFiles, file);
    }
    unusedFiles.removeAll(excludeFiles);
    return unusedFiles;
  }
  
  public synchronized void decrementFiles(Set<FileValue> checkFiles) {
    Set<String> paths = new HashSet<String>();
    for(FileValue file: checkFiles) {
      extractPathsFromFileValue(paths, file);
    }
    for(String path: paths) {
      files.put(path, files.get(path) - 1);
    }
    dumpFiles();
  }
  
  public void extractPathsFromFileValue(Set<String> paths, FileValue file) {
    paths.add(file.getPath());
    for(FileValue f: file.getSecondaryFiles()) {
      extractPathsFromFileValue(paths, f);
    }
  }
  
  private void dumpFiles() {
    VerboseLogger.log("Intermediary files table");
    for(Iterator<Map.Entry<String, Integer>> it = files.entrySet().iterator(); it.hasNext();) {
      Entry<String, Integer> entry = it.next();
      VerboseLogger.log(entry.getKey() + ": " + entry.getValue());
    }
  }
  
}
