package com.teambition.kafka.restapp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class MainApp {
  public static void main(String[] args ) throws Exception {
    ResourceConfig config = new ResourceConfig();
    config.register(JacksonFeature.class);
    config.packages("com.teambition.kafka.admin.rest");
    ServletHolder servlet = new ServletHolder(new ServletContainer(config));
  
    Server server = new Server(9001);
    ServletContextHandler context = new ServletContextHandler(server, "/*");
    context.addServlet(servlet, "/*");
  
    try {
      server.start();
      server.join();
    } finally {
      server.destroy();
    }
  }
}
