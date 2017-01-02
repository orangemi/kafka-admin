package com.teambition.kafka.admin.api;

import com.teambition.kafka.admin.model.Consumer;
import com.teambition.kafka.admin.model.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/consumers-v2")
public class ConsumerV2API {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> getConsumers() {
    return Model.getInstance().getConsumerV2s();
  }
  
  @GET
  @Path("/{group}")
  @Produces(MediaType.APPLICATION_JSON)
  public Consumer getConsumerTopic(@PathParam("group") String group) {
    return Model.getInstance().getConsumerV2(group);
  }
}
