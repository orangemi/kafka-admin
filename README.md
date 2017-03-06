[WIP] Kafka Admin Rest API
==========================

## Dependencies
- [Grafana](http://www.grafana.org/) & [InfluxDB](https://influxdb.com/) 
This can simply run by docker with this `docker-compose.yml`:
```
version: '2'
services:
  grafana:
    image: grafana/grafana
    network_mode: bridge
    ports:
      - 9008:3000
    links:
      - influxdb
  influxdb:
    image: influxdb
    network_mode: bridge
    ports:
      - 8083:8083
      - 8086:8086
    volumes:
      - ./data:/var/lib/influxdb
```
- [Zookeeper](http://zookeeper.apache.org/) & [Kafka](http://kafka.apache.org/)
This can simply run by docker with this `docker-compose.yml`:
```
version: '2'
services:
  zk1:
    image: zookeeper
    ports:
      - 2181:2181
  kafka:
    image: orangemi/kafka
    restart: always
    ports:
      - 9092:9092
    links:
      - zk1
    environment:
      JMX_PORT: 9999
      KAFKA_ZOOKEEPER_CONNECT: zk1:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```
Please note that `JMX_PORT` should be assigned for kafka instance

## Get started
- modify your `config/config.properties`
- run `gradle run`
- see your result by grafana
- visit [http://localhost:9001](http://localhost:9001)

## rujning on Docker
See and modify your `docker-compose.yml` and run `docker-compose up -d`
```
docker-compose up -d
```

## Build
```
gradle distTar
docker build -t kafka-admin .
```

## [WIP] API
- [X] GET /brokers

- [X] GET /topics
- [X] GET /topics/:topic
- [X] GET /topics/:topic/configs
- [ ] POST /topics/:topic/configs
- [X] GET /topics/:topic/partitions
- [X] GET /topics/:topic/partitions/:partition

- [X] GET /consumers
- [X] GET /consumers/:group

- [X] GET /consumers2
- [X] GET /consumers2/:group
- [X] GET /consumers2/:group/topics/:topic/partitions
- [X] GET /consumers2/:group/topics/:topic/partitions/:partition
- [X] POST /consumers2/:group/topics/:topic/partitions/:partition
提交offset, 提交0为删除

- [X] GET /zookeeper/*

## TODO
- Get disk usage for topic-partition
- Monitor offset change for topic-partition
- Monitor consumer offset change for topic-partition
- Change topic config
- ReAssign topic partition

- Create topic
- Delete topic

- Get ACL knowledge

- Add a web ui to zk & kafka

## Resources
- https://gist.github.com/ashrithr/5811266
- https://sematext.com/blog/2016/06/07/kafka-consumer-lag-offsets-monitoring/
- https://blog.serverdensity.com/how-to-monitor-kafka/
- https://www.datadoghq.com/blog/collecting-kafka-performance-metrics/
- https://github.com/linkedin/Burrow/wiki/What-is-Burrow
- https://apps.sematext.com/spm-reports/mainPage.do?selectedApplication=4291
- http://blog.csdn.net/lizhitao/article/details/24581907
- http://blog.csdn.net/lizhitao/article/details/39499283

## Related Projects
- https://github.com/orangemi/kafka-admin-web

## Monitor Metrics
- [ ] Partitions
- [ ] Leader Elections
- [ ] Active Controllers ??
- [ ] ISR / Log Flush ??
- [ ] Purgatory ??
- [ ] Log Cleaner
- [ ] Queue / Expires ??
- [ ] Replicas: Imbalance Count, Lag
- [ ] Requests: local time, remote time, request queue time, response queue time, response send time
- [ ] Topic: bytes in, bytes out, bytes rejected, messages in; failed fetch requests, failed produce requests
- [ ] Topic Partition: segments, size, offset increasing(speed), under replicated
- [ ] Producer Error: failed, resends, serialization errors
- [ ] Producer Requests: requests, size, request time
- [ ] Producer Topics: bytes, dropped messages, messages
- [ ] Consumer Lag: lag
- [ ] Consumer Fetcher: fetch bytes, requests, response bytes, responses, fetch time,
- [ ] Consumer Connectors: kafka_commits rebalance_count zookeeper_commits
- [ ] Consumer Topics: bytes, messages, queue size, consumer owned parititions
