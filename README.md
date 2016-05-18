#Demo Kafka Connect & Kafka Streams

##Prerequisites : 

* Kafka 0.10+. You can download a tech preview confluent platform [here](http://www.confluent.io/developer#download)
* A running kafka cluster (one node in localhost is ok !)
* A running postgres database
 
 
##Create the postgres table

<pre>CREATE TABLE message(date TIMESTAMP NOT NULL,id SERIAL PRIMARY KEY, username VARCHAR(100), message TEXT);</pre>

##Kafka Connect

###Start Kafka connect cluster

* In kafka-connect/start-distributed-connect.sh, replace <KAFKA-INSTALL-DIR> by the path you installed Kafka 0.10
* Review the configration specified in  kafka-connect/conf/connect-distributed.properties.1 and kafka-connect/conf/connect-distributed.properties.2
* Launch kafka-connect/start-distributed-connect.sh
* Check log trace in kafka-connect/log
* Check HTTP response to localhost:8085 and localhost:8086

###JDBC connector registration

* Check and execute  kafka-connect/start-distributed-jdbc.sh
* http://localhost:8085/connectors must return ['postgres-source']

### Insert into postgres and check topic table-message 

* Open a console-consumer 
<pre>$KAFKA_HOME/bin/kafka-console-consumer --zookeeper localhost:2181 --topic table-message</pre>
* Insert a record into postgres message table
 <pre>insert into message(date,username,message,city) values (now(),'Herve','Hello kafka User Group','Paris');</pre>
* Record must appears in the console-consumer


##Kafka Stream 

* Import the maven project into IntelliJ/ Eclipse...

###Insert into postgres and check topic table-message-kafka  
* Execute the main class DemoKafkaStreams.java
* Open a console-consumer 
<pre>$KAFKA_HOME/bin/kafka-console-consumer --zookeeper localhost:2181 --topic table-message-kafka</pre>
* Insert records in message postgre table. If the message contains "kafka user group" it must appears in table-message-kafka topic.
* To check the KTable, insert the same message two times. The username must appears in table-message-kafka topic.

###Check kafka-user-group-example-kafka_user_count-changelog topic

* If you want to check the persistence topic of the Ktable, execute : 
<pre>$KAFKA_HOME/bin/kafka-console-consumer --zookeeper localhost:2181 --topic kafka-user-group-example-kafka_user_count-changelog    \
--formatter kafka.tools.DefaultMessageFormatter           --property print.key=true           \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer    \       
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer \
--from-beginning</pre>