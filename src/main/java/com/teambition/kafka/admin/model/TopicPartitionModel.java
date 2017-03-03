package com.teambition.kafka.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class TopicPartitionModel {
  private int id; // partition id
  private int leader; // broker id
  private long beginOffset; // begin offset
  private long endOffset; // end offset
  private Collection<PartitionReplica> replicas;
  private String topic;
  
  public TopicPartitionModel(String topic, int id, long beginOffset, long endOffset, Collection<PartitionReplica> replicas) {
    this.topic = topic;
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
