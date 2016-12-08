package org.rabix.engine.service;

import java.util.ArrayList;
import java.util.List;

import org.rabix.engine.model.RootRecord;

public class ContextRecordService {

  private List<RootRecord> contextRecords = new ArrayList<>();
  
  public synchronized void create(RootRecord contextRecord) {
    contextRecords.add(contextRecord);
  }
  
  public synchronized void update(RootRecord context) {
    for (RootRecord c : contextRecords) {
      if (c.getId().equals(context.getId())) {
        c.setStatus(context.getStatus());
        return;
      }
    }
  }
  
  public synchronized RootRecord find(String id) {
    for (RootRecord contextRecord : contextRecords) {
      if (contextRecord.getId().equals(id)) {
        return contextRecord;
      }
    }
    return null;
  }
  
}
