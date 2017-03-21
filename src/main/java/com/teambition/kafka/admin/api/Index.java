package com.teambition.kafka.admin.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.teambition.kafka.admin.tasks.WebServer;

import java.util.Map;
import java.util.Properties;
import java.util.HashMap;

@Path("/")
public class Index {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Properties index() {
    return version();
  }

  @GET
  @Path("/version")
  @Produces(MediaType.APPLICATION_JSON)
  public Properties version() {
    return WebServer.getVersionProperties();
  }

  // TODO: return configs of this kafka-admin info
  // @GET
  // @Path("/configs")
  // @Produces(MediaType.APPLICATION_JSON)
  // public Properties configs() {
  //   return WebServer.getVersionProperties();
  // }
  
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
