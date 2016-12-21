package com.teambition.kafka.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Consumer {
  private String group;
  private Map<String, Map<Integer, Long>> topics = new HashMap<>();
  public Consumer(String group) {
    this.group = group;
  }
  public void addTopicPartition(String topic, int partition, long offset) {
    Map<Integer, Long> consumerTopic = topics.get(topic);
    if (consumerTopic == null) {
      consumerTopic = new HashMap<>();
      topics.put(topic, consumerTopic);
    }
  
    consumerTopic.put(partition, offset);
  }
  
  @JsonProperty
  public String getGroup() {
    return group;
  }
  
  @JsonProperty
  public Map<String, Map<Integer, Long>> getTopics() {
    return topics;
  }
}
