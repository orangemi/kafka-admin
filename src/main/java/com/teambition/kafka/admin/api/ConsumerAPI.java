package com.teambition.kafka.admin.api;

import com.teambition.kafka.admin.model.Consumer;
import com.teambition.kafka.admin.model.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/consumers")
public class ConsumerAPI {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> getZkConsumerGroups() {
    return Model.getInstance().getZookeeperChildren("/consumers");
  }

  @GET
  @Path("/{group}")
  @Produces(MediaType.APPLICATION_JSON)
  public Consumer getZkConsumerGroup(@PathParam("group") String group) {
    return Model.getInstance().getZkConsumerGroup(group);
  }
}
