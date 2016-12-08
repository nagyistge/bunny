package org.rabix.engine.event.impl;

import org.rabix.engine.event.Event;
import org.rabix.engine.model.RootRecord.RootStatus;

public class RootStatusEvent implements Event {

  private final String rootId;
  private final RootStatus status;
  
  public RootStatusEvent(String rootId, RootStatus status) {
    this.status = status;
    this.rootId = rootId;
  }
  
  @Override
  public EventType getType() {
    return EventType.CONTEXT_STATUS_UPDATE;
  }

  public RootStatus getStatus() {
    return status;
  }
  
  @Override
  public String getRootId() {
    return rootId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((rootId == null) ? 0 : rootId.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
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
    RootStatusEvent other = (RootStatusEvent) obj;
    if (rootId == null) {
      if (other.rootId != null)
        return false;
    } else if (!rootId.equals(other.rootId))
      return false;
    if (status != other.status)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ContextStatusEvent [contextId=" + rootId + ", status=" + status + "]";
  }

}
