## Had API

### Map<TopicPartition, Long> getTopicPartitionsLogEndOffsetMetrics()
kafka.log:type=Log,name=LogEndOffset,topic=*,partition=*

### Map<TopicPartition, Long> getTopicPartitionsNumLogSegmentsMetrics()
kafka.log:type=Log,name=NumLogSegments,topic=*,partition=*

### Map<TopicPartition, Long> getTopicPartitionsLogStartOffsetMetrics()
kafka.log:type=Log,name=LogStartOffset,topic=*,partition=*

### Map<TopicPartition, Long> getTopicPartitionsSizeMetrics()
kafka.log:type=Log,name=Size,topic=*,partition=*

### Map<TopicPartition, Long> getTopicPartitionsUnderReplicatedMetrics()
kafka.cluster:type=Partition,name=UnderReplicated,topic=*,partition=*

### int getBrokerState()
kafka.server:type=KafkaServer,name=BrokerState

### Map<String, MeterMBean> getBrokerTopicsMetrics()
kafka.server:type=BrokerTopicMetrics,name=TotalFetchRequestsPerSec
kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec
kafka.server:type=BrokerTopicMetrics,name=BytesRejectedPerSec
kafka.server:type=BrokerTopicMetrics,name=FailedFetchRequestsPerSec
kafka.server:type=BrokerTopicMetrics,name=TotalProduceRequestsPerSec
kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec
kafka.server:type=BrokerTopicMetrics,name=FailedProduceRequestsPerSec
kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec

### Map<String, MeterMBean> getBrokerReplicaManagerMetrics()
kafka.server:type=ReplicaManager,name=PartitionCount
kafka.server:type=ReplicaManager,name=IsrExpandsPerSec
kafka.server:type=ReplicaManager,name=IsrShrinksPerSec
kafka.server:type=ReplicaManager,name=LeaderCount
kafka.server:type=ReplicaManager,name=UnderReplicatedPartitions

### Map<String, MeterMBean> getSessionExpireListenerMetrics()
kafka.server:type=SessionExpireListener,name=ZooKeeperSaslAuthenticationsPerSec
kafka.server:type=SessionExpireListener,name=ZooKeeperSyncConnectsPerSec
kafka.server:type=SessionExpireListener,name=ZooKeeperAuthFailuresPerSec
kafka.server:type=SessionExpireListener,name=ZooKeeperReadOnlyConnectsPerSec
kafka.server:type=SessionExpireListener,name=ZooKeeperExpiresPerSec
kafka.server:type=SessionExpireListener,name=ZooKeeperDisconnectsPerSec

------------------------------------------------------------------------------------

### Map<String, HistogramMBean> getRequestMetrics()
### Map<String, MeterMBean> getRequestPerSecMetrics()
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=ListGroups
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=Heartbeat
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=OffsetCommit
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=ApiVersions
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=StopReplica
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=JoinGroup
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=Fetch
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=FetchConsumer
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=Metadata
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=StopReplica
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=GroupCoordinator
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=ApiVersions
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=JoinGroup
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=LeaderAndIsr
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=Fetch
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=StopReplica
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=DescribeGroups
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=LeaveGroup
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=GroupCoordinator
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Heartbeat
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=OffsetFetch
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=FetchFollower
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=FetchConsumer
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=SaslHandshake
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Produce
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Metadata
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=Heartbeat
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=DescribeGroups
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=Produce
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=ControlledShutdown
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=ListGroups
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=Metadata
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=DescribeGroups
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=UpdateMetadata
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchConsumer
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=GroupCoordinator
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=OffsetFetch
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=Fetch
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=JoinGroup
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=DescribeGroups
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=ListGroups
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=DescribeGroups
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Offsets
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=GroupCoordinator
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=JoinGroup
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=Offsets
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=Fetch
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Fetch
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=Metadata
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=UpdateMetadata
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=LeaderAndIsr
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=OffsetCommit
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=ListGroups
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchFollower
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=SaslHandshake
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=DescribeGroups
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=DescribeGroups
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=Heartbeat
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=LeaderAndIsr
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=GroupCoordinator
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=JoinGroup
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=Heartbeat
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=UpdateMetadata
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=FetchFollower
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=OffsetCommit
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=FetchConsumer
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=SyncGroup
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=SaslHandshake
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=StopReplica
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=UpdateMetadata
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=FetchFollower
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=LeaveGroup
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=OffsetFetch
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=OffsetCommit
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Metadata
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=ApiVersions
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=StopReplica
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=LeaderAndIsr
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=JoinGroup
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=FetchConsumer
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=StopReplica
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=Fetch
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=OffsetFetch
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=SyncGroup
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=Metadata
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=JoinGroup
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=SaslHandshake
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=UpdateMetadata
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=ControlledShutdown
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=ApiVersions
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=FetchConsumer
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=FetchFollower
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=LeaveGroup
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=GroupCoordinator
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=OffsetFetch
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=OffsetFetch
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=LeaveGroup
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=GroupCoordinator
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=SyncGroup
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=LeaderAndIsr
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=UpdateMetadata
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=OffsetCommit
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=Produce
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=ApiVersions
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=SyncGroup
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=Heartbeat
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=LeaveGroup
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=JoinGroup
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Heartbeat
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=ListGroups
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=ApiVersions
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=SaslHandshake
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=Heartbeat
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=UpdateMetadata
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Offsets
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=OffsetFetch
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=SyncGroup
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=LeaveGroup
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=ControlledShutdown
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=FetchFollower
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=FetchFollower
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=ListGroups
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=Offsets
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Produce
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=StopReplica
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=FetchConsumer
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Fetch
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=Metadata
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=GroupCoordinator
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=ControlledShutdown
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=ControlledShutdown
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=SyncGroup
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=Offsets
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=Produce
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=OffsetCommit
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=ControlledShutdown
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=LeaderAndIsr
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=UpdateMetadata
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=Offsets
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=OffsetFetch
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=LeaderAndIsr
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=Produce
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=Offsets
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=OffsetCommit
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=ListGroups
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=Fetch
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=SaslHandshake
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=ControlledShutdown
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=SyncGroup
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=FetchFollower
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=ListGroups
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=Metadata
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=LeaveGroup
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=SaslHandshake
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=DescribeGroups
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=OffsetCommit
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=SyncGroup
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=FetchConsumer
kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=LeaveGroup
kafka.network:type=RequestMetrics,name=RequestsPerSec,request=ApiVersions
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=StopReplica
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=ControlledShutdown
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=SaslHandshake
kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request=LeaderAndIsr
kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request=Produce
kafka.network:type=RequestMetrics,name=RemoteTimeMs,request=Produce
kafka.network:type=RequestMetrics,name=ThrottleTimeMs,request=ApiVersions
kafka.network:type=RequestMetrics,name=LocalTimeMs,request=Offsets

### Map<String, KafkaDelayedProperty> getDelayOperations()
kafka.server:type=Fetch
kafka.server:type=DelayedOperationPurgatory,name=PurgatorySize,delayedOperation=Fetch
kafka.server:type=DelayedOperationPurgatory,name=NumDelayedOperations,delayedOperation=Fetch
kafka.server:type=Produce
kafka.server:type=DelayedOperationPurgatory,name=PurgatorySize,delayedOperation=Produce
kafka.server:type=DelayedOperationPurgatory,name=NumDelayedOperations,delayedOperation=Produce
kafka.server:type=DelayedOperationPurgatory,name=PurgatorySize,delayedOperation=Heartbeat
kafka.server:type=DelayedOperationPurgatory,name=NumDelayedOperations,delayedOperation=Heartbeat
kafka.server:type=DelayedOperationPurgatory,name=PurgatorySize,delayedOperation=Rebalance
kafka.server:type=DelayedOperationPurgatory,name=NumDelayedOperations,delayedOperation=Rebalance

### Map<Integer, Map<String, Double>> getSocketServerMetrics()
kafka.server:type=socket-server-metrics,networkProcessor=0
kafka.server:type=socket-server-metrics,networkProcessor=1
kafka.server:type=socket-server-metrics,networkProcessor=2

------------------------------------------------------------------------------------------------

## Not Process
kafka.server:type=ReplicaFetcherManager,name=MaxLag,clientId=Replica
kafka.server:type=ReplicaFetcherManager,name=MinFetchRate,clientId=Replica
kafka.server:type=KafkaServer,name=yammer-metrics-count
kafka.server:type=kafka-metrics-count

## Under Processing

kafka.server:type=KafkaRequestHandlerPool,name=RequestHandlerAvgIdlePercent
kafka.server:type=controller-channel-metrics,broker-id=0
kafka.server:type=app-info,id=0

kafka.coordinator:type=GroupMetadataManager,name=NumOffsets
kafka.coordinator:type=GroupMetadataManager,name=NumGroups

kafka.network:type=Processor,name=IdlePercent,networkProcessor=0
kafka.network:type=Processor,name=IdlePercent,networkProcessor=1
kafka.network:type=Processor,name=IdlePercent,networkProcessor=2

kafka.network:type=RequestChannel,name=RequestQueueSize
kafka.network:type=RequestChannel,name=ResponseQueueSize,processor=0
kafka.network:type=RequestChannel,name=ResponseQueueSize,processor=1
kafka.network:type=RequestChannel,name=ResponseQueueSize,processor=2
kafka.network:type=RequestChannel,name=ResponseQueueSize
kafka.network:type=SocketServer,name=NetworkProcessorAvgIdlePercent

kafka:type=kafka.Log4jController

kafka.utils:type=Throttler,name=cleaner-io

kafka.log:type=LogCleaner,name=max-clean-time-secs
kafka.log:type=LogCleaner,name=cleaner-recopy-percent
kafka.log:type=LogCleaner,name=max-buffer-utilization-percent
kafka.log:type=LogCleanerManager,name=max-dirty-percent

kafka.controller:type=ControllerStats,name=UncleanLeaderElectionsPerSec
kafka.controller:type=ControllerStats,name=LeaderElectionRateAndTimeMs
kafka.controller:type=KafkaController,name=PreferredReplicaImbalanceCount
kafka.controller:type=KafkaController,name=ActiveControllerCount
kafka.controller:type=KafkaController,name=OfflinePartitionsCount
