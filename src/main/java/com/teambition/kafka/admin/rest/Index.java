package com.teambition.kafka.admin.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.teambition.kafka.model.Version;

import java.util.HashMap;

@Path("")
public class Index {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Version info() {
    return new Version();
  }
  
  @GET
  @Path("/test/json")
  @Produces(MediaType.APPLICATION_JSON)
  public HashMap<String, String> json() {
    HashMap<String, String> a = new HashMap<>();
    a.put("a", "1");
    return a;
//    return new Model();
  }
}
