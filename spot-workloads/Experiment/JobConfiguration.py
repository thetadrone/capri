import configparser

class JobConfiguration:
  """Creates a Spark multi-container job for Capri"""
  def __init__(self, config, bench, jobname, jobid, size, containers, bid, slowdown, restarts):
    self.config = config
    self.bench = bench
    self.jobname = jobname
    self.jobid = jobid
    self.size = size
    self.containers = containers
    self.bid = bid
    self.slowdown = slowdown
    self.restarts = restarts

  def get_string(self):
    return self.jobname + " " + str(self.jobid) + " " + str(self.bid) + " " + str(self.size)
    
  def build(self):
    cmd = eval(self.config['SPARK']['SPARK_SUBMIT'])\
        + " --master %s" % eval(self.config['SPARK']['CLUSTER_MANAGER'])\
        + " --deploy-mode %s" % eval(self.config['SPARK']['DEPLOY_MODE'])\
        + " --name %s" % self.bench\
        + " --class QueryRunner"\
        + " --conf spark.hadoop.fs.s3n.awsAccessKeyId=%s" % eval(self.config['AWS']['ACCESS_KEY_ID'])\
        + " --conf spark.hadoop.fs.s3n.awsSecretAccessKey=%s" % eval(self.config['AWS']['SECRET_ACCESS_KEY'])\
        + " --conf spark.hadoop.fs.s3n.impl=%s" % eval(self.config['AWS']['S3_FILE_SYSTEM'])\
        + " --jars %s" % eval(self.config['AWS']['LIBRARIES'])\
        + " --conf spark.imdb.input.path=%s" % eval(self.config['BENCHMARKS']['IMDB_DATASET'])\
        + " --conf spark.tpcds.input.path=%s" % eval(self.config['BENCHMARKS']['TPCDS_DATASET'])\
        + " --conf spark.driver.cores=%s" % eval(self.config['SPARK']['DRIVER_CORES'])\
        + " --conf spark.driver.memory=%s" % eval(self.config['SPARK']['DRIVER_MEMORY'])\
        + " --conf spark.executor.instances=%s" % self.containers\
        + " --conf spark.executor.cores=%s" % eval(self.config['SPARK']['EXECUTOR_CORES'])\
        + " --conf spark.executor.memory=%s" % eval(self.config['SPARK']['EXECUTOR_MEMORY'])\
        + " --conf spark.task.maxFailures=10000"\
        + " --conf spark.kubernetes.container.image=%s" % eval(self.config['SPARK']['DOCKER_IMAGE'])\
        + " --conf spark.kubernetes.authenticate.driver.serviceAccountName=default"\
        + " --conf spark.kubernetes.job.executors=%s" % self.containers\
        + " --conf spark.kubernetes.job.bid=%s" % self.bid\
        + " --conf spark.kubernetes.job.slowdown=%s" % self.slowdown\
        + " --conf spark.kubernetes.job.restarts=%s" % self.restarts\
        + " --conf spark.kubernetes.user.id=%s" % self.jobname\
        + " --conf spark.sql.broadcastTimeout=1200"\
        + " %s" % eval(self.config['SPARK']['JAR'])\
        + " %s" % self.bench\
        + " %s" % self.jobid

    print('Submit %s with id %s <%s, %s>' % (self.jobname, self.jobid, self.containers, self.bid))

    return cmd.split(' ')

