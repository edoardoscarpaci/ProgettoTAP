#!/bin/bash
ZK_DATA_DIR=/tmp/zookeeper
ZK_SERVER="localhost"
[[ -z "${KAFKA_ACTION}" ]] && { echo "KAFKA_ACTION required"; exit 1; }
[[ -z "${KAFKA_DIR}" ]] && { echo "KAFKA_DIR missing"; exit 1; }
# ACTIONS start-zk, start-kafka, create-topic, 

echo "Running action ${KAFKA_ACTION} (Kakfa Dir:${KAFKA_DIR}, ZK Server: ${ZK_SERVER})"
case ${KAFKA_ACTION} in
"start-zk")
echo "Starting ZK"
mkdir -p ${ZK_DATA_DIR}; # Data dir is setup in conf/zookeeper.properties
cd ${KAFKA_DIR}
zookeeper-server-start.sh config/zookeeper.properties
;;
"start-kafka")
cd ${KAFKA_DIR}
kafka-server-start.sh config/server.properties
;;
"create-topic")
cd ${KAFKA_DIR}
kafka-topics.sh --create --bootstrap-server ${KAFKA_SERVER}:9092 -config max.message.bytes=10000121 --replication-factor 1 --partitions 1 --topic ${KAFKA_TOPIC}
;;
"kafka-connect-elastic")
cd ${KAFKA_DIR}
bin/connect-standalone.sh config/connect-standalone.properties config/elastic-search.properties
;;
esac

