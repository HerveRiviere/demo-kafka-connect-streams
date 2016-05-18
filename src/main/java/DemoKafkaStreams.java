import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import java.util.Properties;

/**
 * Created by adminuser on 09/05/16.
 */
public class DemoKafkaStreams {



  public static void main(String[] args) throws Exception {
    Properties streamsConfiguration = new Properties();
    streamsConfiguration.put(StreamsConfig.JOB_ID_CONFIG, "kafka-user-group-example");
    // Where to find Kafka broker(s).
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    // Where to find the corresponding ZooKeeper ensemble.
    streamsConfiguration.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
    // Specify default (de)serializers for record keys and for record values.
    streamsConfiguration.put(StreamsConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    streamsConfiguration.put(StreamsConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
  // Default serdes for values of data records
    streamsConfiguration.put(StreamsConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    streamsConfiguration.put(StreamsConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    streamsConfiguration.put(StreamsConfig.AUTO_OFFSET_RESET_CONFIG, "latest");


    KStreamBuilder builder = new KStreamBuilder();

    StreamsConfig config = new StreamsConfig(streamsConfiguration);



  //KSTREAMS


    //read from "table-message" topic
    KStream<String, String> message = builder.stream(new StringDeserializer(), new StringDeserializer(), "table-message");


    KStream<String, String> messageFiltered = message.filter((k, v) -> v.toLowerCase().contains("kafka user group"));


    //Send output to "table-message-kafka" topic
    messageFiltered.to("table-message-kafka");




    //KTABLES
    //countBy username (countByKey)
    KTable<String, Long> userTable = messageFiltered.map((k, v) -> new KeyValue<>(getUsername(v), new Long(1))).
                                      countByKey(new StringSerializer(), new LongSerializer(), new StringDeserializer(), new LongDeserializer(), "kafka_user_count");


    //Filter and publish
    userTable.toStream().filter((k,v)-> v >= 2).map((k, v) -> new KeyValue<>(k, k + " appears " + v.toString() + " times !")).to("table-message-kafka");


    KafkaStreams streams = new KafkaStreams(builder, config);
    streams.start();

    streams.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Error");
        System.out.println(e.toString());
        System.out.println(e.getCause().fillInStackTrace());
      }
    });

  }




  public static String getUsername(String message){
    return message.substring(message.indexOf("username\":\""),message.indexOf("\",\"message"));
  }

}
