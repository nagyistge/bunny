package org.rabix.engine.service.cache.generic;

public class CacheItem<T extends Cachable> {

  public static enum Action {
    INSERT,
    UPDATE
  }
  
  public Action action;
  public T cachable;
  
  public CacheItem(Action action, T cachable) {
    this.action = action;
    this.cachable = cachable;
  }

  @Override
  public String toString() {
    return "CacheItem [action=" + action + ", cachable=" + cachable + "]";
  }
  
}
