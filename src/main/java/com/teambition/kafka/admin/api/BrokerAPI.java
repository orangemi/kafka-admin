package com.teambition.kafka.admin.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.teambition.kafka.admin.model.KafkaBrokerJmxClient;
import com.teambition.kafka.admin.model.Model;

import kafka.cluster.Broker;
import kafka.utils.Json;

import java.lang.management.ThreadInfo;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@Path("brokers")
public class BrokerAPI {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Integer> getBrokerIds() {
    return Model.getInstance().getBrokerCollections();
  }
  
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> getBrokerInfo(@PathParam("id") int id) {
    return Model.getInstance().getBrokerInfo(id);
  }
  
  @GET
  @Path("/{id}/jmx")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Map<String, Object>> getJmx(@PathParam("id") int id, @QueryParam("key") String key) {
    if (key == null || key.isEmpty()) key = "kafka.*:*";
    KafkaBrokerJmxClient client = Model.getInstance().getKafkaBrokerJmxClient(id);
    Map<String, Map<String, Object>> result = new HashMap<>();
    client.getObjectNamesByPattern(key).forEach(objectName -> {
      result.put(objectName.toString(), client.getAttributeByObjectName(objectName));
    });
    client.close();
    return result;
  }
  
  @GET
  @Path("/{id}/threads")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<ThreadInfo> getThreads(@PathParam("id") int id) {
    KafkaBrokerJmxClient client = Model.getInstance().getKafkaBrokerJmxClient(id);
    Collection<ThreadInfo> list = client.getThreads();
    // close client connection immediately sure to not leak
    client.close();
    return list;
  }
  
  @GET
  @Path("/{id}/jmx/key")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> getJmxKeyList(@PathParam("id") int id, @QueryParam("key") String key) {
    if (key == null || key.isEmpty()) key = "kafka.*:*";
    KafkaBrokerJmxClient client = Model.getInstance().getKafkaBrokerJmxClient(id);
    Collection<String> result = new Vector<>();
    client.getObjectNamesByPattern(key).forEach(objectName -> {
      result.add(objectName.toString());
    });
    client.close();
    return result;
  }
}
