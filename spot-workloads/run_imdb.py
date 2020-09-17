import time
import random
import math
import subprocess
import configparser

from Experiment.ImdbWorkload import ImdbWorkload

EXECUTION_DISABLED = False

random.seed(10)

config = configparser.ConfigParser()
config.read('config.ini')

start = time.time()

workload = ImdbWorkload(config)

for i in range(0, int(config['IMDB']['TOTAL_JOBS'])):
  if time.time() - start > 20 * 60:
    start = time.time()
    workload.switch_arrival_rate()
 
  dt = workload.generate_arrival() 
  cmd = workload.prepare_job()

  print(cmd.get_string())
  
  if (EXECUTION_DISABLED == False):
    p = subprocess.Popen(cmd.build())
    print("Sleep %s" % dt)
    time.sleep(max(dt, 1))
  
