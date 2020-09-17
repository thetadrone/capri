import time
import random
import math
import subprocess
import configparser

from Experiment.TpcdsWorkload import TpcdsWorkload

EXECUTION_DISABLED = False

random.seed(20)

config = configparser.ConfigParser()
config.read('config.ini')

start = time.time()

workload = TpcdsWorkload(config)

for i in range(0, int(config['TPCDS']['TOTAL_JOBS'])):
  if time.time() - start > int(config['TPCDS']['REGIME_DURATION']):
    start = time.time()
    workload.switch_arrival_rate()
 
  dt = workload.generate_arrival() 
  cmd = workload.prepare_job()

  print(cmd.get_string())
  
  if (EXECUTION_DISABLED == False):
    p = subprocess.Popen(cmd.build())
    print("Sleep %s" % dt)
    time.sleep(max(dt, 1))
  
