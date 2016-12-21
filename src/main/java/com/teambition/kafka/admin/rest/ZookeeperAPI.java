package com.teambition.kafka.admin.rest;

import com.teambition.kafka.model.Model;
import com.teambition.kafka.model.ZKNode;
import kafka.utils.Json;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Vector;

@Path("/zookeeper")
public class ZookeeperAPI {
  private String path;

  public ZookeeperAPI() {
     this("/");
  }

  public ZookeeperAPI(String path) {
    this.path = path;
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public ZKNode get() {
    ZKNode node = new ZKNode();
    node.setChildren(Model.getInstance().getZookeeperChildren(path));
    node.setData(Model.getInstance().getZookeeperData(path));
    node.setState(Model.getInstance().getZookeeperStat(path));
    return node;
  }
  
  @Path("/{child}")
  public ZookeeperAPI getChild(@PathParam("child") String child) {
    String target = path.equals("/") ? path + child : path + "/" + child;
    return new ZookeeperAPI(target);
  }
}
