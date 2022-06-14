#! /bin/bash

docker stop spark
docker rm spark

cd apps/tapw2v
mvn clean package install
cd ../..

docker build . --tag progetto:spark

docker run -m 1024m --name spark -e SPARK_ACTION="spark-submit-apps" --network progetto  progetto:spark progetto.tap.TapSpark tapw2v/target/stepsW2V-1.0.jar