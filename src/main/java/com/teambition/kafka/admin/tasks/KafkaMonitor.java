package com.teambition.kafka.admin.tasks;

import com.teambition.kafka.admin.model.KafkaBrokerJmxClient;
import com.teambition.kafka.admin.model.Model;
import com.teambition.kafka.admin.model.TopicPartitionModel;
import com.yammer.metrics.reporting.JmxReporter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class KafkaMonitor extends TimerTask {
  
  private Properties properties;
  private boolean enable = true;
  private boolean running = false;
  private int round = 0;
  private String dbUrl = "http://localhost:8086";
  private String dbUser = "root";
  private String dbPassword = "root";
  private String dbName = "default";
  private InfluxDB db;
  private BatchPoints batchPoints;
  private int internal_time = 10000; // 10 seconds
  private Timer timer;
  
  public static void main(String[] argv) {
    KafkaMonitor monitor = new KafkaMonitor();
    monitor.start();
  }
  
  public KafkaMonitor() {
    properties = new Properties();
  }
  
  public KafkaMonitor(Properties properties) {
    this.properties = properties;
    enable = properties.getProperty("monitor.enable", "true").equals("true");
    dbUrl = properties.getProperty("influxdb.url", "http://localhost:8086");
    dbUser = properties.getProperty("influxdb.user", "root");
    dbPassword = properties.getProperty("influxdb.password", "root");
    internal_time = Integer.valueOf(properties.getProperty("influxdb.internal.ms", "10000"));
    dbName = properties.getProperty("influxdb.db", "default");
  }
  
  public void start() {
    if (!enable) return;
    timer = new Timer();
    timer.schedule(this, 0, internal_time);
  }
  
  public void connect() {
    if (db != null) return;
    db = InfluxDBFactory.connect(dbUrl, dbUser, dbPassword);
    db.createDatabase(dbName);
  }
  
  @Override
  public void run() {
    try {
      if (running) return;
      running = true;
      _run();
      running = false;
    } catch (Exception ex) {
      ex.printStackTrace();
      db = null;
      running = false;
    }
  }
  
  public void _run() {
    round++;
    connect();
    System.out.println("Start gather info (round: " + round + ") ...");
    batchPoints = BatchPoints
      .database(dbName)
      .consistency(InfluxDB.ConsistencyLevel.ALL)
      .build();
  
    logTopicCount();
    
    // TopicModel TopicPartitionModel
    Model.getInstance().getTopicCollections().forEach(topic -> {
      int partitions = Model.getInstance().getTopicPartitions(topic);
      for (int partition = 0; partition < partitions; partition++) {
        TopicPartitionModel topicPartitionModel = Model.getInstance().getTopicPartition(topic, partition);
        batchPoints.point(Point.measurement("topic-offsets")
          .tag("topic", topic)
          .tag("partition", String.valueOf(partition))
          .addField("beginOffset", topicPartitionModel.getBeginOffset())
          .addField("endOffset", topicPartitionModel.getEndOffset())
          .build()
        );
      }
    });
  
    // Consumers2
    Model.getInstance().getConsumerV2s().forEach(consumer -> {
      Model.getInstance().getConsumerV2(consumer).getOffsets().forEach((topic, paritionOffsets) -> {
        paritionOffsets.forEach((partition, offset) -> {
          batchPoints.point(Point.measurement("consumer-offsets")
            .tag("group", consumer)
            .tag("topic", topic)
            .tag("partition", String.valueOf(partition))
            .addField("offset", offset)
            .build()
          );
        });
      });
    });
  
    // ConsumerModel
    Model.getInstance().getConsumerGroups().forEach(consumer -> {
      Model.getInstance().getZkConsumerGroup(consumer).getOffsets().forEach((topic, partitionOffsets) -> {
        partitionOffsets.forEach((partition, offset) -> {
          batchPoints.point(Point.measurement("consumer-offsets")
            .tag("group", consumer)
            .tag("topic", topic)
            .tag("partition", String.valueOf(partition))
            .addField("offset", offset)
            .build()
          );
        });
      });
    });
  
    logBrokers();
    db.write(batchPoints);
    running = false;
  }
  
  public void logBrokers() {
    // broker count
    batchPoints.point(Point.measurement("kafka-broker")
      .addField("count", Model.getInstance().getBrokerCollections().size())
      .build());
  
    Model.getInstance().getBrokerCollections().forEach(id -> {
      String brokerId = String.valueOf(id);
      KafkaBrokerJmxClient jmx = Model.getInstance().getKafkaBrokerJmxClient(id);
  
      jmx.getObjectNamesByPattern("kafka.*:type=*,name=*").forEach(objectName -> {
        String className = jmx.getClassName(objectName);
        String name = objectName.getKeyProperty("name");
        String type = objectName.getKeyProperty("type");
        if (className.equals("com.yammer.metrics.reporting.JmxReporter$Meter")) {
          batchPoints.point(Point.measurement("kafka-broker")
            .tag("broker", brokerId)
            .tag("type", type)
            .addField(name, jmx.getMeterByObjectName(objectName).getOneMinuteRate())
            .build());
        } else if (className.equals("com.yammer.metrics.reporting.JmxReporter$Gauge")) {
          try {
            Number number = (Number)jmx.getGaugeByName(objectName);
            batchPoints.point(Point.measurement("kafka-broker")
              .tag("broker", brokerId)
              .tag("type", type)
              .addField(name, number)
              .build());
          } catch (ClassCastException e) {}
        } else {
          // TODO: Unknown objectName type
          System.out.println("Unknown class: " + className + " for object: " + objectName);
        }
      });
      
      // Network (type=RequestMetrics)
      jmx.getObjectNamesByPattern("kafka.*:type=RequestMetrics,name=*,request=*").forEach(objectName -> {
        String name = objectName.getKeyProperty("name");
        String request = objectName.getKeyProperty("request");
        String className = jmx.getClassName(objectName);
        if (className.equals("com.yammer.metrics.reporting.JmxReporter$Histogram")) {
          batchPoints.point(Point
            .measurement("kafka-broker-network")
            .tag("broker", brokerId)
            .tag("request", request)
            .addField(name, jmx.getHistogramByObjectName(objectName).getMean())
            .build());
        } else if (className.equals("com.yammer.metrics.reporting.JmxReporter$Meter")) {
          batchPoints.point(Point
            .measurement("kafka-broker-network")
            .tag("broker", brokerId)
            .tag("request", request)
            .addField(name, jmx.getMeterByObjectName(objectName).getMeanRate())
            .build());
        } else {
          // TODO: Unknown objectName type
          System.out.println("Unknown class: " + className + " for object: " + objectName);
        }
      });
  
      // Producers
      // TODO: kafka.server:type=Produce,client-id=DemoProducer
      
      
      // TopicModel Metrics
      Map<String, Point.Builder> topicPointMap = new HashMap<>();
      jmx.getObjectNamesByPattern("kafka.*:type=*,name=*,topic=*").forEach(objectName -> {
        String type = objectName.getKeyProperty("type");
        String topic = objectName.getKeyProperty("topic");
        String name = objectName.getKeyProperty("name");
        JmxReporter.MeterMBean meter = jmx.getMeterByObjectName(objectName);
        if (!topicPointMap.containsKey(topic)) {
          topicPointMap.put(topic, Point
            .measurement("kafka-broker-topic")
            .tag("broker", brokerId)
            .tag("type", type)
            .tag("topic", topic));
        }
        topicPointMap.get(topic).addField(name, meter.getMeanRate());
      });
      topicPointMap.forEach((topic, pointBuilder) -> {
        batchPoints.point(pointBuilder.build());
      });
  
      // TopicModel TopicPartitionModel Metrics
      Map<String, Point.Builder> topicPartitionPointMap = new HashMap<>();
      jmx.getObjectNamesByPattern("kafka.*:type=*,name=*,topic=*,partition=*").forEach(objectName -> {
        String type = objectName.getKeyProperty("type");
        String topic = objectName.getKeyProperty("topic");
        String partition = objectName.getKeyProperty("partition");
        String name = objectName.getKeyProperty("name");
        JmxReporter.GaugeMBean gauge = jmx.getGaugeByObjectName(objectName);
        Point.Builder pointBuilder;
        String key = topic + "." + partition;
        if (!topicPartitionPointMap.containsKey(key)) {
          pointBuilder = Point
            .measurement("kafka-broker-topic-partition")
            .tag("broker", brokerId)
            .tag("type", type)
            .tag("topic", topic)
            .tag("partition", partition);
          topicPartitionPointMap.put(key, pointBuilder);
        } else {
          pointBuilder = topicPartitionPointMap.get(key);
        }
        pointBuilder.addField(name, (Number)gauge.getValue());
      });
      topicPartitionPointMap.forEach((topicPartition, pointBuilder) -> {
        batchPoints.point(pointBuilder.build());
      });
    });
  }
  
  public void logTopicCount() {
    Point point = Point.measurement("kafka-topic")
      .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
      .addField("count", Model.getInstance().getTopicCollections().size())
      .build();
    batchPoints.point(point);
  }
}
