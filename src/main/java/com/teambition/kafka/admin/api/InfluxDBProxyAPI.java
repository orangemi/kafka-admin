package com.teambition.kafka.admin.api;

import com.teambition.kafka.admin.tasks.KafkaMonitor;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/influxdb")
@Produces(MediaType.APPLICATION_JSON)
public class InfluxDBProxyAPI {
  @GET
  @Path("/query")
  @Produces(MediaType.APPLICATION_JSON)
  public Response query(@QueryParam("q") String query) {
    if (query == null || query.isEmpty()) {
      return Response.status(400).build();
    }
    KafkaMonitor monitor = KafkaMonitor.getInstance();
    QueryResult queryResult = monitor.connect().query(new Query(query, monitor.getDbName()));
    List<QueryResult.Result> result = queryResult.getResults();
    int status = 200;
    if (queryResult.hasError()) status = 400;
    return Response.status(status).entity(result).build();
  }
  
  @POST
  @Path("/query")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response queryPost(Map<String, Object> body) {
    return query(body.get("q").toString());
  }
  
}
