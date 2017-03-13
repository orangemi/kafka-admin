package com.teambition.kafka.admin.model;

import kafka.common.ErrorMapping;
import kafka.common.OffsetAndMetadata;
import kafka.common.OffsetMetadata;
import kafka.common.TopicAndPartition;
import kafka.coordinator.GroupMetadataManager;
import kafka.coordinator.GroupTopicPartition;
import kafka.coordinator.OffsetKey;
import kafka.javaapi.OffsetCommitRequest;
import kafka.javaapi.OffsetCommitResponse;
import kafka.network.BlockingChannel;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Arrays;

public class ConsumerManager implements Runnable {
  
  private Map<String, ConsumerModel> consumerMap = new HashMap<>();
  private String kafkaHost;
  private static final String OFFSET_CONSUMER_GROUP = "kafka-admin-offset-reader";
  private Consumer<byte[], byte[]> offsetConsumer;
  
  public ConsumerManager(String kafkaHost) {
    this.kafkaHost = kafkaHost;
  }
  
  public Map<String, ConsumerModel> getConsumerList() {
    return consumerMap;
  }
  
  @Override
  public void run() {
    while (true) {
      fetchOffsets();
    }
  }
  
  public void fetchOffsets() {
    boolean hasRecord = true;
    int count = 0;
    
    while (hasRecord) {
      count++;
      hasRecord = false;
      Iterator<ConsumerRecord<byte[], byte[]>> iterator = getOffsetConsumer().poll(50000).iterator();
      while (iterator.hasNext() && count < 100) {
        hasRecord = true;
        ConsumerRecord<byte[], byte[]> record = iterator.next();
        Object key = GroupMetadataManager.readMessageKey(ByteBuffer.wrap(record.key()));
        if (key instanceof OffsetKey) {
          GroupTopicPartition groupTopicPartition = ((OffsetKey) key).key();
          String group = groupTopicPartition.group();
          if (!consumerMap.containsKey(group)) {
            consumerMap.put(groupTopicPartition.group(), new ConsumerModel(group));
          }

          if (record.value() == null) {
            consumerMap.get(group).addTopicPartition(groupTopicPartition.topicPartition(), 0L);
          } else {
            OffsetAndMetadata value = GroupMetadataManager.readOffsetMessageValue(ByteBuffer.wrap(record.value()));
            consumerMap.get(groupTopicPartition.group()).addTopicPartition(groupTopicPartition.topicPartition(), value.offset());
          }
  
          // remove consumer if consumer's offset all removed
          // do not remove because history need !
//          if (consumerMap.get(group).getOffsets().size() == 0) {
//            consumerMap.remove(group);
//          }
          
//          System.out.println("record: partition: " + record.partition()  + " offset:" + record.offset() + " : " + count);
//          System.out.println("  group: " + ((OffsetKey) key).key().group());
//          System.out.println("  topic: " + ((OffsetKey) key).key().topicPartition().topic());
//          System.out.println("  partition: " + ((OffsetKey) key).key().topicPartition().partition());
//          System.out.println("  offset: " + value.offset());
//          System.out.println("  metadata: " + value.metadata());
//          System.out.println("  commitTime: " + value.commitTimestamp());
//          System.out.println("  expiredTime: " + value.expireTimestamp());
        } else {
          // ignore other value
        }
      }
    }
  }
  
  public void commitOffset2(String group, String clientId, String topic, int partition, long offset) {
    String deserializer = ByteArrayDeserializer.class.getName();
    Properties consumerProps = new Properties();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, group);
    consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//    consumerProps.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, "false");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
  
    KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(consumerProps);
    consumer.assign(Arrays.asList(new TopicPartition(topic, partition)));
  
//    consumer.
//    consumer.seek(new TopicPartition(topic, partition), offset);
    Map<TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> commitMeta = new HashMap<>();
    commitMeta.put(new TopicPartition(topic, partition), new org.apache.kafka.clients.consumer.OffsetAndMetadata(offset));
    consumer.commitSync(commitMeta);
    consumer.close();
  }
  
  public void commitOffset(String group, String clientId, String topic, int partition, long offset) {
    int correlationId = 0;
    long now = System.currentTimeMillis();
    long expired = now + 86400L * 1000;

    Map<TopicAndPartition, OffsetAndMetadata> offsets = new HashMap<>();
    offsets.put(new TopicAndPartition(topic, partition), new OffsetAndMetadata(new OffsetMetadata(offset, ""), now, expired));
    OffsetCommitRequest commitRequest = new OffsetCommitRequest(
      group,
      offsets,
      correlationId++,
      clientId,
      (short) 1 // version
    ); // version 1 and above commit to Kafka, version 0 commits to ZooKeeper
    
    try {
      // TODO: set blockchannel host & port
      BlockingChannel channel = new BlockingChannel("", 9092,
        BlockingChannel.UseDefaultBufferSize(),
        BlockingChannel.UseDefaultBufferSize(),
        5000 /* read timeout in millis */);
      channel.connect();
      channel.send(commitRequest.underlying());
      OffsetCommitResponse commitResponse = OffsetCommitResponse.readFrom(channel.receive().payload());
      if (commitResponse.hasError()) {
        for (Object partitionErrorCode: commitResponse.errors().values()) {
          System.out.println("commit-error: " + partitionErrorCode);
          if ((short)partitionErrorCode == ErrorMapping.OffsetMetadataTooLargeCode()) {
            // You must reduce the size of the metadata if you wish to retry
          } else if ((short)partitionErrorCode == ErrorMapping.NotCoordinatorForConsumerCode() ||
            (short)partitionErrorCode == ErrorMapping.ConsumerCoordinatorNotAvailableCode()) {
            channel.disconnect();
            // Go to step 1 (offset manager has moved) and then retry the commit to the new offset manager
          } else {
            // log and retry the commit
          }
        }
      }
    }
    catch (Exception ioe) {
      ioe.printStackTrace();
    }
  }
  
  private Consumer<byte[], byte[]> getOffsetConsumer() {
    if (offsetConsumer == null) {
      String deserializer = ByteArrayDeserializer.class.getName();
      Properties consumerProps = new Properties();
      consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
      consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, OFFSET_CONSUMER_GROUP);
      consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
      consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
      consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      consumerProps.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, "false");
      consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer);
      consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
  
      offsetConsumer = new KafkaConsumer<>(consumerProps);
      offsetConsumer.subscribe(Arrays.asList("__consumer_offsets"));
    }
    return offsetConsumer;
  }
  
  public static void main(String[] argv) {
    String kafkaHost = "localhost:9092";
    ConsumerManager consumerManager = new ConsumerManager(kafkaHost);
    consumerManager.fetchOffsets();

//    consumerManager.brokerHost = "kafka01-cn.teambition.corp";
//    consumerManager.brokerPort = 9092;
//    consumerManager.commitOffset("connect-pompeii-postgresql-sink-connector", "", "core", 0, 2172559353L);
  }
  
  
}
