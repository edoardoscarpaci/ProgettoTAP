#!/bin/bash
docker rm Kibana

docker build . --tag progetto:Kibana

docker stop Kibana
docker run -m 2048m -e ES_JAVA_OPTS="-Xms2g -Xmx2g" -e XPACK_SECURITY_ENABLED="false" --network progetto -p 9200:9200 -p 9300:9300 --ip 10.0.100.52  --name Kibana progetto:Kibana