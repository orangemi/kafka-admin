package com.teambition.kafka.admin.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.teambition.kafka.model.Model;
import com.teambition.kafka.model.Version;

import java.util.HashMap;
import java.util.List;

@Path("brokers")
public class Broker {
  @GET
  public List<String> index() {
    return Model.getInstance().getBrokerList();
  }
}
