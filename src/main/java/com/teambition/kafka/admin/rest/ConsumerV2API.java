package com.teambition.kafka.admin.rest;

import com.teambition.kafka.model.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/consumers-v2")
public class ConsumerV2API {
  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> getConsumers() {
    return Model.getInstance().getConsumerV2s();
  }
  
  @GET
  @Path("/{group}")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> getConsumerTopic(@PathParam("consumer") String consumer) {
    return Model.getInstance().getTopicsByConsumerV2(consumer);
  }
}
