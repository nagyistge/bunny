package org.rabix.engine.service;

import java.util.List;

import org.rabix.bindings.model.dag.DAGLinkPort.LinkPortType;
import org.rabix.engine.dao.LinkRecordRepository;
import org.rabix.engine.dao.Repository;
import org.rabix.engine.model.JobRecord;
import org.rabix.engine.model.LinkRecord;
import org.rabix.engine.model.LinkRecord.LinkRecordCacheKey;
import org.rabix.engine.service.cache.generic.Cache;
import org.rabix.engine.service.cache.generic.CacheItem.Action;

import com.google.inject.Inject;

public class LinkRecordService {

  private LinkRecordRepository linkRecordRepository;
  private Cache<LinkRecord, Repository<LinkRecord>> cache;
  
  @Inject
  public LinkRecordService(LinkRecordRepository linkRecordRepository) {
    this.linkRecordRepository = linkRecordRepository;
    this.cache = new Cache<LinkRecord, Repository<LinkRecord>>(linkRecordRepository, LinkRecord.class);
  }
  
  public void create(LinkRecord link) {
    cache.put(link, Action.INSERT);
  }

  public void delete(String rootId) {
  }
  
  public List<LinkRecord> findBySourceJobId(String jobId, String contextId) {
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

  public Cache<LinkRecord, Repository<LinkRecord>> getCache() {
    return cache;
  }

}
