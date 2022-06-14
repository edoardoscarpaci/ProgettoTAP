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
public class IngredientParser {
	
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-ingredient-lister");
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
		

		KStream<String,JsonNode > recipes = builder.stream("Recipes", Consumed.with(Serdes.String(),jsonNodeSerde));

		KTable<String, JsonNode> recipesParsed  = recipes.mapValues(value -> {
			if(value == null){
				System.out.println("Value null");
			}

			JsonNode jsonIngredients = value.get("Ingredienti");
			List<String[]> ingredients = new ArrayList<String[]>();
			List<String> KeyIngredients = new ArrayList<String>(); 

			System.out.println(jsonIngredients.toPrettyString());
			
			Iterator<String> iterator = jsonIngredients.fieldNames();
    		iterator.forEachRemaining(e -> KeyIngredients.add(e));

		
			for(String key : KeyIngredients) {
				String[] values ={key,jsonIngredients.get(key).asText()};
				ingredients.add(values);
			}
			
			Recipe recipe = new Recipe(value.get("Steps").asText(), value.get("Nome").asText(),ingredients,value.get("Link").asText());
			return recipe;
		}).mapValues(recipe->{
			List<String[]> cleanedIngredients = new ArrayList<String[]>();
			for(String[] ingredient : recipe.getIngredients()){
				cleanedIngredients.add(parseIngredient(ingredient[0],ingredient[1]));
			}

			JsonNode recipeCleaned = JsonNodeFactory.instance.objectNode();
			
			((ObjectNode)recipeCleaned).put("Link",recipe.getLink());
			((ObjectNode)recipeCleaned).put("Steps",recipe.getSteps());
			((ObjectNode)recipeCleaned).put("Nome",recipe.getName());

			ArrayNode ingredientNode = ((ObjectNode)recipeCleaned).putArray("Ingredienti");
			for(String[] cleanedIngredient : cleanedIngredients){
				ObjectNode cleanedNode = ingredientNode.addObject();
				cleanedNode.put("Nome",cleanedIngredient[0]);
				cleanedNode.put("Peso",cleanedIngredient[1]);
				cleanedNode.put("Note",cleanedIngredient[2]);
			}

			System.out.println(recipeCleaned.toPrettyString());

			return recipeCleaned;
		}).toTable();
		recipesParsed.toStream().to("CleanedRecipes",Produced.with(Serdes.String(),jsonNodeSerde));
		recipesParsed.toStream().print(Printed.toSysOut());
        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

		streams.cleanUp();
		streams.start();
    }

	static private String[] parseIngredient(String name,String ingredient){
		Pattern pattern = Pattern.compile(" ?([A-zÀ-ú ,.]+|[(]?[A-zÀ-ú ,.0-9\"\"]+[)]) ?([0-9]+)([a-zA-z]*)|( ?[(]?[A-zÀ-ú ,.0-9\"\"]+[)])([a-zA-Z.]+)", Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(ingredient);
		String ingredientCleaned = ingredient;
		String note = "";
		System.out.println(name + " " + ingredient + " group finded: "+ matcher.groupCount());

		if(matcher.find()){
			if(matcher.group(1) != null && !matcher.group(1).isEmpty()){
				note = matcher.group(1);
			}

			if(matcher.group(2) != null && !matcher.group(2).isEmpty()){
				ingredientCleaned = matcher.group(2);
				if(matcher.group(3) != null && !matcher.group(3).isEmpty()){
					ingredientCleaned += matcher.group(3);
				} 
			}
			else if(matcher.group(4) != null && !matcher.group(4).isEmpty() && matcher.group(5) != null && !matcher.group(5).isEmpty() ){
				note = matcher.group(4);
				ingredientCleaned = matcher.group(5);
			}
		}

		String values[] = {name,ingredientCleaned,note};
		return values;
	};
}