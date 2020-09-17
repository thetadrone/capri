# Spark Benchmarks
This repository consists of a series of Spark benchmarks:

### Running Spark Benchmarks ###

Build the benchmarks:
```bash
sbt package
```

Run a JOB query with Spark:
```bash
$SPARK_HOME/bin/spark-submit 
	--class ImdbQuery 
	--conf spark.imdb.input.path=<PATH_TO_DATASET> 
	<PATH_TO_JAR> <QUERY_IDENTIFIER>
```

Run a TPCDS query with Spark:
```bash
$SPARK_HOME/bin/spark-submit
	--class TpcdsQuery
	--conf spark.tpcds.input.path=<PATH_TO_DATASET>
	<PATH_TO_JAR> <QUERY_IDENTIFIER>
```

