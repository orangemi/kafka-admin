package com.teambition.kafka.model;

import kafka.admin.AdminUtils;
import kafka.api.LeaderAndIsr;
import kafka.cluster.Broker;
import kafka.common.Topic;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import scala.Option;
import scala.collection.*;
import scala.collection.convert.WrapAsJava$;
import scala.collection.Map;

import java.util.*;
import java.util.Map.Entry;

public class Model {
  private static Model instance;
  private ZkUtils zkUtils;
  
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
      = zkUtils.getPartitionAssignmentForTopics(JavaConversions.asScalaBuffer(Arrays.asList(topic)))
      .get(topic).get();
    for (Entry<Object, Seq<Object>> partitionSet : JavaConversions.mapAsJavaMap(partitionMap).entrySet()) {
      int partitionId = (Integer) partitionSet.getKey();
  
      Collection<PartitionReplica> replicas = new Vector<>();
      Partition partition = new Partition(partitionId, replicas);
      partitions.add(partition);
  
      Option<LeaderAndIsr> leaderAndIsrOpt = zkUtils.getLeaderAndIsrForPartition(topic, partitionId);
      if (leaderAndIsrOpt.isEmpty()) continue;

      LeaderAndIsr leaderAndIsr = leaderAndIsrOpt.get();
      partition.setLeader(leaderAndIsr.leader());

      for (Object brokerObj: JavaConversions.asJavaCollection(partitionSet.getValue())) {
        int broker = (Integer) brokerObj;

        PartitionReplica replica = new PartitionReplica(
          broker,
          broker == leaderAndIsr.leader(),
          leaderAndIsr.isr().toSet().contains(broker)
        );
        replicas.add(replica);
      }
    }
    return partitions;
  }
  
  public Collection<String> getConsumerGroups() {
    return JavaConversions.asJavaCollection(zkUtils.getConsumerGroups());
  }
  
  public Collection<String> getConsumerGroupTopic(String group) {
    return null;
  }
  
  private Model() {
    // TODO: should load config properties
    zkUtils = ZkUtils.apply("localhost:2181", 3000, 3000, false);
  }
}
