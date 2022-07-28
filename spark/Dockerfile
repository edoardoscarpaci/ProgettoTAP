#3.2.1 pyspark
FROM openjdk:8-jre

ENV SPARK_VERSION=2.4.8
ENV SPARK_DIR=/opt/spark
ENV HADOOP_VERSION=2.7
ENV PATH ${SPARK_DIR}/bin:$PATH

ENV SPARK_HOME=${SPARK_DIR}
ENV PYSPARK_PYTHON=/usr/bin/python3
ENV PYTHONPATH=${SPARK_DIR}/python/:${SPARK_DIR}/python/lib/py4j-0.10.7-src.zip:${SPARK_DIR}/python/lib/pyspark.zip

ADD setup/spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz /opt

RUN apt-get update && apt-get -y install bash netcat libsqlite3-dev procps 

RUN apt install -y build-essential zlib1g-dev libncurses5-dev libgdbm-dev libnss3-dev libssl-dev libreadline-dev libffi-dev wget
RUN wget https://www.python.org/ftp/python/3.7.5/Python-3.7.5.tgz
RUN tar -xf Python-3.7.5.tgz

WORKDIR Python-3.7.5

RUN ./configure --enable-optimizations
RUN make -j 4 install

RUN pip3 install numpy elasticsearch notebook

RUN ln -s /opt/spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION} ${SPARK_DIR} 
RUN ln -s /usr/local/bin/python3 /usr/local/bin/python
RUN ln -s /usr/local/bin/python3 /usr/bin/python3


ADD setup/conf/* ${SPARK_DIR}/conf/
ADD spark-manager.sh ${SPARK_DIR}/bin/spark-manager
CMD alias python=python3

WORKDIR ${SPARK_DIR}

RUN mkdir jupyter

ENTRYPOINT ["spark-manager"]