# 系统数据流程设计

<img src="/resources/process_design.png" width="757"/><br/>

（注：本项目不包括实时数仓部分）
## 电商业务表
<img src="/resources/business_table.png" width="757"/><br/>
## 架构设计

<img src="/resources/architecture.png" width="757"/><br/>
## 集群服务规划

| 服务名称             | 子服务               |     node1 |     node2      | node3 |
|------------------|-------------------|---------------|-----------------|-----------------|
| HDFS             | NameNode          | ✓             |                 |                 |
|                  | DataNode          | ✓             | ✓               | ✓               |
|                  | SecondaryNameNode |               |                 | ✓               |
| YARN             | Resourcemanager   |               | ✓               |                 |
|                  | NodeManager       | ✓             | ✓               | ✓               |
| Zookeeper        | Zookeeper Server  | ✓             | ✓               | ✓               |
| Flume（采集日志）  | Flume             | ✓             | ✓               |                 |
| Kafka            | Kafka             | ✓             | ✓               | ✓               |
| Flume（消费日志）   | Flume           |               |                 | ✓               |
| Flume（消费业务）   | Flume           |               |                 | ✓               |
| MySQL            | MySQL             | ✓             |                 |                 |
| DataX            | DataX             | ✓             | ✓               | ✓               |
| Maxwell          | Maxwell           | ✓             |                 |                 |
| Hive             | -                 | ✓             | ✓               | ✓               |
| Spark            | -                 | ✓             | ✓               | ✓               |
| DolphinScheduler | MasterServer      | ✓             |                 |                 |
|                  | WorkerServer      | ✓             | ✓               | ✓               |


# 数据采集

## 用户行为日志采集

1. 模拟数据生成：使用applog生成模拟数据到磁盘上（修改application.yml指定日期后，通过 `getlog.sh` 生成数据 ）
2. 日志同步：启动zookeeper、hadoop、kafka、flume1、flume2，自动识别磁盘文件改动，同步至HDFS上



## 业务数据采集

1. 模拟数据生成：使用dblog生成模拟数据到mysql上（修改application.properties指定日期后，通过jar包生成数据 ）
2. 全量表导入：启动hadoop，使用 `gen_import_config.sh` 生成dataX使用的json文件（执行一次即可），再通过 `mysql_to_hdfs_full.sh all 日期` 同步数据（注意是最新日期）
3. 增量表同步：启动mysql、zookeeper、hadoop、kafka、flume3、maxwell，使用 `mysql_to_kafka_inc_init.sh all` 同步首日数据（执行一次即可），后续如果需要生成其它日期的数据时，需要先修改maxwell配置文件里的日期并重启maxwell，再使用dblog（修改配置文件与前面一致）生成新一天的数据，模拟增量同步


# 数据仓库架构图

采用维度建模

<img src="/resources/warehouse.jpg" width="550"/><br/>


## Hive表

建表与数据导入，使用的引擎为 Hive on Spark

1. hdfs -> ods，包括行为日志（log）和业务数据（db）
2. ods -> dwd
3. ods -> dim
4. dwd -> dws，包括dws_1d、dws_nd 和 dws_td
5. dws -> ads


# 调度器

使用 dolphin scheduler 进行脚本调度

1. 头尾的 mysql <-> hdfs 脚本调用的是 dataX
2. 中间的数仓脚本调用的是 Hive on Spark 

# 指标看板

最终在SuperSet上集成离线指标看板效果
<img src="/resources/index.png" width="550"/><br/>
