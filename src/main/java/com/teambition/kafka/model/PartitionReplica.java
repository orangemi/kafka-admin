package com.teambition.kafka.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartitionReplica {
  private int broker;
  private boolean isLeader;
  private boolean inSync;

  public PartitionReplica(int broker, boolean isLeader, boolean inSync) {
    this.broker = broker;
    this.isLeader = isLeader;
    this.inSync = inSync;
  }
  
  @JsonProperty
  public int getBroker() { return broker; }
  @JsonProperty
  public boolean isLeader() {
    return isLeader;
  }
  @JsonProperty
  public boolean isInSync() {
    return inSync;
  }
}
