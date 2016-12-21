package com.teambition.kafka.model;

import kafka.admin.AdminClient;
import kafka.admin.AdminUtils;
import kafka.api.LeaderAndIsr;
import kafka.cluster.Broker;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.zookeeper.data.Stat;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.*;
import java.util.Vector;

public class Model {
  private static final String ADMIN_CONSUMER_GROUP_NAME = "kafka-admin-consumer";
  private static final String ZKHost = "localhost:2181";
  private static final String KafkaHost = "localhost:9092";
// private static final ZKHost = "kafka01-cn.teambition.corp:2181";
// private static final KafkaHost = "kafka01-cn.teambition.corp:9092";
  
  private static Model instance;
  private ZkUtils zkUtils;
  private AdminClient adminClient;
//  private Consumer<String, String> consumer;
  
  public static Model getInstance() {
    if (instance == null) instance = new Model();
    return instance;
  }
  
  public Collection<Broker> getBrokerCollections() {
    return JavaConversions.asJavaCollection(zkUtils.getAllBrokersInCluster());
  }
  
  public Collection<String> getTopicCollections() {
    return JavaConversions.asJavaCollection(zkUtils.getAllTopics());
  }
  
  public Properties getTopicConfig(String topic) {
    Properties configs = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), topic);
    return configs;
  }
  public Collection<Partition> getTopicPartitions(String topic) {
    Collection<Partition> partitions = new Vector<>();
    Map<Object, Seq<Object>> partitionMap
      = JavaConversions.mapAsJavaMap(
        zkUtils.getPartitionAssignmentForTopics(JavaConversions.asScalaBuffer(Arrays.asList(topic)))
          .get(topic)
          .get());
    partitionMap.forEach((key, value) -> {
      int id = (Integer)key;

      // get partition end offset
      long beginOffset = getTopicPartitionOffset(topic, id, true);
      long endOffset = getTopicPartitionOffset(topic, id);
  
      Collection<PartitionReplica> replicas = new Vector<>();
      Partition partition = new Partition(id, beginOffset, endOffset, replicas);
      partitions.add(partition);
      
      Option<LeaderAndIsr> leaderAndIsrOpt = zkUtils.getLeaderAndIsrForPartition(topic, id);
      if (leaderAndIsrOpt.isEmpty()) return;

      LeaderAndIsr leaderAndIsr = leaderAndIsrOpt.get();
      partition.setLeader(leaderAndIsr.leader());
  
      JavaConversions.asJavaCollection(value).forEach(brokerObj -> {
        int broker = (Integer)brokerObj;
        PartitionReplica replica = new PartitionReplica(
          broker,
          broker == leaderAndIsr.leader(),
          leaderAndIsr.isr().toSet().contains(broker)
        );
        replicas.add(replica);
      });
    });

    return partitions;
  }
  
  public Collection<String> getConsumerGroups() {
    return JavaConversions.asJavaCollection(zkUtils.getConsumerGroups());
  }
  
  public Collection<String> getZkConsumerGroupsByTopic(String topic) {
    Collection<String> consumers = new Vector<>();
    getZookeeperChildren("/consumers").stream()
      .filter(consumer -> getZookeeperChildren("/consumers/" + consumer + "/offsets").contains(topic))
      .forEach(consumer -> consumers.add(consumer));
    return consumers;
  }

  @Deprecated
  public Collection<String> getZkConsumerGroupsByTopicOrigin(String topic) {
    return JavaConversions.asJavaCollection(zkUtils.getAllConsumerGroupsForTopic(topic));
  }
  
  public Collection<String> getZkTopicsByConsumerGroup(String group) {
    return getZookeeperChildren("/consumers/" + group + "/offsets");
  }
  
  @Deprecated
  public Collection<String> getZkTopicsByConsumerGroupOrigin(String group) {
    return JavaConversions.asJavaCollection(zkUtils.getTopicsByConsumerGroup(group));
  }
  
  public long getTopicPartitionOffset(String topic, int parititon) {
    return getTopicPartitionOffset(topic, parititon, false);
  }
  
  public long getTopicPartitionOffset(String topic, int partition, boolean seekBeginning) {
    Consumer<String, String> consumer = createConsumer(ADMIN_CONSUMER_GROUP_NAME);
    TopicPartition topicPartition = new TopicPartition(topic, partition);
    Collection<TopicPartition> topicPartitions = new Vector<>();
    topicPartitions.add(topicPartition);
    consumer.assign(topicPartitions);
    if (seekBeginning) {
      consumer.seekToBeginning(topicPartitions);
    } else {
      consumer.seekToEnd(topicPartitions);
    }
    long offset = consumer.position(topicPartition);
    consumer.close();
    return offset;
  }
  
  public com.teambition.kafka.model.Consumer getZkConsumerGroup(String group) {
    com.teambition.kafka.model.Consumer consumerModel = new com.teambition.kafka.model.Consumer(group);
    Collection<String> topics = getZookeeperChildren("/consumers/" + group + "/offsets");
    topics.forEach(topic -> {
      Collection<String> partitions = getZookeeperChildren("/consumers/" + group + "/offsets/" + topic);
      partitions.forEach(partitionString -> {
        int partition = Integer.valueOf(partitionString);
        String offsetString = getZookeeperData("/consumers/" + group + "/offsets/" + topic + "/" + partitionString);
        long offset = Long.valueOf(offsetString);
        System.out.println("topic, partition, offset: " + topic + "," + partition + "," + offset);
        consumerModel.addTopicPartition(topic, partition, offset);
      });
    });
    return consumerModel;
  }
  
  public Collection<String> getConsumerV2s() {
    Collection<String> consumers = new Vector<>();
    JavaConversions.asJavaCollection(adminClient.listAllConsumerGroupsFlattened()).forEach(consumerGroup -> consumers.add(consumerGroup.groupId()));
    return consumers;
  }
  
  public com.teambition.kafka.model.Consumer getConsumerV2(String group) {
    com.teambition.kafka.model.Consumer consumerModel = new com.teambition.kafka.model.Consumer(group);
    Consumer<String, String> consumer = createConsumer(group);
    JavaConversions.asJavaCollection(adminClient.describeConsumerGroup(group))
      .forEach(consumerSummary -> {
        JavaConversions.asJavaCollection(consumerSummary.assignment()).forEach(topicPartition -> {
          long offset = consumer.committed(new TopicPartition(topicPartition.topic(), topicPartition.partition())).offset();
          consumerModel.addTopicPartition(topicPartition.topic(), topicPartition.partition(), offset);
        });
        
      });
    consumer.close();
    return consumerModel;
  }
  
  public Collection<String> getZookeeperChildren(String path) {
    return JavaConversions.asJavaCollection(zkUtils.getChildren(path));
  }
  
  public Stat getZookeeperStat(String path) {
    return zkUtils.readDataMaybeNull(path)._2();
  }
  public String getZookeeperData(String path) {
    return zkUtils.readDataMaybeNull(path)._1().get();
  }
  
  private Model() {
    // TODO: should load config properties
    zkUtils = ZkUtils.apply(ZKHost, 3000, 3000, false);

    // Create adminClient
    Properties adminProps = new Properties();
    adminProps.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, KafkaHost);
    adminClient = AdminClient.create(adminProps);
  }
  
  private Consumer<String, String> createConsumer(String group) {
    String deserializer = new StringDeserializer().getClass().getName();
  
    // Create Consumer
    Properties consumerProps = new Properties();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaHost);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, group);
    consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
  
    return new KafkaConsumer<>(consumerProps);
  }
}
