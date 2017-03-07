package com.teambition.kafka.admin.tasks;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.Properties;

public class WebServer {
  public final static String CONFIG_PREFIX = "webserver";
  public final static String PORT_CONFIG = "webserver.port";
  public final static String PREFIX_CONFIG = "webserver.prefix";
  private Properties properties;
  private int port = 9001;
  private Server server;
  private String prefix = "/";
  public WebServer() {}

  public WebServer(int port) {
    this();
    this.port = port;
  }

  public WebServer(Properties properties) {
    this();
    this.properties = properties;
    this.port = Integer.valueOf(properties.getProperty(PORT_CONFIG));
    this.prefix = properties.getProperty(PREFIX_CONFIG);
  }
  
  public void start() {
    ResourceConfig config = new ResourceConfig();
    config.register(new ApplicationEventListener() {
      @Override
      public void onEvent(ApplicationEvent event) {
    
      }
  
      @Override
      public RequestEventListener onRequest(RequestEvent requestEvent) {
        return new RequestEventListener() {
          @Override
          public void onEvent(RequestEvent event) {
            // System.out.println(event.getType());
            switch (event.getType()) {
              case FINISHED:
                System.out.println(
                  event.getContainerRequest().getMethod() +
                  " " +
                  event.getContainerRequest().getRequestUri().getPath() +
                  " " +
                  event.getContainerResponse().getStatus());
            }
          }
        };
      }
  
    });
    config.register(JacksonFeature.class);
    config.packages("com.teambition.kafka.admin.api");
//    config.register(LoggingFeature.class);
//    config.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_SERVER, LoggingFeature.Verbosity.PAYLOAD_ANY);
//    config.registerInstances(new LoggingFilter())
    ServletHolder servlet = new ServletHolder(new ServletContainer(config));
  
    server = new Server(port);
    ServletContextHandler context = new ServletContextHandler(server, "/*");
    context.addServlet(servlet, this.prefix + "*");
  
    try {
      System.out.println("Server start ...");
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
