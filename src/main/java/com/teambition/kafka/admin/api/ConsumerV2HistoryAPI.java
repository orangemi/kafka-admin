package com.teambition.kafka.admin.api;

import com.teambition.kafka.admin.model.Consumer;
import com.teambition.kafka.admin.model.Model;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/consumer-history")
public class ConsumerV2HistoryAPI {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Consumer> getConsumers() {
    return Model.getInstance().getAllConsumerV2s();
  }

}
