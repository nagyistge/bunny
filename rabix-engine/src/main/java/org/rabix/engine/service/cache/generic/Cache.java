package org.rabix.engine.service.cache.generic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.rabix.engine.dao.Repository;
import org.rabix.engine.service.cache.generic.CacheItem.Action;

public class Cache<C extends Cachable, R extends Repository<C>> {

  private R repository;
  private Class<C> entityClass;
  
  private Map<CacheKey, CacheItem<C>> cache = new HashMap<>();
  
  public Cache(R repository, Class<C> entityClass) {
    this.repository = repository;
    this.entityClass = entityClass;
  }
  
  public void clear() {
    cache.clear();
  }
  
  public void flush() {
    if (cache.isEmpty()) {
      return;
    }
    Collection<CacheItem<C>> items = cache.values();
    
    List<CacheItem<C>> inserts = new ArrayList<>();
    List<CacheItem<C>> updates = new ArrayList<>();
    
    for (CacheItem<C> item : items) {
      switch (item.action) {
      case INSERT:
        inserts.add(item);
        break;
      case UPDATE:
        updates.add(item);
        break;
      default:
        break;
      }
    }
    
    try {
      StringBuilder cacheLog = new StringBuilder();
      Method insertMethod = repository.getClass().getMethod("insert", entityClass);
      for (CacheItem<C> item : inserts) {
        insertMethod.invoke(repository, item.cachable);
        cacheLog.append("insert " + item.cachable);
      }

      Method updateMethod = repository.getClass().getMethod("update", entityClass);
      for (CacheItem<C> item : updates) {
        updateMethod.invoke(repository, item.cachable);
        cacheLog.append("update " + item.cachable);
      }
      System.out.println("Cache log \n" + cacheLog.toString());
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
    }
    cache.clear();
  }
  
  public void put(C cachable, Action action) {
    CacheKey key = cachable.generateKey();
    if (cache.containsKey(key)) {
      CacheItem<C> item = cache.get(key);
      item.cachable = cachable;
    } else {
      cache.put(key, new CacheItem<C>(action, cachable));
    }
  }
  
  public List<C> get(CacheKey search) {
    List<C> result = new ArrayList<>();
    for (Entry<CacheKey, CacheItem<C>> entry : cache.entrySet()) {
      if (entry.getKey().satisfies(search)) {
        result.add(entry.getValue().cachable);
      }
    }
    return result;
  }
  
  public boolean isEmpty() {
    return cache.isEmpty();
  }
  
}
