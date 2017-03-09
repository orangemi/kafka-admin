package com.teambition.kafka.admin.model;

public class VersionModel {
  private String name;
  private String version;
  
  public VersionModel() {
    this.name = this.getClass().getPackage().getName();
    this.version = this.getClass().getPackage().getImplementationVersion();
  }
  
  public String getVersion() {
    return version;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  
}
