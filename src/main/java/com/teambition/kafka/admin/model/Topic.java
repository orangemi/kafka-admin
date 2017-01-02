package com.teambition.kafka.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Properties;

public class Topic {
  private String name;
  private int partitions;
  private Properties configs;
  public Topic(String name, int partitions, Properties configs) {
    this.name = name;
    this.partitions = partitions;
    this.configs = configs;
  }
  
  @JsonProperty
  public String getName() { return name; }
  @JsonProperty
  public int getPartitions() { return partitions; }
  @JsonProperty
  public Properties getConfigs() { return configs; }
}
