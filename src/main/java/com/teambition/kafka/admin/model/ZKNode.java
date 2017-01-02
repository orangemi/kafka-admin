package com.teambition.kafka.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class ZKNode {
  private Collection<String> children;
  private String data;
  private ZkStat stat;
  
  @JsonProperty
  public Collection<String> getChildren() {
    return children;
  }
  
  public void setChildren(Collection<String> children) {
    this.children = children;
  }
  
  @JsonProperty
  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }
  
  @JsonProperty
  public ZkStat getZkState() {
    return stat;
  }
  
  public void setState(org.apache.zookeeper.data.Stat stat) {
    this.stat = new ZkStat();
    this.stat.setAclVersion(stat.getAversion());
    this.stat.setCtime(stat.getCtime());
    this.stat.setCversion(stat.getCversion());
    this.stat.setcZxid(stat.getCzxid());
    this.stat.setDataLength(stat.getDataLength());
    this.stat.setDataVersion(stat.getVersion());
    this.stat.setEphemeralOwner(stat.getEphemeralOwner());
    this.stat.setMtime(stat.getMtime());
    this.stat.setmZxid(stat.getMzxid());
    this.stat.setNumChildren(stat.getNumChildren());
    this.stat.setpZxid(stat.getPzxid());
  }
  
  public void setZkState(ZkStat zkStat) {
    this.stat = zkStat;
  }
}

class ZkStat {
  private long cZxid; // = 0x0
  private long ctime; // = Thu Jan 01 08:00:00 CST 1970
  private long mZxid; // = 0x0
  private long mtime; // = Thu Jan 01 08:00:00 CST 1970
  private long pZxid; // = 0x11c
  private int cversion; // = 32
  private int dataVersion; // = 0
  private int aclVersion; // = 0
  private long ephemeralOwner; // = 0x0
  private int dataLength; // = 0
  private int numChildren; // = 8
  
  @JsonProperty
  public long getcZxid() {
    return cZxid;
  }
  
  public void setcZxid(long cZxid) {
    this.cZxid = cZxid;
  }
  
  @JsonProperty
  public long getCtime() {
    return ctime;
  }
  
  public void setCtime(long ctime) {
    this.ctime = ctime;
  }
  
  @JsonProperty
  public long getmZxid() {
    return mZxid;
  }
  
  public void setmZxid(long mZxid) {
    this.mZxid = mZxid;
  }
  
  @JsonProperty
  public long getMtime() {
    return mtime;
  }
  
  public void setMtime(long mtime) {
    this.mtime = mtime;
  }
  
  @JsonProperty
  public long getpZxid() {
    return pZxid;
  }
  
  public void setpZxid(long pZxid) {
    this.pZxid = pZxid;
  }
  
  @JsonProperty
  public int getCversion() {
    return cversion;
  }
  
  public void setCversion(int cversion) {
    this.cversion = cversion;
  }
  
  @JsonProperty
  public int getDataVersion() {
    return dataVersion;
  }
  
  public void setDataVersion(int dataVersion) {
    this.dataVersion = dataVersion;
  }
  
  @JsonProperty
  public int getAclVersion() {
    return aclVersion;
  }
  
  public void setAclVersion(int aclVersion) {
    this.aclVersion = aclVersion;
  }
  
  @JsonProperty
  public long getEphemeralOwner() {
    return ephemeralOwner;
  }
  
  public void setEphemeralOwner(long ephemeralOwner) {
    this.ephemeralOwner = ephemeralOwner;
  }
  
  @JsonProperty
  public int getDataLength() {
    return dataLength;
  }
  
  public void setDataLength(int dataLength) {
    this.dataLength = dataLength;
  }
  
  @JsonProperty
  public int getNumChildren() {
    return numChildren;
  }
  
  public void setNumChildren(int numChildren) {
    this.numChildren = numChildren;
  }
}
