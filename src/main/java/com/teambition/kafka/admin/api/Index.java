package com.teambition.kafka.admin.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.teambition.kafka.admin.model.VersionModel;

import java.util.HashMap;

@Path("/")
public class Index {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public VersionModel info() {
    return new VersionModel();
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
