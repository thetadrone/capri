# Capri Spot Market #

Capri is a spot market system for cloud data analytics. It provides a framework for running Spark applications in containers and a scheduling system for deploying those containers on a Kubernetes cluster. Capri uses a novel bribe scheduling policy to operate the spot market and employs a preempt-resume mechanism to allow applications to make progress in the face of a time-varying spot price.

### Prerequisites ###
External libraries:

* aws-java-sdk-1.7.4.jar
* hadoop-aws-2.7.4.jar

Internal libraries:

* capri.jar (build an artifact in IntelliJ)
* spark-benchmarks_2.11-1.0.jar 

### Spark Applications ###
To run Spark applications we need to bundle the runtime system of the Spark framework inside containers. We do so using the Spark [capabilities](https://spark.apache.org/docs/latest/running-on-kubernetes.html) of allocating resources from Kubernetes. We have made small adjustments of these capabilities in order to be able to re-submit an application containers in case they are revoked by Capri and to push down to Capri information about the user's bid.

Build the Spark framework:
```bash
$SPARK_HOME/build/mvn -Pkubernetes -Phadoop-2.7 -DskipTests clean package
$SPARK_HOME/build/sbt package -J-Xmx2G -J-Xss2M
```

Build and package benchmarking suites:
```bash
cp spark-benchmarks_2.11-1.0.jar $SPARK_HOME/examples/target
```

Include external libraries:
```bash
mkdir $SPARK_HOME/lib
cp aws-java-sdk-1.7.4.jar $SPARK_HOME/lib
cp hadoop-aws-2.7.4.jar $SPARK_HOME/lib
```

Create a Docker image:
```bash
sudo $SPARK_HOME/bin/docker-image-tool.sh -t main build
```

Note that this step needs to be performed manually on each node of the Kubernetes cluster. In the future it can be automated by pushing the Spark image to an image registry.

