package org.rabix.engine.service;

import java.util.List;

import org.rabix.bindings.model.dag.DAGLinkPort.LinkPortType;
import org.rabix.engine.dao.LinkRecordRepository;
import org.rabix.engine.model.LinkRecord;
import org.rabix.engine.model.LinkRecord.LinkRecordCacheKey;
import org.rabix.engine.service.cache.generic.Cache;
import org.rabix.engine.service.cache.generic.CacheItem.Action;
import org.rabix.engine.singleton.RepositoriesFactory;
import org.rabix.engine.service.cache.generic.CacheService;

import com.google.inject.Inject;

public class LinkRecordService {

  private CacheService cacheService;
  private LinkRecordRepository linkRecordRepository;
  
  @Inject
  public LinkRecordService(RepositoriesFactory repositoriesFactory, CacheService cacheService) {
    this.cacheService = cacheService;
    this.linkRecordRepository = repositoriesFactory.getRepositories().linkRecordRepository();
  }
  
  public void create(LinkRecord link) {
    Cache cache = cacheService.getCache(link.getContextId(), "LINK_RECORD");
    cache.put(link, Action.INSERT);
  }

  public void delete(String rootId) {
  }
  
  public List<LinkRecord> findBySourceJobId(String jobId, String contextId) {
    Cache cache = cacheService.getCache(contextId, "LINK_RECORD");
    List<LinkRecord> records = cache.get(new LinkRecordCacheKey(contextId, jobId, null, null, null, null, null));
    if (!records.isEmpty()) {
      return records;
    }
    records = linkRecordRepository.getBySourceJobId(jobId, contextId);
    for (LinkRecord linkRecord : records) {
      cache.put(linkRecord, Action.UPDATE);
    }
    return records;
  }
  
  public List<LinkRecord> findBySourceAndSourceType(String jobId, LinkPortType varType, String contextId) {
    Cache cache = cacheService.getCache(contextId, "LINK_RECORD");
    List<LinkRecord> records = cache.get(new LinkRecordCacheKey(contextId, jobId, null, varType, null, null, null));
    if (!records.isEmpty()) {
      return records;
    }
    records = linkRecordRepository.getBySourceAndSourceType(jobId, varType, contextId);
    for (LinkRecord linkRecord : records) {
      cache.put(linkRecord, Action.UPDATE);
    }
    return records;
  }
  
  public List<LinkRecord> findBySource(String jobId, String portId, String contextId) {
    Cache cache = cacheService.getCache(contextId, "LINK_RECORD");
    List<LinkRecord> records = cache.get(new LinkRecordCacheKey(contextId, jobId, portId, null, null, null, null));
    if (!records.isEmpty()) {
      return records;
    }
    records = linkRecordRepository.getBySource(jobId, portId, contextId);
    for (LinkRecord linkRecord : records) {
      cache.put(linkRecord, Action.UPDATE);
    }
    return records;
  }
  
  public List<LinkRecord> findBySourceAndDestinationType(String jobId, String portId, LinkPortType varType, String contextId) {
    Cache cache = cacheService.getCache(contextId, "LINK_RECORD");
    List<LinkRecord> records = cache.get(new LinkRecordCacheKey(contextId, jobId, portId, null, null, null, varType));
    if (!records.isEmpty()) {
      return records;
    }
    records = linkRecordRepository.getBySourceAndDestinationType(jobId, portId, varType, contextId);
    for (LinkRecord linkRecord : records) {
      cache.put(linkRecord, Action.UPDATE);
    }
    return records;
  }

}
