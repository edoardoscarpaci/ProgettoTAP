/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package progetto.TAP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Properties;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In this example, we implement a simple LineSplit program using the high-level Streams DSL
 * that reads from a source topic "streams-plaintext-input", where the values of messages represent lines of text;
 * the code split each text line in string into words and then write back into a sink topic "streams-linesplit-output" where
 * each record represents a single word.
 */
public class IngredientLister{
	
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-ingredient-parser");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.100.23:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        final StreamsBuilder builder = new StreamsBuilder();

		final Serializer<JsonNode> jsonNodeSerializer = new JsonSerializer();
		final Deserializer<JsonNode> jsonNodeDeserializer = new JsonDeserializer();
		final Serde<JsonNode> jsonNodeSerde = Serdes.serdeFrom(jsonNodeSerializer,jsonNodeDeserializer);
		
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, jsonNodeSerde.getClass());
		
		KStream<String,JsonNode> recipes = builder.stream("Recipes", Consumed.with(Serdes.String(),jsonNodeSerde));

		Set<String> ingredientSet = new HashSet<String>();


		KTable<String, String> recipesParsed  = recipes.flatMapValues(value -> {
			JsonNode jsonIngredients = value.get("Ingredienti");
			List<String> KeyIngredients = new ArrayList<String>(); 			
			Iterator<String> iterator = jsonIngredients.fieldNames();
    		
			for (Iterator<String> i = iterator; i.hasNext(); ) {
				String e = i.next(); 
				if(!ingredientSet.contains(e)){
					KeyIngredients.add(e);
					ingredientSet.add(e);
				}
			}
			return KeyIngredients;
		}).toTable();

		recipesParsed.toStream().to("Ingredients",Produced.with(Serdes.String(),Serdes.String()));
		recipesParsed.toStream().print(Printed.toSysOut());
        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

		streams.cleanUp();
		streams.start();
}

}