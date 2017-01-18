package org.rabix.engine.service.cache.generic;

public interface CacheKey {

  boolean satisfies(CacheKey key);
  
}
