package com.teambition.kafka.admin.api;

import com.teambition.kafka.admin.model.ConsumerManager;
import com.teambition.kafka.admin.model.ConsumerModel;
import com.teambition.kafka.admin.model.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Path("/consumers2")
public class ConsumerV2API {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> getConsumers() {
    return Model.getInstance().getConsumerManager().getConsumerList().keySet();
  }
  
  @GET
  @Path("/{group}")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Long> getConsumerOffsets(@PathParam("group") String group) {
    Map<String, Long> result = new HashMap<>();
    Model.getInstance().getConsumerManager().getConsumerList().get(group).getOffsets().forEach((topic, partitions) -> {
      partitions.forEach((partition, offset) -> {
        result.put(topic + "-" + partition, offset);
      });
    });
    return result;
  }
  
  @GET
  @Path("/{group}/topics")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> getConsumerTopics(@PathParam("group") String group) {
    return Model.getInstance().getConsumerManager().getConsumerList().get(group).getOffsets().keySet();
  }
  
  @GET
  @Path("/{group}/topics/{topic}/paritions")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Integer> getConsumerPartitions(@PathParam("group") String group, @PathParam("topic") String topic) {
    return Model.getInstance().getConsumerManager().getConsumerList().get(group).getOffsets().get(topic).keySet();
  }

  @GET
  @Path("/{group}/topics/{topic}/partitions/{partition}")
  @Produces(MediaType.APPLICATION_JSON)
  public long getConsumerTopicPartitionOffsets(@PathParam("group") String group, @PathParam("topic") String topic, @PathParam("partition") int partition) {
    return Model.getInstance().getConsumerManager().getConsumerList().get(group).getOffsets().get(topic).get(partition);
  }
  
  @POST
  @Path("/{group}/topics/{topic}/partitions/{partition}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void postConsumerOffset(@PathParam("group") String group, @PathParam("topic") String topic, @PathParam("partition") int partition, long offset) {
    System.out.println(offset);
    Model.getInstance().getConsumerManager().commitOffset2(group, "connect-cluster", topic, partition, offset);
  }
}
