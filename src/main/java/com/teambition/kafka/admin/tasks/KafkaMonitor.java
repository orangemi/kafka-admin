package com.teambition.kafka.admin.tasks;

import com.teambition.kafka.admin.model.KafkaBrokerJmxClient;
import com.teambition.kafka.admin.model.Model;
import com.teambition.kafka.admin.model.TopicPartitionModel;
import com.yammer.metrics.reporting.JmxReporter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class KafkaMonitor extends TimerTask {
  
  private static KafkaMonitor instance;
  private Properties properties;
  private boolean enable = true;
  private boolean running = false;
  private int round = 0;
  private String dbUrl = "http://localhost:8086";
  private String dbUser = "root";
  private String dbPassword = "root";
  private String dbName = "default";
  private InfluxDB db = null;
  private BatchPoints batchPoints;
  private int internal_time = 10000; // 10 seconds
  private Timer timer;
  
  public static void main(String[] argv) {
    KafkaMonitor monitor = new KafkaMonitor();
    monitor.start();
  }
  
  public static KafkaMonitor getInstance(Properties props) {
    if (instance == null) {
      instance = new KafkaMonitor(props);
    }
    return instance;
  }
  
  public static KafkaMonitor getInstance() {
    return instance;
  }
  
  private KafkaMonitor() {
    properties = new Properties();
  }
  
  private KafkaMonitor(Properties properties) {
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
  
  public InfluxDB connect() {
    if (db != null) return db;
    db = InfluxDBFactory.connect(dbUrl, dbUser, dbPassword);
    
    // Try with database
    Query query = new Query("SELECT * FROM cpu GROUP BY *", dbName);
    QueryResult result = db.query(query);

    if (result.hasError()) throw new RuntimeException(result.getError());

    if (result.getResults().get(0).hasError()) {
      String err = result.getResults().get(0).getError();
      if (err.startsWith("database not found")) {
        // Try create db
        db.createDatabase(dbName);
      } else {
        throw new RuntimeException(err);
      }
    }

    return db;
  }
  
  public String getDbName() {
    return dbName;
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
    Model.getInstance().getConsumerManager().getConsumerList().forEach((group, consumer) -> {
      consumer.getOffsets().forEach((topic, paritionOffsets) -> {
        paritionOffsets.forEach((partition, offset) -> {
          batchPoints.point(Point.measurement("consumer-offsets")
            .tag("group", group)
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
        try {
          String className = jmx.getClassName(objectName);
          String name = objectName.getKeyProperty("name");
          String type = objectName.getKeyProperty("type");
          if (className.equals("com.yammer.metrics.reporting.JmxReporter$Meter")) {
            JmxReporter.MeterMBean meter = jmx.getMeterByObjectName(objectName);
            batchPoints.point(Point.measurement(name)
              .tag("broker", brokerId)
              .tag("type", type)
              .addField("MeanRate", meter.getMeanRate())
              .addField("OneMinuteRate", meter.getOneMinuteRate())
              .addField("Count", meter.getCount())
              .build());
          } else if (className.equals("com.yammer.metrics.reporting.JmxReporter$Gauge")) {
            try {
              Number number = (Number) jmx.getGaugeByName(objectName);
              batchPoints.point(Point.measurement(name)
                .tag("broker", brokerId)
                .tag("type", type)
                .addField("Value", number)
                .build());
            } catch (ClassCastException e) {
              System.out.println("Cast Gauge Fail: " + objectName);
              batchPoints.point(Point.measurement(name)
                .tag("broker", brokerId)
                .tag("type", type)
                .addField("Value", jmx.getGaugeByName(objectName).toString())
                .build());
              
//              e.printStackTrace();
            }
          } else {
            // TODO: Unknown objectName type
            System.out.println("Unknown class: " + className + " for object: " + objectName);
          }
        } catch (Exception e) {
          // ignore unknown expection
          e.printStackTrace();
        }
      });
      
      // Network (type=RequestMetrics)
      jmx.getObjectNamesByPattern("kafka.*:type=RequestMetrics,name=*,request=*").forEach(objectName -> {
        try {
          String name = objectName.getKeyProperty("name");
          String request = objectName.getKeyProperty("request");
          String className = jmx.getClassName(objectName);
          if (className.equals("com.yammer.metrics.reporting.JmxReporter$Histogram")) {
            JmxReporter.HistogramMBean meter = jmx.getHistogramByObjectName(objectName);
            batchPoints.point(Point
              .measurement(name)
              .tag("broker", brokerId)
              .tag("request", request)
//              .tag("type", type)
              .addField("Mean", meter.getMean())
              .addField("Count", meter.getCount())
              .addField("50thPercentile", meter.get50thPercentile())
              .addField("99thPercentile", meter.get99thPercentile())
              .build());
          } else if (className.equals("com.yammer.metrics.reporting.JmxReporter$Meter")) {
            JmxReporter.MeterMBean meter = jmx.getMeterByObjectName(objectName);
            batchPoints.point(Point
              .measurement(name)
              .tag("broker", brokerId)
              .tag("request", request)
//              .tag("type", type)
              .addField("MeanRate", meter.getMeanRate())
              .addField("OneMinuteRate", meter.getOneMinuteRate())
              .addField("Count", meter.getCount())
              .build());
          } else {
            // TODO: Unknown objectName type
            System.out.println("Unknown class: " + className + " for object: " + objectName);
          }
        } catch (Exception e) {
          // ignore uknown expection
          e.printStackTrace();
        }
      });
  
      // Producers
      // TODO: kafka.server:type=Produce,client-id=DemoProducer
      
      // TopicModel Metrics
      jmx.getObjectNamesByPattern("kafka.*:type=*,name=*,topic=*").forEach(objectName -> {
        try {
          String type = objectName.getKeyProperty("type");
          String topic = objectName.getKeyProperty("topic");
          String name = objectName.getKeyProperty("name");
          JmxReporter.MeterMBean meter = jmx.getMeterByObjectName(objectName);
          batchPoints.point(Point.measurement(name)
            .tag("broker", brokerId)
            .tag("type", type)
            .tag("topic", topic)
            .addField("Count", meter.getCount())
            .addField("MeanRate", meter.getMeanRate())
            .addField("OneMinuteRate", meter.getOneMinuteRate())
            .build());
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
  
      // TopicModel TopicPartitionModel Metrics
      jmx.getObjectNamesByPattern("kafka.*:type=*,name=*,topic=*,partition=*").forEach(objectName -> {
        String type = objectName.getKeyProperty("type");
        String topic = objectName.getKeyProperty("topic");
        String partition = objectName.getKeyProperty("partition");
        String name = objectName.getKeyProperty("name");
        JmxReporter.GaugeMBean gauge = jmx.getGaugeByObjectName(objectName);
        try {
          Number value = (Number)gauge.getValue();
          batchPoints.point(Point.measurement(name)
            .tag("broker", brokerId)
            .tag("type", type)
            .tag("topic", topic)
            .tag("partition", partition)
            .addField("Value", value)
            .build());
        } catch (ClassCastException e) {
          System.out.println("Cast Gauge Fail: " + objectName);
          batchPoints.point(Point.measurement(name)
            .tag("broker", brokerId)
            .tag("type", type)
            .tag("topic", topic)
            .tag("partition", partition)
            .addField("Value", jmx.getGaugeByName(objectName).toString())
            .build());
        }
      });

      // All Metrics recoreded.
      jmx.close();
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
