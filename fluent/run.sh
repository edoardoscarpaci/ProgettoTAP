#!/bin/bash

docker rm fluent

docker build . --tag progetto:fluent

docker stop fluent
docker run -m 1024m --network progetto --name fluent progetto:fluent -c opt/fluent.conf
