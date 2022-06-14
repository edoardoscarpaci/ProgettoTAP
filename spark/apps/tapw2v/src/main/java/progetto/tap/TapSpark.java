package progetto.tap;

import java.util.*;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.Tuple2;


public class TapSpark {
    public static void main(String[] args) throws Exception {
        String brokers = "10.0.100.23:9092";
        String groupId = "steps-w2v-consumer-group"; // Kafka Consumer Group

	
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", brokers);
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", groupId);
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);
		

        SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount");

		Collection<String> topics = Arrays.asList("CleanedRecipes");




/*
        // Create context with a 2 seconds batch interval
		

		StructType schema = new StructType();
		schema.add("Link", DataTypes.StringType);
		schema.add("Steps", DataTypes.StringType);
		schema.add("Ingredients", 
					DataTypes.createMapType(DataTypes.StringType,
											new StructType().add("Nome",DataTypes.StringType)
															.add("Peso",DataTypes.StringType)
															.add("Note",DataTypes.StringType)));
		schema.add("Nome", DataTypes.StringType);


		SparkSession spark = SparkSession
		.builder()
		.config(sparkConf)
		.getOrCreate();
/* 
		Dataset<Row> df = spark.readStream()
  							.format("kafka")
  							.option("kafka.bootstrap.servers", "10.0.100.23:9092")
  							.option("subscribe", "CleanedRecipes")
  							.load();
		df.select(functions.from_json(new Column("value"), schema).as("data"));


		Dataset<Row> df = spark
		.readStream()
		.format("kafka")
		.option("kafka.bootstrap.servers", "host1:port1,host2:port2")
		.option("subscribe", "topic1")
		.option("value.deserializer" ,StringDeserializer.class.toString())
		.option("fetch.max.bytes","524288000")
		.load();
		df = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)");


		StreamingQuery query = df.writeStream()
		.outputMode(OutputMode.Append())
		.format("console")
		.start();


		query.awaitTermination();*/

		JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(2));

        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                );

        JavaPairDStream<String, String> stringStringJavaPairDStream = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
        stringStringJavaPairDStream.print();

        // Start the computation
        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
