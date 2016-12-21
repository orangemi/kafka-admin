package com.teambition.kafka.admin.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.teambition.kafka.model.Model;

import kafka.cluster.Broker;

import java.util.Collection;
import java.util.Vector;

@Path("/brokers")
public class BrokerAPI {
  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Integer> getBrokerIds() {
    Collection<Integer> brokers = new Vector<>();
    
    for (Broker broker: Model.getInstance().getBrokerCollections()) {
      brokers.add(broker.id());
    }
    return brokers;
  }
}
