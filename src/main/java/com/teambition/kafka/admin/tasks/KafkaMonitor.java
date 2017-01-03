package com.teambition.kafka.admin.tasks;

import com.teambition.kafka.admin.model.KafkaBrokerJmxClient;
import com.teambition.kafka.admin.model.Model;
import com.yammer.metrics.reporting.JmxReporter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class KafkaMonitor extends TimerTask {
  
  private Properties properties;
  private boolean enable = true;
  private int round = 0;
  private String dbUrl = "http://localhost:8086";
  private String dbUser = "root";
  private String dbPassword = "root";
  private String dbName = "default2";
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
    round++;
    connect();
    System.out.println("Start gather info (round: " + round + ") ...");
    batchPoints = BatchPoints
      .database(dbName)
      .retentionPolicy("autogen")
      .consistency(InfluxDB.ConsistencyLevel.ALL)
      .build();
  
    System.out.println("gather broker info(round: " + round + ") ...");
    logBrokers();
  
    System.out.println("gather topic info(round: " + round + ") ...");
    logTopicCount();

    // Topic Partition
    Model.getInstance().getTopicCollections().forEach(topic -> {
      Model.getInstance().getTopicPartitions(topic).forEach(partition -> {
        batchPoints.point(Point.measurement("topic-start-offsets")
          .tag("topic", topic)
          .tag("partition", "" + partition.getId())
          .addField("offset", partition.getBeginOffset())
          .build()
        );
        batchPoints.point(Point.measurement("topic-end-offsets")
          .tag("topic", topic)
          .tag("partition", "" + partition.getId())
          .addField("offset", partition.getEndOffset())
          .build()
        );

      });
    });
  
    System.out.println("gather consumer info(round: " + round + ") ...");
  
    // Consumers2
    Model.getInstance().getConsumerV2s().forEach(consumer -> {
      Model.getInstance().getConsumerV2(consumer).getOffsets().forEach((topicPartition, offset) -> {
        batchPoints.point(Point.measurement("consumer-offsets")
          .tag("group", consumer)
          .addField("offset", offset)
          .build()
        );
      });
    });
  
    // Consumer
    Model.getInstance().getConsumerGroups().forEach(consumer -> {
      Model.getInstance().getZkConsumerGroup(consumer).getOffsets().forEach((topicPartition, offset) -> {
        batchPoints.point(Point.measurement("consumer-offsets")
          .tag("group", consumer)
          .addField("offset", offset)
          .build()
        );
      });
    });
  
    db.write(batchPoints);
  }
  
  private Point getMeterPoint(JmxReporter.MeterMBean meter, String name, Point.Builder builder) {
    return builder
      .addField(name + "-MeanRate", meter.getMeanRate())
      .addField(name + "-Count", meter.getCount())
      .addField(name + "-OneMinuteRate", meter.getOneMinuteRate())
      .addField(name + "-FiveMinuteRate", meter.getFiveMinuteRate())
      .addField(name + "-FifteenMinuteRate", meter.getFifteenMinuteRate())
      .build();
  }
  
  private Point getTopicPointByMeasure(KafkaBrokerJmxClient jmx, int brokerId, String name) {
    Point.Builder pointBuilder = Point.measurement("kafka-broker-" + name);
    jmx.getObjectNamesByPattern("kafka.*:type=*,name=" + name + ",topic=*").forEach(objectName -> {
      String topic = objectName.getKeyProperty("topic");
      JmxReporter.MeterMBean meter = jmx.getMeterByObjectName(objectName);
      pointBuilder
        .tag("broker", String.valueOf(brokerId))
        .tag("topic", topic)
        .addField("MeanRate", meter.getMeanRate())
        .addField("Count", meter.getCount())
        .addField("OneMinuteRate", meter.getOneMinuteRate())
        .addField("FiveMinuteRate", meter.getFiveMinuteRate())
        .addField("FifteenMinuteRate", meter.getFifteenMinuteRate());
    });
    return pointBuilder.build();
  }
  
  private Point getTopicPartitionPointByMeasure(KafkaBrokerJmxClient jmx, int brokerId, String name) {
    Point.Builder pointBuilder = Point.measurement("kafka-broker-" + name);
    jmx.getObjectNamesByPattern("kafka.*:type=*,name=" + name + ",topic=*,partition=*").forEach(objectName -> {
      String topic = objectName.getKeyProperty("topic");
      String partition = objectName.getKeyProperty("partition");
      pointBuilder
        .tag("broker", String.valueOf(brokerId))
        .tag("topic", topic)
        .tag("partition", partition)
        .addField(name, (Number)jmx.getGaugeByObjectName(objectName).getValue());
    });
    return pointBuilder.build();
    
  }
  
  public void logBrokers() {
    // broker count
    batchPoints.point(Point.measurement("kafka-broker")
      .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
      .addField("count", Model.getInstance().getBrokerCollections().size())
      .build());
  
    Model.getInstance().getBrokerCollections().forEach(id -> {
      KafkaBrokerJmxClient jmx = Model.getInstance().getKafkaBrokerJmxClient(id);

      batchPoints.point(getTopicPointByMeasure(jmx, id, "BytesInPerSec"));
      batchPoints.point(getTopicPointByMeasure(jmx, id, "TotalProduceRequestsPerSec"));
      batchPoints.point(getTopicPointByMeasure(jmx, id, "FailedProduceRequestsPerSec"));
      batchPoints.point(getTopicPointByMeasure(jmx, id, "TotalFetchRequestsPerSec"));
      batchPoints.point(getTopicPointByMeasure(jmx, id, "BytesOutPerSec"));
      batchPoints.point(getTopicPointByMeasure(jmx, id, "MessagesInPerSec"));
      batchPoints.point(getTopicPointByMeasure(jmx, id, "FailedFetchRequestsPerSec"));
      batchPoints.point(getTopicPointByMeasure(jmx, id, "BytesRejectedPerSec"));
      
      batchPoints.point(getTopicPartitionPointByMeasure(jmx, id, "LogStartOffset"));
      batchPoints.point(getTopicPartitionPointByMeasure(jmx, id, "LogEndOffset"));
      batchPoints.point(getTopicPartitionPointByMeasure(jmx, id, "NumLogSegments"));
      batchPoints.point(getTopicPartitionPointByMeasure(jmx, id, "Size"));
      batchPoints.point(getTopicPartitionPointByMeasure(jmx, id, "UnderReplicated"));
      
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
