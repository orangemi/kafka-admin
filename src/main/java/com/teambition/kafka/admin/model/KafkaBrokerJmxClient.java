package com.teambition.kafka.admin.model;

import com.yammer.metrics.core.Meter;
import org.apache.kafka.common.TopicPartition;
import sun.management.ConnectorAddressLink;

import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.AttributeNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.MalformedURLException;
import java.util.*;

import com.yammer.metrics.reporting.JmxReporter.MeterMBean;
import com.yammer.metrics.reporting.JmxReporter.GaugeMBean;
import com.yammer.metrics.reporting.JmxReporter.HistogramMBean;
import com.yammer.metrics.reporting.JmxReporter.TimerMBean;

public class KafkaBrokerJmxClient {
  protected MBeanServerConnection connection;
  private JMXConnector jmxConnection;
  
  public KafkaBrokerJmxClient() {
  }

  public KafkaBrokerJmxClient(int pid) {
    this.init(pid);
  }
  
  public KafkaBrokerJmxClient(String jmxUrl) {
    this.init(jmxUrl);
  }

  public static void main(String[] argv) {
    KafkaBrokerJmxClient client = new KafkaBrokerJmxClient(53825);
//    client.getBrokerTopicsMetrics().forEach((key, meter) -> {
//      System.out.println(key + ": ");
//      System.out.println("  EventType : " + meter.getEventType());
//      System.out.println("  Count : " + meter.getCount());
//      System.out.println("  RateUnit : " + meter.getRateUnit());
//      System.out.println("  OneMinuteRate : " + meter.getOneMinuteRate());
//      System.out.println("  MeanRate : " + meter.getMeanRate());
//    });

    client.getObjectNamesByPattern("kafka.*:*").forEach(objectName -> {
      System.out.println(objectName.toString());
    });
    
//    System.out.println(client.getTopicPartitionsLogEndOffsetMetrics());
  
//    System.out.println(client.getTopicPartitionsUnderReplicatedMetrics());
//    client.getRequestMetrics().forEach((key, histogram) -> {
//      System.out.println(key + ": ");
//      System.out.println("  Count : " + histogram.getCount());
//      System.out.println("  Max : " + histogram.getMax());
//      System.out.println("  Min : " + histogram.getMin());
//      System.out.println("  Mean : " + histogram.getMean());
//      System.out.println("  50%th : " + histogram.get50thPercentile());
//    });
//
//    client.getRequestPerSecMetrics().forEach((key, meter) -> {
//      System.out.println(key + ": ");
//      System.out.println("  EventType : " + meter.getEventType());
//      System.out.println("  Count : " + meter.getCount());
//      System.out.println("  RateUnit : " + meter.getRateUnit());
//      System.out.println("  OneMinuteRate : " + meter.getOneMinuteRate());
//      System.out.println("  MeanRate : " + meter.getMeanRate());
//    });
//    System.out.println(client.getDelayOperations());
//    System.out.println(client.getSocketServerMetrics());

  }
  
  public Collection<ThreadInfo> getThreads() {
    return getThreads(false, false);
  }
  
  public Collection<ThreadInfo> getThreads(boolean lockedMonitors, boolean lockedSynchronizers) {
    try {
      ThreadMXBean threadMXBean = JMX.newMXBeanProxy(connection, new ObjectName("java.lang:type=Threading"), ThreadMXBean.class);
      return Arrays.asList(threadMXBean.dumpAllThreads(lockedMonitors, lockedSynchronizers));
    } catch (MalformedObjectNameException e) {
      e.printStackTrace();
      ThreadInfo[] threads = new ThreadInfo[]{};
      return Arrays.asList(threads);
    }
//    return threads;
  }
  
  public ThreadInfo getThread(int threadId) {
    try {
      ThreadMXBean threadMXBean = JMX.newMXBeanProxy(connection, new ObjectName("java.lang:type=Threading"), ThreadMXBean.class);
      return threadMXBean.getThreadInfo(threadId);
    } catch (MalformedObjectNameException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public Map<String, MeterMBean> getBrokerTopicsMetrics() {
    return getBrokerServerMetricsByType("BrokerTopicMetrics");
  }

  public Map<String, MeterMBean> getBrokerReplicaManagerMetrics() {
    return getBrokerServerMetricsByType("ReplicaManager");
  }
  
  public Map<String, MeterMBean> getSessionExpireListenerMetrics() {
    return getBrokerServerMetricsByType("SessionExpireListener");
  }
  
  public int getBrokerState() {
    return (int)getGaugeByName("kafka.server:type=KafkaServer,name=BrokerState");
  }
  
  public Object getGaugeByName(String name) {
    try {
      return getGaugeByName(new ObjectName(name));
    } catch (MalformedObjectNameException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  public Object getGaugeByName(ObjectName objectName) {
    GaugeMBean gauge = JMX.newMBeanProxy(connection, objectName, GaugeMBean.class);
    return gauge.getValue();
  }
  
  public Map<String, HistogramMBean> getRequestMetrics() {
    Map<String, HistogramMBean> result = new HashMap<>();
    String exceptionString = "RequestsPerSec";
    getObjectNamesByPattern("kafka.network:type=RequestMetrics,*").forEach(objectName -> {
      String name = objectName.getKeyProperty("name");
      String request = objectName.getKeyProperty("request");
      if (name.equals(exceptionString)) return;
      HistogramMBean histogram = JMX.newMBeanProxy(connection, objectName, HistogramMBean.class);
      result.put(name + "." + request, histogram);
    });
    return result;
  }
  
  public Map<Integer, Map<String, Double>> getSocketServerMetrics() {
    Map<Integer, Map<String, Double>> result = new HashMap<>();
    getObjectNamesByPattern("kafka.server:type=socket-server-metrics,networkProcessor=*").forEach(objectName -> {
      Map<String, Double> socketProperty = new HashMap<>();
      result.put(Integer.valueOf(objectName.getKeyProperty("networkProcessor")), socketProperty);
      try {
        Arrays.asList(connection.getMBeanInfo(objectName).getAttributes()).forEach(attr -> {
          try {
            String key = attr.getName();
            double value = (double)connection.getAttribute(objectName, key);
            socketProperty.put(key, value);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }

    });
    return result;
  }
  
  public Map<String, MeterMBean> getRequestPerSecMetrics() {
    Map<String, MeterMBean> result = new HashMap<>();
    getObjectNamesByPattern("kafka.network:type=RequestMetrics,name=RequestsPerSec,*").forEach(objectName -> {
      MeterMBean meter = JMX.newMBeanProxy(connection, objectName, MeterMBean.class);
      result.put(objectName.getKeyProperty("request"), meter);
    });
    return result;
  }
  
  public Map<String, MeterMBean> getBrokerServerMetricsByType(String type) {
    Map<String, MeterMBean> result = new HashMap<>();
    getObjectNamesByPattern("kafka.server:type=" + type + ",*").forEach(objectName -> {
      String name = objectName.getKeyProperty("name");
      MeterMBean meter = JMX.newMBeanProxy(connection, objectName, MeterMBean.class);
      result.put(name, meter);
    });
    return result;
  }
  
  public Set<ObjectName> getAllObjectNames() {
    return getObjectNamesByPattern("*:*");
  }
  
  public Set<ObjectName> getObjectNamesByPattern(String pattern) {
    try {
      return connection.queryNames(new ObjectName(pattern), null);
    } catch (IOException | MalformedObjectNameException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  public Map<String, Object> getAttributeByObjectName(ObjectName objectName) {
    Map<String, Object> result = new HashMap<>();
    try {
      Arrays.asList(connection.getMBeanInfo(objectName).getAttributes()).forEach(attr -> {
        String key = attr.getName();
        try {
          Object value = connection.getAttribute(objectName, key);
          result.put(key, value);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
  
  public MeterMBean getMeterByObjectName(ObjectName objectName) {
    return JMX.newMBeanProxy(connection, objectName, MeterMBean.class);
  }
  
  public GaugeMBean getGaugeByObjectName(ObjectName objectName) {
    return JMX.newMBeanProxy(connection, objectName, GaugeMBean.class);
  }

  public HistogramMBean getHistogramByObjectName(ObjectName objectName) {
    return JMX.newMBeanProxy(connection, objectName, HistogramMBean.class);
  }
  
  public TimerMBean getTimerMBean(ObjectName objectName) {
    return JMX.newMBeanProxy(connection, objectName, TimerMBean.class);
  }
  
  public String getClassName(ObjectName objectName) {
    try {
      return connection.getMBeanInfo(objectName).getClassName();
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }
  
  public Map<String, KafkaDelayedProperty> getDelayOperations() {
    Map<String, KafkaDelayedProperty> result = new HashMap<>();
    result.put("Fetch", new KafkaDelayedProperty());
    result.put("Produce", new KafkaDelayedProperty());
    result.put("Heartbeat", new KafkaDelayedProperty());
    result.put("Rebalance", new KafkaDelayedProperty());
    getObjectNamesByPattern("kafka.server:type=DelayedOperationPurgatory,name=PurgatorySize,delayedOperation=*").forEach(objectName -> {
      int value = (Integer)getGaugeByName(objectName);
      result.get(objectName.getKeyProperty("delayedOperation")).setPurgatorySize(value);
    });
    getObjectNamesByPattern("kafka.server:type=DelayedOperationPurgatory,name=NumDelayedOperations,delayedOperation=*").forEach(objectName -> {
      int value = (Integer)getGaugeByName(objectName);
      result.get(objectName.getKeyProperty("delayedOperation")).setNumDelayedOperations(value);
    });
    getObjectNamesByPattern("kafka.server:type=*").forEach(objectName -> {
      if (result.get(objectName.getKeyProperty("type")) == null) return;
      try {
        double value = (double)connection.getAttribute(objectName, "queue-size");
        result.get(objectName.getKeyProperty("type")).setValue(value);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    return result;
  }
  
  public void init(String jmxUrl) {
    try {
      jmxConnection = JMXConnectorFactory.connect(new JMXServiceURL(jmxUrl));
      connection = jmxConnection.getMBeanServerConnection();
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  public Map<TopicPartition, Long> getTopicPartitionsLogStartOffsetMetrics() {
    return getTopicPartitionsOffsetMetrics("log", "Log", "LogStartOffset");
  }
  public Map<TopicPartition, Long> getTopicPartitionsLogEndOffsetMetrics() {
    return getTopicPartitionsOffsetMetrics("log", "Log", "LogEndOffset");
  }
  public Map<TopicPartition, Long> getTopicPartitionsSizeMetrics() {
    return getTopicPartitionsOffsetMetrics("log", "Log", "Size");
  }
  public Map<TopicPartition, Long> getTopicPartitionsNumLogSegmentsMetrics() {
    return getTopicPartitionsOffsetMetrics("log", "Log", "NumLogSegments");
  }
  public Map<TopicPartition, Long> getTopicPartitionsUnderReplicatedMetrics() {
    return getTopicPartitionsOffsetMetrics("cluster", "TopicPartitionModel", "UnderReplicated");
  }
  
  protected Map<TopicPartition, Long> getTopicPartitionsOffsetMetrics(String className, String type, String name) {
    Map<TopicPartition, Long> result = new HashMap<>();
//    Pattern regex = Pattern.compile("topic=(.*),partition=(.*)");
    getObjectNamesByPattern("kafka." + className + ":type=" + type + ",name=" + name + ",topic=*,partition=*").forEach(objectName -> {
//      Matcher matcher = regex.matcher(objectName.toString());
//      matcher.find();
      String topic = objectName.getKeyProperty("topic");
      int partition = Integer.valueOf(objectName.getKeyProperty("partition"));
      Long offset = ((Number)getGaugeByName(objectName)).longValue();
      result.put(new TopicPartition(topic, partition), offset);
    });
    return result;
  }
  
  public void close() {
    if (this.jmxConnection != null) {
      try {
        this.jmxConnection.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      this.connection = null;
      this.jmxConnection = null;
    }
  }
  
  public void init(int pid) {
    try {
      init(ConnectorAddressLink.importFrom(pid));
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
