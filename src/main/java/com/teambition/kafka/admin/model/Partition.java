package com.teambition.kafka.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class Partition {
  private int id; // partition id
  private int leader; // broker id
  private long beginOffset; // begin offset
  private long endOffset; // end offset
  private Collection<PartitionReplica> replicas;
  
  public Partition(int id, long beginOffset, long endOffset, Collection<PartitionReplica> replicas) {
    this.id = id;
    this.beginOffset = beginOffset;
    this.endOffset = endOffset;
    this.replicas = replicas;
  }
  
  @JsonProperty
  public int getId() {
    return id;
  }
  @JsonProperty
  public int getLeader() {
    return leader;
  }
  @JsonProperty
  public Collection<PartitionReplica> getReplicas() {
    return replicas;
  }
  @JsonProperty
  public long getEndOffset() { return endOffset; }
  @JsonProperty
  public long getBeginOffset() { return beginOffset; }
  
  public void setLeader(int leader) {
    this.leader = leader;
  }
}
