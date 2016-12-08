package org.rabix.engine.model;

import java.util.Map;

public class RootRecord {

  public static enum RootStatus {
    RUNNING,
    COMPLETED,
    FAILED
  }
  
  private String id;
  private Map<String, Object> config;
  private RootStatus status;
  
  public RootRecord(final String id, Map<String, Object> config, RootStatus status) {
    this.id = id;
    this.config = config;
    this.status = status;
  }
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  public void setConfig(Map<String, Object> config) {
    this.config = config;
  }

  public RootStatus getStatus() {
    return status;
  }

  public void setStatus(RootStatus status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "RootRecord [id=" + id + ", config=" + config + ", status=" + status + "]";
  }

}
