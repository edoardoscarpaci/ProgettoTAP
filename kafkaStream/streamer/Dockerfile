FROM openjdk:11
ADD target/streamer-1-jar-with-dependencies.jar /opt/streamer.jar
WORKDIR /opt/
ENTRYPOINT [ "java","-jar","streamer.jar"]  