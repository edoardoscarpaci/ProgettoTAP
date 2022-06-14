#! /bin/bash

[[ -z "${STREAMER_ACTION}" ]] && { echo "STREAMER_ACTION required"; exit 1; }
echo "Running action ${STREAMER_ACTION}.jar"
case ${STREAMER_ACTION} in
"lister")
java -jar lister.jar
;;
"streamer")
java -jar streamer.jar
;;
esac