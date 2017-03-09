package com.teambition.kafka.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.Map;

public class ConsumerModel {
  private String group;
  private Map<TopicPartition, Long> offsets = new HashMap<>();
  public ConsumerModel(String group) {
    this.group = group;
  }

  @JsonProperty
  public String getGroup() {
    return group;
  }
  
  @JsonProperty
  public Map<String, Map<Integer, Long>> getOffsets() {
    Map<String, Map<Integer, Long>> result = new HashMap<>();
    offsets.forEach((topicPartition, offset) -> {
      if (!result.containsKey(topicPartition.topic())) {
        result.put(topicPartition.topic(), new HashMap<>());
      }
      result.get(topicPartition.topic()).put(topicPartition.partition(), offset);
    });
    return result;
  }
  
  public void addTopicPartition(TopicPartition topicPartition, long offset) {
    offsets.put(topicPartition, offset);
  }
  
  
}
