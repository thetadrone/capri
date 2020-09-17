import time
import random
import math
import subprocess
import configparser

from Experiment.JobConfiguration import JobConfiguration


config = configparser.ConfigParser()
config.read('config.ini')

for i in range(0, int(config['BENCHMARKS']['TPCDS_SIZE'])):
  cmd = JobConfiguration(config, "job-%s" % i, i, 5, 1.0, 1.0, 1)
  p = subprocess.Popen(cmd.build())
  p.wait()
  
