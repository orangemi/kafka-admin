package com.teambition.kafka.admin.tasks;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.Properties;

public class WebServer {
  public final static String CONFIG_PREFIX = "webserver";
  public final static String PORT_CONFIG = "port";
  private Properties properties;
  private int port = 9001;
  private Server server;
  public WebServer() {}

  public WebServer(int port) {
    this();
    this.port = port;
  }

  public WebServer(Properties properties) {
    this();
    this.properties = properties;
    this.port = Integer.valueOf(properties.getProperty(CONFIG_PREFIX + "." + PORT_CONFIG));
  }
  
  public void start() {
    ResourceConfig config = new ResourceConfig();
    config.register(JacksonFeature.class);
    config.packages("com.teambition.kafka.admin.api");
    ServletHolder servlet = new ServletHolder(new ServletContainer(config));
  
    server = new Server(port);
    ServletContextHandler context = new ServletContextHandler(server, "/*");
    context.addServlet(servlet, "/*");
  
    try {
      System.out.println("Server start ...");
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
