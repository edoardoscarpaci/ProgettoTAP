FROM confluentinc/cp-schema-registry:7.0.3
ADD schema.json /opt/cleanedRecipes.json
ADD schema-registry.properties /opt/schema-registry.properties
ENTRYPOINT ["schema-registry-start","/opt/schema-registry.properties"]