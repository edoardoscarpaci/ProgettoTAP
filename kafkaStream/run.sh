#!/bin/bash
docker rm kafkaStream

docker build . --tag progetto:kafkaStream

docker stop kafkaStream
docker run -m 2048m -e STREAMER_ACTION=$1 --network progetto --name kafkaStream progetto:kafkaStream 
