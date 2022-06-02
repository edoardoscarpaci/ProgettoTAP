FROM openjdk:8-jre-alpine
ENV PATH /opt/kafka/bin:$PATH
ENV KAFKA_DIR "/opt/kafka"
ARG KAFKA_VERSION="2.13-3.2.0"

RUN apk update && apk add --no-cache bash gcompat

# Installing Kafka
# ADD will automatically extract the file
ADD kafka_${KAFKA_VERSION}.tgz /opt

# Create Sym Link 
RUN ln -s /opt/kafka_${KAFKA_VERSION} ${KAFKA_DIR} 
# Copy All conf here

ADD kafka-manager.sh ${KAFKA_DIR}/bin/kafka-manager
RUN chmod u+x ${KAFKA_DIR}/bin/kafka-manager

# Copy All conf here
ADD conf/* ${KAFKA_DIR}/config/

ENTRYPOINT ["kafka-manager"]
#docker run -m 2048m --network progetto --ip 10.0.100.1 --name kafka progetto:kafka