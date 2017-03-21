package com.teambition.kafka.admin.tasks;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.IOException;
import java.util.Properties;

public class WebServer {
  public final static String CONFIG_PREFIX = "webserver";
  public final static String PORT_CONFIG = "webserver.port";
  public final static String PREFIX_CONFIG = "webserver.prefix";
  public final static String VERSION_FILE = "version.properties";

  private static Properties versionProperties;
  public static Properties getVersionProperties() {
    if (versionProperties == null) {
      try {
        versionProperties = new Properties();
        versionProperties.load(WebServer
          .class.getClassLoader()
          .getResourceAsStream(VERSION_FILE));
      } catch (IOException ex) {
        // ignore exception
      }
    }
    return versionProperties;
  }

  private Properties properties;
  private int port = 9001;
  private Server server;
  private String apiPrefix = "/";
  public WebServer() {}

  public WebServer(int port) {
    this();
    this.port = port;
  }

  public WebServer(Properties properties) {
    this();
    this.properties = properties;
    this.port = Integer.valueOf(properties.getProperty(PORT_CONFIG));
    this.apiPrefix = properties.getProperty(PREFIX_CONFIG);
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
            try {
              // System.out.println(event.getType());
              
              switch (event.getType()) {
                case FINISHED:
                  Throwable ex = event.getException();
                  if (event.getContainerResponse() != null) {
                    System.out.println(
                      event.getContainerRequest().getMethod() +
                        " " +
                        event.getContainerRequest().getRequestUri().getPath() +
                        " " +
                        event.getContainerResponse().getStatus());
                  } else if (ex != null) {
                    System.out.println(
                      event.getContainerRequest().getMethod() +
                        " " +
                        event.getContainerRequest().getRequestUri().getPath() +
                        " " +
                        500);
                    ex.printStackTrace();
                  }
                  break;
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };
      }
  
    });
    config.register(JacksonFeature.class);
    config.packages("com.teambition.kafka.admin.api");

//    ServletHolder servlet = new ServletHolder(new ServletContainer(config));
  
    ServletContextHandler apiContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    apiContextHandler.addServlet(new ServletHolder(new ServletContainer(config)), this.apiPrefix + "*");
  
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setResourceBase("./web");
    ContextHandler resourceContextHandler = new ContextHandler("/admin");
    resourceContextHandler.setHandler(resourceHandler);
  
    HandlerList handlers = new HandlerList();
    handlers.addHandler(resourceContextHandler);
    handlers.addHandler(apiContextHandler);

    server = new Server(port);
    server.setHandler(handlers);
  
    try {
      System.out.println("Server start ...");
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
