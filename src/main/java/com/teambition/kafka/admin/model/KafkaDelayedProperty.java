package com.teambition.kafka.admin.model;

public class KafkaDelayedProperty {
  private int numDelayedOperations;
  private int purgatorySize;
  private double value;
  
  public int getNumDelayedOperations() {
    return numDelayedOperations;
  }
  
  public void setNumDelayedOperations(int numDelayedOperations) {
    this.numDelayedOperations = numDelayedOperations;
  }
  
  public int getPurgatorySize() {
    return purgatorySize;
  }
  
  public void setPurgatorySize(int purgatorySize) {
    this.purgatorySize = purgatorySize;
  }
  
  public double getValue() {
    return value;
  }
  
  public void setValue(double value) {
    this.value = value;
  }
  
  @Override
  public String toString() {
    return "{value=" + this.getValue() + ",delayed=" + this.getNumDelayedOperations() + ",size=" + this.getPurgatorySize() + "}";
  }
}
