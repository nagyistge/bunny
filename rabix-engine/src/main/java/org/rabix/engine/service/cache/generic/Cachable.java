package org.rabix.engine.service.cache.generic;

public interface Cachable {

  String getName();
  
  CacheKey generateKey();
  
}
