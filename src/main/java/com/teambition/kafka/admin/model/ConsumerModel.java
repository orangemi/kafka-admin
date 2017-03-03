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
//  public Map<TopicPartition, Long> getOffsets() {
//    return offsets;
//  }
  
  public void addTopicPartition(TopicPartition topicPartition, long offset) {
//    System.out.println(topicPartition);
//    System.out.println(offset);
    if (offset == 0L) {
      offsets.remove(topicPartition);
//      System.out.println("offset is Zero");
//      System.out.println("offset size: " + offsets.size());
    } else {
      offsets.put(topicPartition, offset);
    }
  }
  
  
}
