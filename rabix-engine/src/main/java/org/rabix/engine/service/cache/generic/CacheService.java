package org.rabix.engine.service.cache.generic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.rabix.engine.dao.JobRecordRepository;
import org.rabix.engine.dao.LinkRecordRepository;
import org.rabix.engine.dao.VariableRecordRepository;
import org.rabix.engine.model.JobRecord;
import org.rabix.engine.model.LinkRecord;
import org.rabix.engine.model.VariableRecord;
import org.rabix.engine.processor.EventProcessor.EventProcessorDispatcher;
import org.rabix.engine.singleton.RepositoriesFactory;

import com.google.inject.Inject;

public class CacheService {

  private Map<String, Map<String, Cache>> caches = new HashMap<>();
  
  private JobRecordRepository jobRecordRepository;
  private LinkRecordRepository linkRecordRepository;
  private VariableRecordRepository variableRecordRepository;

  private Configuration configuration;
  
  @Inject
  public CacheService(RepositoriesFactory repositoriesFactory, Configuration configuration) {
    this.configuration = configuration;
    this.jobRecordRepository = repositoriesFactory.getRepositories().jobRecordRepository();
    this.linkRecordRepository = repositoriesFactory.getRepositories().linkRecordRepository();
    this.variableRecordRepository = repositoriesFactory.getRepositories().variableRecordRepository();
  }
  
  public Cache getCache(String rootId, String entity) {
    String index = "" + EventProcessorDispatcher.dispatch(rootId, getNumberOfEventProcessors());
    
    Map<String, Cache> singleCache = caches.get(index);
    if (singleCache == null) {
      singleCache = generateCache();
      caches.put(index, singleCache);
    }
    return singleCache.get(entity);
  }
  
  public void flush(String rootId) {
    if (rootId == null) {
      return;
    }
    String index = "" + EventProcessorDispatcher.dispatch(rootId, getNumberOfEventProcessors());
    Map<String, Cache> singleCache = caches.get(index);
    if (singleCache == null) {
      return;
    }
    for (Cache cache : singleCache.values()) {
      cache.flush();
    }
  }
  
  private Map<String, Cache> generateCache() {
    Map<String, Cache> generated = new HashMap<>();
    generated.put("JOB_RECORD", new Cache(jobRecordRepository, JobRecord.class));
    generated.put("LINK_RECORD", new Cache(linkRecordRepository, LinkRecord.class));
    generated.put("VARIABLE_RECORD", new Cache(variableRecordRepository, VariableRecord.class));
    return generated;
  }
  
  private int getNumberOfEventProcessors() {
    return configuration.getInt("bunny.event_processor.count", Runtime.getRuntime().availableProcessors());
  }
  
}
