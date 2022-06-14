FROM openjdk:8-jre
ENV PATH $SPARK_DIR/bin:$PATH
ENV SPARK_VERSION=3.2.1
ENV SPARK_DIR=/opt/spark
ENV PATH $SPARK_DIR/bin:$PATH

ADD setup/spark-${SPARK_VERSION}-bin-hadoop3.2.tgz /opt

RUN apt-get update && apt-get -y install bash python3 python3-pip netcat

RUN pip3 install pyspark numpy elasticsearch

ADD apps /opt/tap/apps

RUN ln -s /opt/spark-${SPARK_VERSION}-bin-hadoop3.2 ${SPARK_DIR} 

ADD setup/conf/* ${SPARK_DIR}/conf/

ADD spark-manager.sh $SPARK_DIR/bin/spark-manager

WORKDIR ${SPARK_DIR}
ENTRYPOINT [ "spark-manager" ]