package action;

import com.teambition.kafka.admin.model.ConsumerManager;
import kafka.consumer.ConsumerIterator;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by Orange on 06/01/2017.
 */
public class CommitOffest {
  public static void main(String[] argv) {
//    fetchData();
    commitOffset();
  }

  
  public static void fetchData() {
  
    String kafkaHost = "kafka01-cn.teambition.corp:9092,kafka02-cn.teambition.corp:9092";
    String groupId = "sample-consumer";
    String topic = "core-actionlog-post2";
  
    Properties props = new Properties();
    String deserializer = ByteArrayDeserializer.class.getName();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//    props.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, "false");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
    KafkaConsumer<byte[], byte[]> consumer;
    consumer = new KafkaConsumer(props);
//    consumer.subscribe(Arrays.asList(topic));
    consumer.assign(Arrays.asList(new TopicPartition(topic, 0)));
    consumer.seek(new TopicPartition(topic, 0), 2587110 /* 9187981 /* 17587280L */);
    Iterator<ConsumerRecord<byte[], byte[]>> iterator = consumer.poll(50000).iterator();
    while (iterator.hasNext()) {
      ConsumerRecord<byte[], byte[]> record = iterator.next();
      String value = new String(record.value(), StandardCharsets.UTF_8);
      System.out.println(record.offset() +
        " " + value
        );
    }
    consumer.close();
    
  }

  public static void commitOffset() {
//    String kafkaHost = "localhost:9092";
//    String groupId = "sample-consumer";
//    String topic = "sample";
//    int partition = 0;
//    long offset = 820L * 1000;
//
    String kafkaHost = "kafka01-cn.teambition.corp:9092,kafka02-cn.teambition.corp:9092";
//    String groupId = "connect-core-actionlog-sink2";
    String groupId = "actionlog";
//    String topic = "core-actionlog-post2";
    String topic = "core-actionlog";
//    int partition = 1;
//    long offset = 9187981;

    ConsumerManager cm = new ConsumerManager(kafkaHost);
    cm.commitOffset2(groupId, "connect-cluster", topic, 0, 1547669);
    cm.commitOffset2(groupId, "connect-cluster", topic, 1, 1545191);
    cm.commitOffset2(groupId, "connect-cluster", topic, 2, 1545273);
    cm.commitOffset2(groupId, "connect-cluster", topic, 3, 1546311);
    cm.commitOffset2(groupId, "connect-cluster", topic, 4, 1548221);
    cm.commitOffset2(groupId, "connect-cluster", topic, 5, 1548242);
    cm.commitOffset2(groupId, "connect-cluster", topic, 6, 1547092);
    cm.commitOffset2(groupId, "connect-cluster", topic, 7, 1548302);
  }
  
}
