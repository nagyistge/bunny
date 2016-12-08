package org.rabix.engine.processor.handler.impl;

import org.rabix.engine.event.impl.RootStatusEvent;
import org.rabix.engine.model.RootRecord;
import org.rabix.engine.processor.handler.EventHandler;
import org.rabix.engine.processor.handler.EventHandlerException;
import org.rabix.engine.service.ContextRecordService;

import com.google.inject.Inject;

public class ContextStatusEventHandler implements EventHandler<RootStatusEvent> {

  private final ContextRecordService contextRecordService;

  @Inject
  public ContextStatusEventHandler(ContextRecordService contextRecordService) {
    this.contextRecordService = contextRecordService;
  }
  
  @Override
  public void handle(RootStatusEvent event) throws EventHandlerException {
    RootRecord contextRecord = contextRecordService.find(event.getRootId());
    contextRecord.setStatus(event.getStatus());
    contextRecordService.update(contextRecord);
  }

}
