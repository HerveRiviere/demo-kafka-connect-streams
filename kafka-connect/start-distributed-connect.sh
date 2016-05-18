#/bin/sh
WORK_DIR=./
KAFKA_DIR=<KAFKA-INSTALL-DIR>

nohup $KAFKA_DIR/bin/connect-distributed $WORK_DIR/conf/connect-distributed.properties.1 >/$WORK_DIR/log/connect-1.log &

nohup $KAFKA_DIR/bin/connect-distributed $WORK_DIR/conf/connect-distributed.properties.2 > $WORK_DIR/log/connect-2.log &