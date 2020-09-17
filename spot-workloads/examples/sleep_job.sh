#!/bin/bash

./bin/spark-submit \
  --master k8s://172.31.14.180:6443 \
  --deploy-mode cluster \
  --name sparkpi \
  --class org.apache.spark.examples.SparkPi \
  --conf spark.executor.instances=1 \
  --conf spark.kubernetes.container.image=spark:main \
  --conf spark.kubernetes.authenticate.driver.serviceAccountName=default \
  --conf spark.kubernetes.job.bid=0.7 \
  --conf spark.kubernetes.job.slowdown=2.5 \
  --conf spark.kubernetes.job.restarts=3 \
  --conf spark.kubernetes.pod.cpu=1 \
  --conf spark.kubernetes.pod.memory=1.0 \
  --conf spark.kubernetes.user.id="spot-user-1" \
  local:///opt/spark/examples/target/original-spark-examples_2.11-2.3.0.jar 2000

