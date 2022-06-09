FROM openjdk:8-jre-alpine
ENV PATH /opt/kafka/bin:$PATH

ENV KAFKA_DIR "/opt/kafka"
ENV KAFKA_PLUGIN "/opt/plugins"
ARG KAFKA_VERSION="2.13-3.2.0"


ENV CLASSPATH=${KAFKA_PLUGIN}/*

RUN apk update && apk add --no-cache bash gcompat

# Installing Kafka
# ADD will automatically extract the file
ADD kafka_${KAFKA_VERSION}.tgz /opt

# Create Sym Link 
RUN ln -s /opt/kafka_${KAFKA_VERSION} ${KAFKA_DIR} 
# Copy All conf here

ADD kafka-manager.sh ${KAFKA_DIR}/bin/kafka-manager
RUN chmod u+x ${KAFKA_DIR}/bin/kafka-manager

RUN mkdir ${KAFKA_PLUGIN}

ADD kafka-connect-esearch.zip /tmp/
RUN unzip /tmp/kafka-connect-esearch.zip -d /tmp/
RUN cp /tmp/confluentinc-kafka-connect-elasticsearch-11.1.8/lib/* ${KAFKA_PLUGIN}

ADD conf/* ${KAFKA_DIR}/config/

RUN echo "plugin.path="${KAFKA_PLUGIN} >> ${KAFKA_DIR}/config/connect-standalone.properties
# Copy All conf here

ENTRYPOINT ["kafka-manager"]
#docker run -m 2048m --network progetto --ip 10.0.100.1 --name kafka progetto:kafka