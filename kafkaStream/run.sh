#!/bin/bash


docker build . --tag progetto:kafkaStream

docker rm kafkaStream
docker rm kafkaLister

docker stop kafkaStream
docker stop kafkaLister

docker run -m 1024m -e STREAMER_ACTION=streamer --network progetto --name kafkaStream progetto:kafkaStream 
docker run -m 1024m -e STREAMER_ACTION=lister --network progetto --name kafkaLister progetto:kafkaStream 
