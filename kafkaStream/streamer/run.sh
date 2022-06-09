#!/bin/bash

docker rm kafkaStream

docker build . --tag progetto:kafkaStream

docker stop kafkaStream
docker run -m 2048m --network progetto --name kafkaStream progetto:kafkaStream
