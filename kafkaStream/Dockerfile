FROM openjdk:11
ADD target/IngredientParser-jar-with-dependencies.jar /opt/streamer.jar
ADD target/IngredientLister-jar-with-dependencies.jar /opt/lister.jar
ADD streamer-manager.sh /opt/streamer-manager

ENV PATH /opt/:$PATH
WORKDIR /opt/

ENTRYPOINT ["streamer-manager"]  