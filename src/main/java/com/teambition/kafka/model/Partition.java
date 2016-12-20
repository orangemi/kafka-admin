package com.teambition.kafka.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class Partition {
  private int partittionId; // broker id
  private int leader; // broker id
  private Collection<PartitionReplica> replicas;
  
  public Partition(int partitionId, Collection<PartitionReplica> replicas) {
    this.partittionId = partitionId;
    this.replicas = replicas;
  }
  
  @JsonProperty
  public int getPartittionId() {
    return partittionId;
  }
  @JsonProperty
  public int getLeader() {
    return leader;
  }
  @JsonProperty
  public Collection<PartitionReplica> getReplicas() {
    return replicas;
  }
  
  public void setLeader(int leader) {
    this.leader = leader;
  }
}
