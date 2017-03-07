package demo;

import kafka.consumer.ConsumerIterator;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class SampleConsumer {
  private static final String KAFKA_HOST = "project.ci:29092";
  private static final String KAFKA_TOPIC = "sample";
  private static final String GROUP_ID = "sample-consumer";
  
  public KafkaConsumer<byte[], byte[]> consumer;
  
  private void init() {
    Properties props = new Properties();
    String deserializer = ByteArrayDeserializer.class.getName();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "3000");
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//    props.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, "false");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
    consumer = new KafkaConsumer(props);
    consumer.subscribe(Arrays.asList(KAFKA_TOPIC));
  }
  
  private void run() {
    long count = 0L;
    while (true) {
      Iterator<ConsumerRecord<byte[], byte[]>> iterator = consumer.poll(5000).iterator();
      while (iterator.hasNext()) {
        ConsumerRecord<byte[], byte[]> record = iterator.next();
        record.value();

        // Simulate process wait 1s
        process(record);
        
        if (count++ % 1000 == 0) {
          byte[] bytes = record.value();
          String value = new String(bytes, StandardCharsets.UTF_8);
          System.out.println("consumed 1000 line (" + count + "): " + value);
        }
        
      }
      
//      consumer.commitSync(new HashMap<>(new TopicPartition(KAFKA_TOPIC, 0)));
    }
  }
  
  private void process(ConsumerRecord<byte[], byte[]> record) {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] atrv) {
    SampleConsumer sc = new SampleConsumer();
    sc.init();
    System.out.println("Start to consumer data...");
    sc.run();
  }
}
