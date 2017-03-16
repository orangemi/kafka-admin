Kafka Admin
============

## Dependencies
- [InfluxDB](https://influxdb.com/) 
- [Kafka](https://kafka.apache.org/)

These can simply run by docker with this `docker-compose.yml`:
```
version: '2'
services:
  influxdb:
    image: influxdb
    ports:
      - 8086:8086
  zk:
    image: zookeeper
    ports:
      - 2181:2181
  kafka:
    image: confluentinc/cp-kafka
    restart: always
    ports:
      - 9092:9092
      - 9999:9999
    links:
      - zk
    environment:
      JMX_PORT: 9999
      KAFKA_ZOOKEEPER_CONNECT: zk:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```
Please note that `JMX_PORT` should be assigned for kafka instance

## Get started
run `make build` and then run `gradle run`. visit [http://localhost:9001/admin/](http://localhost:9001/admin/) to see what you get.

## Get started by Docker
See and modify your `docker-compose.yml` and run `docker-compose up -d`
```
docker-compose up -d
```

## API
### GET /api/brokers
Fetch broker list

### GET /api/topics
Fetch topic list

### GET /api/topics/:topic
Fetch topic info 

### GET /api/topics/:topic/configs
### [ ] POST /api/topics/:topic/configs
Fetch or modify topic config

### [ ] POST /api/topics/:topic/reassign
Reassign topic-partition replica & leader

### GET /api/topics/:topic/partitions
### GET /api/topics/:topic/partitions/:partition
Fetch topic parition(s)

### GET /api/topics/:topic/consumers2
Get topic assigned kafka consumer (new consumer)

### GET /api/consumers
### GET /api/consumers/:group
Get old consumer(s)

### GET /api/consumers2
### GET /api/consumers2/:group
Get new(default) consumer(s)

### GET /api/consumers2/:group/topics
### GET /api/consumers2/:group/topics/:topic/partitions
Get consumer's assigned topics or partitions

### [X] GET /api/consumers2/:group/topics/:topic/partitions/:partition
### [X] POST /api/consumers2/:group/topics/:topic/partitions/:partition
Fetch or commit offset for topic-partition if 0 means delete commit offset.

### [X] GET /api/zookeeper/*
Fetch zookeeper tree node's data, children, stats

### [X] GET /api/influxdb/query
### [X] POST /api/influxdb/query
InfluxDB Query Proxy

## TODO
- [X] Get disk usage for topic-partition
- [X] Monitor offset change for topic-partition
- [X] Monitor consumer offset change for topic-partition
- [X] Change topic config
- [ ] Reassign topic partition
- [X] Proxy influxdb query
- [ ] generate warnning if kafka / zookeeper is not available
- [ ] Create topic
- [ ] Delete topic

- [ ] Get ACL knowledge

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
