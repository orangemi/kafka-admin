package com.teambition.kafka.admin;

import com.teambition.kafka.admin.model.Model;
import com.teambition.kafka.admin.tasks.KafkaMonitor;
import com.teambition.kafka.admin.tasks.WebServer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Main {
  private final static String CONFIG_FILE = "config.properties";
  private static void echo(String message) {
    System.out.println(message);
  }
  
  public static void main(String[] argv) throws Exception {
    Properties prop = new Properties();
    if (argv.length == 0) {
      prop.load(Main
        .class.getClassLoader()
        .getResourceAsStream(CONFIG_FILE));
    } else {
      String configFile = System.getProperty("user.dir") + "/" + argv[0];
      try {
        prop.load(new FileInputStream(configFile));
      } catch (FileNotFoundException ex) {
        ex.printStackTrace();
        System.exit(1);
      } catch (IOException ex) {
        ex.printStackTrace();
        System.exit(1);
      }
    }
    
    printConfig(prop);
    Model.getInstance(prop);
    WebServer webServer = new WebServer(prop);
    webServer.start();
    KafkaMonitor monitor = new KafkaMonitor(prop);
    monitor.start();
  }
  
  private static void printConfig(Properties prop) {
    echo("Monitor config:");
    prop.forEach((key, value) -> {
      echo("  " + key + ": " + value);
    });
  }
}
