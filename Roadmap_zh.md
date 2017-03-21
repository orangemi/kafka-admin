设计思路
=======

## 设计元素
 - [ ] Broker配置
 - [ ] Topic配置
 - [ ] Topic配置

## 指标信息 Metrics
```
 |- Broker Metrics
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
 |   |   |- BytesInPerSec
 |   |   |- BytesOutPerSec
 |   |- Disk
 |   |   |- LogEndOffset(Sum)
 |   |   |- LogCount(=Sum(LogEndOffset) - Sum(LogBeginOffset))
 |   |   |- Log.Size(Sum)
 |   |   |- Log.NumLogSegments(Sum)
 |   |- Disk IO
 |   |   |- LogFlushRateAndTimeMs
 |   |- Requests Rate /PerSec
 |   |   |- Produce
 |   |   |- FetchConsumer
 |   |   |- FetchFollower
 |   |- Requests Time(TotalTimeMs)
 |   |   |- Produce
 |   |   |- FetchConsumer
 |   |   |- FetchFollower
 |   |- DeplayedOperationPurgatory
 |   |   |- Produce
 |   |   |- Fetch
 |- Topic Metrics
 |   |- Consumer
 |   |- Offset
 |   |   |- Begin
 |   |   |- End
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
