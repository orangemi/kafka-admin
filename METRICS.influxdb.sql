# 为什么需要做？
## 当后端数据采集不能统计时，需要通过数据库功能统计

## Broker BytesOutPerSec Smooth by 30s
SELECT moving_average(b, 3) FROM (
  SELECT non_negative_derivative(mean("Count"), 1s) AS b
  FROM "broker-BytesOutPerSec"
  WHERE "broker" = '1' AND time > now() - 1h
  GROUP BY time(1s) fill(none)
)

## Broker Total RequestsPerSec(WIP)
select mean(Count) from RequestsPerSec Where "broker"='1' AND time > now() - 5m group by time(5s), request fill(previous)