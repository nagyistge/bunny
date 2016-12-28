package org.rabix.engine.service.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rabix.engine.model.JobRecord;

public class JobRecordCache {

  public static enum Action {
    INSERT,
    UPDATE
  }

  private Map<CacheKey, CacheItem> cache = new HashMap<>();
  
  public void clear() {
    cache.clear();
  }
  
  public void put(JobRecord jobRecord) {
    put(jobRecord, Action.UPDATE);
  }
  
  public void put(Collection<JobRecord> jobRecords) {
    for (JobRecord record : jobRecords) {
      put(record, Action.UPDATE);
    }
  }
  
  public void put(JobRecord jobRecord, Action action) {
    CacheKey key = new CacheKey(jobRecord.getId(), jobRecord.getRootId());
    if (cache.containsKey(key)) {
      CacheItem item = cache.get(key);
      item.jobRecord = jobRecord;
    } else {
      cache.put(key, new CacheItem(action, jobRecord));
    }
  }
  
  public JobRecord get(String id, String rootId) {
    CacheKey key = new CacheKey(id, rootId);
    return cache.get(key) != null ? cache.get(key).jobRecord : null;
  }
  
  public boolean isEmpty() {
    return cache.isEmpty();
  }
  
  public Set<JobRecord> get() {
    Set<JobRecord> records = new HashSet<>();
    for (CacheItem item : cache.values()) {
      records.add(item.jobRecord);
    }
    return records;
  }
  
  public Collection<CacheItem> getCacheItems() {
    return cache.values();
  }
  
  public static class CacheKey {
    String id;
    String rootId;
    
    public CacheKey(String id, String rootId) {
      this.id = id;
      this.rootId = rootId;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((rootId == null) ? 0 : rootId.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CacheKey other = (CacheKey) obj;
      if (id == null) {
        if (other.id != null)
          return false;
      } else if (!id.equals(other.id))
        return false;
      if (rootId == null) {
        if (other.rootId != null)
          return false;
      } else if (!rootId.equals(other.rootId))
        return false;
      return true;
    }
    
  }
  
  public static class CacheItem {
    public Action action;
    public JobRecord jobRecord;
    
    public CacheItem(Action action, JobRecord jobRecord) {
      this.action = action;
      this.jobRecord = jobRecord;
    }

    @Override
    public String toString() {
      return "CacheItem [action=" + action + ", jobRecord=" + jobRecord + "]";
    }
    
  }
  
}
