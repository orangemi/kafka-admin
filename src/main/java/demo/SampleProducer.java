package demo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SampleProducer {
  private static final String KAFKA_HOST = "localhost:9092";
  private static final String KAFKA_TOPIC = "sample";
  
  private final static String CONFIG_FILE = "config.properties";

  private KafkaProducer producer;
  private String topic;
  
  public static void main(String[] argv) {
  
    Properties prop = new Properties();
    try {
      prop.load(SampleProducer
        .class.getClassLoader()
        .getResourceAsStream(CONFIG_FILE));
  
      SampleProducer sp = new SampleProducer();
      sp.init();
      System.out.println("Start to produce data...");
      sp.run();

    } catch (IOException e) {
      e.printStackTrace();
    }
  
  }
  
  public void init() {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "DemoProducer");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    producer = new KafkaProducer<>(props);
    topic = KAFKA_TOPIC;
  }
  
  public void run() {
    int messageNo = 1;
    while(true) {
      String message = "Message_" + messageNo;
      long timestamp = System.currentTimeMillis();
      producer.send(new ProducerRecord<>(topic, messageNo, message));
      messageNo++;
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
