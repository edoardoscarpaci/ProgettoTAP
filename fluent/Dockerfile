FROM fluent/fluentd:edge-debian
USER root

RUN apt-get update && apt-get -y install build-essential 
RUN gem install fluent-plugin-kafka --no-document
ADD fluent.conf /opt/fluent.conf
ADD fluent.conf /fluentd/etc/
ADD recipe.json /opt/recipe.json

RUN sleep 2

# to build with
# docker build . --tag progetto:fluent
# and run with 
# docker run -m 1024m -c /opt/fluent.conf --network progetto --ip 10.0.100.1 --name fluent progetto:fluent
