#!/bin/bash

AWS_SDK_VERSION="1.7.4"
AWS_HADOOP_VERSION="2.7.4"

/home/ubuntu/capri/spark-2.3.0/bin/spark-submit \
  --master k8s://172.31.16.123:6443 \
  --deploy-mode cluster \
  --name imdb \
  --class QueryRunner \
  --conf spark.hadoop.fs.s3n.awsAccessKeyId="<AWS_KEY>" \
  --conf spark.hadoop.fs.s3n.awsSecretAccessKey="<AWS_SECRET>" \
  --conf spark.hadoop.fs.s3n.impl="org.apache.hadoop.fs.s3native.NativeS3FileSystem" \
  --jars http://central.maven.org/maven2/org/apache/hadoop/hadoop-aws/${AWS_HADOOP_VERSION}/hadoop-aws-${AWS_HADOOP_VERSION}.jar,http://central.maven.org/maven2/com/amazonaws/aws-java-sdk/${AWS_SDK_VERSION}/aws-java-sdk-${AWS_SDK_VERSION}.jar \
  --conf spark.imdb.input.path="s3n://<path_to_dataset>" \
  --conf spark.executor.instances=2 \
  --conf spark.kubernetes.container.image=spark:main \
  --conf spark.kubernetes.authenticate.driver.serviceAccountName=default \
  --conf spark.kubernetes.job.bid=0.7 \
  --conf spark.kubernetes.job.slowdown=2.5 \
  --conf spark.kubernetes.job.restarts=3 \
  --conf spark.kubernetes.pod.cpu=1 \
  --conf spark.kubernetes.pod.memory=1.0 \
  --conf spark.kubernetes.user.id="job" \
  local:///opt/spark/examples/target/spark-bench_2.11-1.0.jar \
  imdb 1

