设计思路
=======

## 设计元素
 - [ ] Broker配置
 - [ ] Topic配置
 - [ ] Topic配置

## 指标信息 Metrics
```
 |- Brokers
 |   |- Replica Manager(Alert)
 |   |   |- UnderReplicatedPartition
 |   |   |- OfflinePartitionsCount
 |   |   |- ActiveControllerCount
 |   |   |- PreferredReplicaImbalanceCount
 |   |- Relica Manager(Count Metrics)
 |   |   |- LeaderCount
 |   |   |- PartitionCount
 |   |- Relica Manager(Rate Metrics)
 |   |   |- LeaderElectionRateAndTimeMs
 |   |   |- IsrShrinksPerSec
 |   |   |- IsrExpandsPerSec
 |   |   |- MaxLag(clientId=Replica)
 |   |- Messages/Bytes IO
 |   |   |- MessagesInPerSec
 |   |   |- BytesRejectedPerSec
 |   |   |- BytesInPerSec
 |   |   |- BytesOutPerSec
 |   |- Log / Disk
 |   |   |- LogEndOffset(Sum)
 |   |   |- LogCount(=Sum(LogEndOffset) - Sum(LogBeginOffset))
 |   |   |- Size(Sum)
 |   |   |- NumLogSegments(Sum) b
 |   |- Disk IO
 |   |   |- LogFlushRateAndTimeMs
 |   |- Requests (Rate / Time)
 |   |   |- FetchConsumer
 |   |   |- OffsetCommit
 |   |   |- Produce
 |   |   |- SyncGroup
 |   |   |- DeleteTopics
 |   |   |- DescribeGroups
 |   |   |- JoinGroup
 |   |   |- Fetch
 |   |   |- GroupCoordinator
 |   |   |- ListGroups
 |   |   |- OffsetFetch
 |   |   |- CreateTopics
 |   |   |- StopReplica
 |   |   |- LeaderAndIsr
 |   |   |- ControlledShutdown
 |   |   |- Heartbeat
 |   |   |- LeaveGroup
 |   |   |- Offsets
 |   |   |- ApiVersions
 |   |   |- FetchFollower
 |   |   |- Metadata
 |   |   |- UpdateMetadata
 |   |   |- SaslHandshake
 |   |- DeplayedOperationPurgatory
 |   |   |- Produce
 |   |   |- Fetch
 
 |- Broker Detail(brokerId=)
 |   |- Summary
 |   |- Metrics (All listed above /w chart)
 |   |- Topic Metrics
 |   |   |- Summary
 |   |   |   |- LeaderCount
 |   |   |   |- PartitionCount
 |   |   |- Log / Disk
 |   |   |   |- LogEndOffset(Sum)
 |   |   |   |- LogStartOffset(Sum)
 |   |   |   |- LogSize(Sum)
 |   |   |   |- NumLogSegments(Sum)
 |   |   |- Request
 |   |   |   |- FailedProduceRequestsPerSec
 |   |   |   |- FailedFetchRequestsPerSec
 |   |   |   |- TotalProduceRequestsPerSec
 |   |   |   |- TotalFetchRequestsPerSec
 |   |   |- Network IO
 |   |   |   |- BytesRejectedPerSec
 |   |   |   |- BytesInPerSec
 |   |   |   |- BytesOutPerSec
 |   |   |   |- MessagesInPerSec
 |   |- Partition Metrics
 |   |   |- LogEndOffset
 |   |   |- LogBeginOffset
 |   |   |- LogSize
 |   |   |- NumLogSegments
 |   |   |- InSyncReplicasCount
 |   |   |- UnderReplicated
 |   |   |- ReplicasCount

 |- Topics
 |   |- Offset
 |   |   |- Begin(Sum)
 |   |   |- End(Sum)
 |   |- PartitionCount
 |   |- Requests
 |   |   |- TotalProduceRequestsPerSec /G broker
 |   |   |- TotalFetchRequestsPerSec /G broker
 |   |   |- FailedProduceRequestsPerSec /G broker
 |   |   |- FailedFetchRequestsPerSec /G broker
 |   |- IO
 |   |   |- MessagesIn /G broker
 |   |   |- BytesIn /G broker
 |   |   |- BytesOut /G broker
 
 |- Broker-Topic-partition Metrics
```

### Messages/Bytes In/Out
 包含多个维度：
 - 按Broker区分：用于查看Broker健康状态，是否均衡
 - 按Topic区分：用于查看热门Topic
 - 按Broker-Topic区分：用于查看Topic-Partition Leader状态
