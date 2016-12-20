package com.teambition.kafka.admin.rest;

import com.teambition.kafka.model.Model;
import com.teambition.kafka.model.Partition;
import com.teambition.kafka.model.Topic;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Properties;

@Path("topics")
public class TopicAPI {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> getTopics() {
    return Model.getInstance().getTopicCollections();
  }
  
  @GET
  @Path("{topic}")
  @Produces(MediaType.APPLICATION_JSON)
  public Topic getTopic(@PathParam("topic") String topic) {
    Topic topicEntity = new Topic(
      topic,
      Model.getInstance().getTopicPartitions(topic).size(),
      Model.getInstance().getTopicConfig(topic));
    return topicEntity;
  }

  @GET
  @Path("{topic}/configs")
  @Produces(MediaType.APPLICATION_JSON)
  public Properties getTopicConfig(@PathParam("topic") String topic) {
    return Model.getInstance().getTopicConfig(topic);
  }
  
  @GET
  @Path("{topic}/partitions")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Partition> getPartitions(@PathParam("topic") String topic) {
    return Model.getInstance().getTopicPartitions(topic);
  }
}
