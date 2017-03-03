package com.teambition.kafka.admin.api;

import com.teambition.kafka.admin.model.Model;
import com.teambition.kafka.admin.model.TopicModel;
import com.teambition.kafka.admin.model.TopicPartitionModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Properties;

@Path("/topics")
public class TopicAPI {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> getTopics() {
    return Model.getInstance().getTopicCollections();
  }
  
  @GET
  @Path("/{topic}")
  @Produces(MediaType.APPLICATION_JSON)
  public TopicModel getTopic(@PathParam("topic") String topic) {
    TopicModel topicModelEntity = new TopicModel(
      topic,
      Model.getInstance().getTopicPartitions(topic).size(),
      Model.getInstance().getTopicConfig(topic));
    return topicModelEntity;
  }

  @GET
  @Path("/{topic}/configs")
  @Produces(MediaType.APPLICATION_JSON)
  public Properties getTopicConfig(@PathParam("topic") String topic) {
    return Model.getInstance().getTopicConfig(topic);
  }
  
  @GET
  @Path("/{topic}/partitions")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<TopicPartitionModel> getPartitions(@PathParam("topic") String topic) {
    return Model.getInstance().getTopicPartitions(topic);
  }
  
  @GET
  @Path("/{topic}/partitions/{partition}")
  @Produces(MediaType.APPLICATION_JSON)
  public TopicPartitionModel getPartitions(@PathParam("topic") String topic, @PathParam("partition") int partition) {
    return Model
      .getInstance()
      .getTopicPartitions(topic)
      .stream()
      .filter(p -> p.getId() == partition)
      .findAny()
      .get();
  }
  
//  @GET
//  @Path("/{topic}/consumers")
//  @Produces(MediaType.APPLICATION_JSON)
//  public Collection<String> getConsumers(@PathParam("topic") String topic) {
//    return Model.getInstance().getZkConsumerGroupsByTopic(topic);
//  }
}
