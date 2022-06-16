#!/bin/bash
[[ -z "${SPARK_ACTION}" ]] && { echo "SPARK_ACTION required"; exit 1; }


echo "Running action ${SPARK_ACTION}"
case ${SPARK_ACTION} in
"example")
echo "Running example ARGS $@"
./bin/run-example $@
;;
"spark-shell")
./bin/spark-shell --master local[2]
;;
"pyspark")
./bin/pyspark --master local[2]
jupyter notebook --ip 0.0.0.0 --no-browser --allow-root
;;
"spark-submit-python")
 ./bin/spark-submit --packages $2 /opt/tap/$1
;;
"spark-submit-apps")
echo "Running spark-submin --class $1 /opt/tap/apps/$2"
./bin/spark-submit --class $1 --executor-memory 1G --total-executor-cores 2 /opt/tap/apps/$2
;;

#--packages $3
"pytap")
cd /opt/tap/
#python ${TAP_CODE}
python3 ${TAP_CODE}
;;
"bash")
while true
do
	echo "Keep Alive"
	sleep 10
done
;;
esac