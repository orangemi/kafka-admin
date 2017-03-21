Monitor Kafka
=============

## Resource
- http://docs.confluent.io/3.2.0/kafka/monitoring.html
- https://cwiki.apache.org/confluence/display/KAFKA/Available+Metrics

## Server Metrics
### Here are the important metrics to alert on a Kafka broker:

- `kafka.server:type=ReplicaManager,name=UnderReplicatedPartitions`
Number of under-replicated partitions (| ISR | < | all replicas |). Alert if value is greater than 0.

- `kafka.controller:type=KafkaController,name=OfflinePartitionsCount`
Number of partitions that don’t have an active leader and are hence not writable or readable. Alert if value is greater than 0.

- `kafka.controller:type=KafkaController,name=ActiveControllerCount`
Number of active controllers in the cluster. Alert if value is anything other than 1.

### Here are the list of metrics to observe on a Kafka broker:

- `kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec`
Aggregate incoming message rate.

- `kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec`
Aggregate incoming byte rate.

- `kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec`
Aggregate outgoing byte rate.

- `kafka.network:type=RequestMetrics,name=RequestsPerSec,request={Produce|FetchConsumer|FetchFollower}`
Request rate.

- `kafka.log:type=LogFlushStats,name=LogFlushRateAndTimeMs`
Log flush rate and time.

- `kafka.controller:type=ControllerStats,name=LeaderElectionRateAndTimeMs`
Leader election rate and latency.

- `kafka.controller:type=ControllerStats,name=UncleanLeaderElectionsPerSec`
Unclean leader election rate.

- `kafka.server:type=ReplicaManager,name=PartitionCount`
Number of partitions on this broker. This should be mostly even across all brokers.

- `kafka.server:type=ReplicaManager,name=LeaderCount`
Number of leaders on this broker. This should be mostly even across all brokers. If not, set auto.leader.rebalance.enable to true on all brokers in the cluster.

- `kafka.server:type=ReplicaManager,name=IsrShrinksPerSec`
If a broker goes down, ISR for some of the partitions will shrink. When that broker is up again, ISR will be expanded once the replicas are fully caught up. Other than that, the expected value for both ISR shrink rate and expansion rate is 0.

- `kafka.server:type=ReplicaManager,name=IsrExpandsPerSec`
When a broker is brought up after a failure, it starts catching up by reading from the leader. Once it is caught up, it gets added back to the ISR.

- `kafka.server:type=ReplicaFetcherManager,name=MaxLag,clientId=Replica`
Maximum lag in messages between the follower and leader replicas. This is controlled by the replica.lag.max.messages config.

- `kafka.server:type=FetcherLagMetrics,name=ConsumerLag,clientId=([-.\w]+),topic=([-.\w]+),partition=([0-9]+)`
Lag in number of messages per follower replica. This is useful to know if the replica is slow or has stopped replicating from the leader.

- `kafka.network:type=RequestMetrics,name=TotalTimeMs,request={Produce|FetchConsumer|FetchFollower}`
Total time in ms to serve the specified request.

- `kafka.server:type=DelayedOperationPurgatory,delayedOperation=Produce,name=PurgatorySize`
Number of requests waiting in the producer purgatory. This should be non-zero acks=-1 is used on the producer.

- `kafka.server:type=DelayedOperationPurgatory,delayedOperation=Fetch,name=PurgatorySize`
Number of requests waiting in the fetch purgatory. This is high if consumers use a large value for fetch.wait.max.ms .

## ZooKeeper Metrics
We expose counts for ZooKeeper state transitions, which can help to spot problems, e.g., with broker sessions to ZooKeeper. The metrics currently show the rate of transitions per second for each one of the possible states. 

Here is the list of the counters we expose, one for each possible ZooKeeper client states:

- `kafka.server:type=SessionExpireListener,name=ZooKeeperDisconnectedPerSec`
Zookeeper client is currently disconnected from the ensemble. The client lost its previous connection to a server and it is currently trying to reconnect. The session is not necessarily expired.

- `kafka.server:type=SessionExpireListener,name=ZooKeeperSyncConnectedPerSec`
ZooKeeper client is connected to the ensemble and ready to execute operations.

- `kafka.server:type=SessionExpireListener,name=ZooKeeperAuthFailedPerSec`
An attempt to connect to the ensemble failed because the client has not provided correct credentials.

- `kafka.server:type=SessionExpireListener,name=ZooKeeperConnectedReadOnlyPerSec`
The server the client is connected to is currently LOOKING, which means that it is neither FOLLOWING nor LEADING. Consequently, the client can only read the ZooKeeper state, but not make any changes (create, delete, or set the data of znodes).

- `kafka.server:type=SessionExpireListener,name=ZooKeeperSaslAuthenticatedPerSec`
Client has successfully authenticated.

- `kafka.server:type=SessionExpireListener,name=ZooKeeperExpiredPerSec`
The ZooKeeper session has expired. When a session expires, we can have leader changes and even a new controller. It is important to keep an eye on the number of such events across a Kafka cluster and if the overall number is high, then we have a few recommendations:
  - Check the health of your network
  - Check for garbage collection issues and tune it accordingly
  - If necessary, increase the session time out by setting the value of `zookeeper.connection.timeout.ms`.

## Producer Metrics
Starting with 0.8.2, the new producer exposes the following metrics:

### Global Request Metrics
MBean: `kafka.producer:type=producer-metrics,client-id=([-.w]+)`

- `request-latency-avg`
The average request latency in ms.

- `request-latency-max`
The maximum request latency in ms.

- `request-rate`
The average number of requests sent per second.

- `response-rate`
The average number of responses received per second.

- `incoming-byte-rate`
The average number of incoming bytes received per second from all servers.

- `outgoing-byte-rate`
The average number of outgoing bytes sent per second to all servers.

### Global Connection Metrics
MBean: `kafka.producer:type=producer-metrics,client-id=([-.w]+)`

- `connection-count`
The current number of active connections.

- `connection-creation-rate`
New connections established per second in the window.

- `connection-close-rate`
Connections closed per second in the window.

- `io-ratio`
The fraction of time the I/O thread spent doing I/O.

- `io-time-ns-avg`
The average length of time for I/O per select call in nanoseconds.

- `io-wait-ratio`
The fraction of time the I/O thread spent waiting.

- `select-rate`
Number of times the I/O layer checked for new I/O to perform per second.

- `io-wait-time-ns-avg`
The average length of time the I/O thread spent waiting for a socket ready for reads or writes in nanoseconds.

### Per-Broker Metrics
MBean: `kafka.producer:type=producer-node-metrics,client-id=([-.w]+),node-id=([0-9]+)`

Besides the Global Request Metrics, the following metrics are also available per broker:

- `request-size-max`
The maximum size of any request sent in the window for a broker.

- `request-size-avg`
The average size of all requests in the window for a broker.

- `request-rate`
The average number of requests sent per second to the broker.

- `response-rate`
The average number of responses received per second from the broker.

- `incoming-byte-rate`
The average number of bytes received per second from the broker.

- `outgoing-byte-rate`
The average number of bytes sent per second to the broker.

### Per-Topic Metrics
MBean: `kafka.producer:type=producer-topic-metrics,client-id=([-.w]+),topic=([-.w]+)`

Besides the Global Request Metrics, the following metrics are also available per topic:

- `byte-rate`
The average number of bytes sent per second for a topic.

- `record-send-rate`
The average number of records sent per second for a topic.

- `compression-rate`
The average compression rate of record batches for a topic.

- `record-retry-rate`
The average per-second number of retried record sends for a topic.

- `record-error-rate`
The average per-second number of record sends that resulted in errors for a topic.

## New Consumer Metrics
Starting with Kafka 0.9.0.0, the new consumer exposes the following metrics:

### Fetch Metrics
MBean: `kafka.consumer:type=consumer-fetch-manager-metrics,client-id=([-.w]+)`

- `records-lag-max`
The maximum lag in terms of number of records for any partition in this window. An increasing value over time is your best indication that the consumer group is not keeping up with the producers.

- `fetch-size-avg`
The average number of bytes fetched per request.

- `fetch-size-max`
The average number of bytes fetched per request.

- `bytes-consumed-rate`
The average number of bytes consumed per second.

- `records-per-request-avg`
The average number of records in each request.

- `records-consumed-rate`
The average number of records consumed per second.

- `fetch-rate`
The number of fetch requests per second.

- `fetch-latency-avg`
The average time taken for a fetch request.

- `fetch-latency-max`
The max time taken for a fetch request.

- `fetch-throttle-time-avg`
The average throttle time in ms. When quotas are enabled, the broker may delay fetch requests in order to throttle a consumer which has exceeded its limit. This metric indicates how throttling time has been added to fetch requests on average.

- `fetch-throttle-time-avg`
The maximum throttle time in ms.

### Topic-level Fetch Metrics
MBean: `kafka.consumer:type=consumer-fetch-manager-metrics,client-id=([-.w]+),topic=([-.w]+)`

- `fetch-size-avg`
The average number of bytes fetched per request for a specific topic.

- `fetch-size-max`
The maximum number of bytes fetched per request for a specific topic.

- `bytes-consumed-rate`
The average number of bytes consumed per second for a specific topic.

- `records-per-request-avg`
The average number of records in each request for a specific topic.

- `records-consumed-rate`
The average number of records consumed per second for a specific topic.

### Consumer Group Metrics
MBean: `kafka.consumer:type=consumer-coordinator-metrics,client-id=([-.w]+)`

- `assigned-partitions`
The number of partitions currently assigned to this consumer.

- `commit-latency-avg`
The average time taken for a commit request.

- `commit-latency-max`
The max time taken for a commit request.

- `commit-rate`
The number of commit calls per second.

- `join-rate`
The number of group joins per second. Group joining is the first phase of the rebalance protocol. A large value indicates that the consumer group is unstable and will likely be coupled with increased lag.

- `join-time-avg`
The average time taken for a group rejoin. This value can get as high as the configured session timeout for the consumer, but should usually be lower.

- `join-time-max`
The max time taken for a group rejoin. This value should not get much higher than the configured session timeout for the consumer.

- `sync-rate`
The number of group syncs per second. Group synchronization is the second and last phase of the rebalance protocol. Similar to join-rate, a large value indicates group instability.

- `sync-time-avg`
The average time taken for a group sync.

- `sync-time-max`
The max time taken for a group sync.

- `heartbeat-rate`
The average number of heartbeats per second. After a rebalance, the consumer sends heartbeats to the coordinator to keep itself active in the group. You can control this using the heartbeat.interval.ms setting for the consumer. You may see a lower rate than configured if the processing loop is taking more time to handle message batches. Usually this is OK as long as you see no increase in the join rate.

- `heartbeat-response-time-max`
The max time taken to receive a response to a heartbeat request.

- `last-heartbeat-seconds-ago`
The number of seconds since the last controller heartbeat.

### Global Request Metrics
MBean: `kafka.consumer:type=consumer-metrics,client-id=([-.w]+)`

- `request-latency-avg`
The average request latency in ms.

- `request-latency-max`
The maximum request latency in ms.

- `request-rate`
The average number of requests sent per second.

- `response-rate`
The average number of responses received per second.

- `incoming-byte-rate`
The average number of incoming bytes received per second from all servers.

- `outgoing-byte-rate`
The average number of outgoing bytes sent per second to all servers.

### Global Connection Metrics
MBean: `kafka.consumer:type=consumer-metrics,client-id=([-.w]+)`

- `connection-count`
The current number of active connections.

- `connection-creation-rate`
New connections established per second in the window.

- `connection-close-rate`
Connections closed per second in the window.

- `io-ratio`
The fraction of time the I/O thread spent doing I/O.

- `io-time-ns-avg`
The average length of time for I/O per select call in nanoseconds.

- `io-wait-ratio`
The fraction of time the I/O thread spent waiting.

- `select-rate`
Number of times the I/O layer checked for new I/O to perform per second.

- `io-wait-time-ns-avg`
The average length of time the I/O thread spent waiting for a socket ready for reads or writes in nanoseconds.

### Per-Broker Metrics
MBean: kafka.consumer:type=consumer-node-metrics,client-id=([-.w]+),node-id=([0-9]+)

Besides the Global Request Metrics, the following metrics are also available per broker:

- `request-size-max`
The maximum size of any request sent in the window for a broker.

- `request-size-avg`
The average size of all requests in the window for a broker.

- `request-rate`
The average number of requests sent per second to the broker.

- `response-rate`
The average number of responses received per second from the broker.

- `incoming-byte-rate`
The average number of bytes received per second from the broker.

- `outgoing-byte-rate`
The average number of bytes sent per second to the broker.

## Old Consumer Metrics

- `kafka.consumer:type=ConsumerFetcherManager,name=MaxLag,clientId=([-.\w]+)`
Number of messages the consumer lags behind the producer by.

- `kafka.consumer:type=ConsumerFetcherManager,name=MinFetchRate,clientId=([-.\w]+)`
The minimum rate at which the consumer sends fetch requests to the broker. If a consumer is dead, this value drops to roughly 0.

- `kafka.consumer:type=ConsumerTopicMetrics,name=MessagesPerSec,clientId=([-.\w]+)`
The throughput in messages consumed per second.

- `kafka.consumer:type=ConsumerTopicMetrics,name=MessagesPerSec,clientId=([-.\w]+)`
The throughput in bytes consumed per second.

The following metrics are available only on the high-level consumer:

- `kafka.consumer:type=ZookeeperConsumerConnector,name=KafkaCommitsPerSec,clientId=([-.\w]+)`
The rate at which this consumer commits offsets to Kafka. This is only relevant if offsets.storage=kafka.

- `kafka.consumer:type=ZookeeperConsumerConnector,name=ZooKeeperCommitsPerSec,clientId=([-.\w]+)`
The rate at which this consumer commits offsets to ZooKeeper. This is only relevant if offsets.storage=zookeeper. Monitor this value if your ZooKeeper cluster is under performing due to high write load.

- `kafka.consumer:type=ZookeeperConsumerConnector,name=RebalanceRateAndTime,clientId=([-.\w]+)`
The rate and latency of the rebalance operation on this consumer.

- `kafka.consumer:type=ZookeeperConsumerConnector,name=OwnedPartitionsCount,clientId=([-.\w]+),groupId=([-.\w]+)`
The number of partitions owned by this consumer.
