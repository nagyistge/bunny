package org.rabix.engine.dao;

import org.rabix.engine.service.cache.generic.Cachable;

public interface Repository<C extends Cachable> {

  int insert(C record);
  
  int update(C record);
  
}
