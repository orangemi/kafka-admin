package com.teambition.kafka.admin.model;

import kafka.common.ErrorMapping;
import kafka.common.OffsetAndMetadata;
import kafka.common.OffsetMetadata;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetCommitRequest;
import kafka.javaapi.OffsetCommitResponse;
import kafka.network.BlockingChannel;
import org.apache.kafka.common.TopicPartition;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Orange on 03/01/2017.
 */
public class ConsumerManager {
  
  public static void main(String[] argv) {
    ConsumerManager consumerManager = new ConsumerManager();
    consumerManager.brokerHost = "kafka01-cn.teambition.corp";
    consumerManager.brokerPort = 9092;
    consumerManager.commitOffset("connect-pompeii-postgresql-sink-connector", "", "core", 0, 2172559353L);
  }
  
  public String brokerHost = "project.ci";
  public int brokerPort = 39092;
  
  public void commitOffset(String group, String clientId, String topic, int partition, long offset) {
    int correlationId = 0;
    long now = System.currentTimeMillis();
    long expired = now + 86400L * 1000;

    Map<TopicAndPartition, OffsetAndMetadata> offsets = new LinkedHashMap<>();
    offsets.put(new TopicAndPartition(topic, partition), new OffsetAndMetadata(new OffsetMetadata(offset, ""), now, expired));
    OffsetCommitRequest commitRequest = new OffsetCommitRequest(
      group,
      offsets,
      correlationId++,
      clientId,
      (short) 1 // version
    ); // version 1 and above commit to Kafka, version 0 commits to ZooKeeper
    
    try {
      BlockingChannel channel = new BlockingChannel(brokerHost, brokerPort,
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
}
