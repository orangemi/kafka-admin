package com.teambition.kafka.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.Map;

public class Consumer {
  private String group;
  private Map<TopicPartition, Long> offsets = new HashMap<>();
  public Consumer(String group) {
    this.group = group;
  }

  @JsonProperty
  public String getGroup() {
    return group;
  }
  
  @JsonProperty
  public Map<TopicPartition, Long> getOffsets() {
    return offsets;
  }
  
  public void addTopicPartition(TopicPartition topicPartition, long offset) {
    offsets.put(topicPartition, offset);
  }
}
