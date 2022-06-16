#! /bin/bash

docker stop spark
docker rm spark

#cd apps/tapw2v
#mvn clean package install
#cd ../..

docker build . --tag progetto:spark
#docker run -m 1024m --name spark -e SPARK_ACTION="spark-submit-apps" --network progetto  progetto:spark progetto.tap.TapSpark tapw2v/target/stepsW2V-1.0.jar
docker run -m 2048m -v $(pwd)/jupyter:/opt/spark/jupyter --name spark -e SPARK_ACTION="pyspark" --network progetto -p 8888:8888 --ip 10.0.100.80 progetto:spark
