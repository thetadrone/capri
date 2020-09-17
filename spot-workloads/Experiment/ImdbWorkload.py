import configparser
import math
import random
import time

from Experiment.JobConfiguration import JobConfiguration

class ImdbWorkload:

  def __init__(self, config):
    self.config = config
    self.setup_job_bins()
    self.interarrival = int(self.config['IMDB']['INTERARRIVAL_TIME_HIGH'])
    self.jobid = 0

  def setup_job_bins(self):
    self.jobs = dict([('tiny', []), ('small', []), ('medium', []), ('large', [])])      

    fin = open(eval(self.config['BENCHMARKS']['IMDB_QUERIES']), "r")

    for line in fin.readlines():
      name, size = line.split(" ")
      duration = int(size)
      if duration < int(self.config['IMDB']['TINY_JOBS_CUTOFF']):
        self.jobs['tiny'].append((name, duration))
      elif duration < int(self.config['IMDB']['SMALL_JOBS_CUTOFF']):
        self.jobs['small'].append((name, duration))
      elif duration < int(self.config['IMDB']['MEDIUM_JOBS_CUTOFF']):
        self.jobs['medium'].append((name, duration))
      else:
        self.jobs['large'].append((name, duration))

  def generate_bid(self):
    p = 0.1
    b = [round(x * 0.1, 2)  for x in range(0, 11)]
    g = [p*math.pow((1-p), k) for k in range(0, 10)]
    v = [round(g[i] / sum(g), 2) for i in range(0, len(g))]
    cumul = 0

    for ind, x in enumerate(v):
      cumul += x
      v[ind] = cumul

    v.append(1)

    s = random.random()
    for ind in range(1, len(v)):
      if s < v[ind]:
        y1, y2 = b[ind-1], b[ind]
        return round((y2 - y1) * random.random() + y1, 2)    

    raise ValueError('Unable to generate a bid')

  def generate_arrival(self):
    u = random.random()
    lnu = math.log(u)
    waittime = -lnu * self.interarrival
    return waittime

  def switch_arrival_rate(self):
    if self.interarrival == int(self.config['IMDB']['INTERARRIVAL_TIME_HIGH']):
      self.interarrival == int(self.config['IMDB']['INTERARRIVAL_TIME_LOW'])
    elif self.interarrival == int(self.config['IMDB']['INTERARRIVAL_TIME_LOW']):
      self.interarrival = int(self.config['IMDB']['INTERARRIVAL_TIME_HIGH'])
    else:
      raise ValueError('Unknown interarrival time') 

  def sample_jobs(self):
    f = random.random()
    if f < float(self.config['IMDB']['TINY_JOBS_FRACTION']):
      index = random.randint(0, len(self.jobs['tiny'])-1)
      return self.jobs['tiny'][index]
    elif f >= float(self.config['IMDB']['TINY_JOBS_FRACTION']) and \
        f < float(self.config['IMDB']['SMALL_JOBS_FRACTION']):
      index = random.randint(0, len(self.jobs['small'])-1)
      return self.jobs['small'][index]  
    elif f >= float(self.config['IMDB']['SMALL_JOBS_FRACTION']) and \
        f < float(self.config['IMDB']['MEDIUM_JOBS_FRACTION']):
      index = random.randint(0, len(self.jobs['medium'])-1)
      return self.jobs['medium'][index]
    else:
      index = random.randint(0, len(self.jobs['large'])-1)
      return self.jobs['large'][index]

  def prepare_job(self):
    bid = self.generate_bid()
    (name, size) = self.sample_jobs() 
    _, index = name.split("-", 1)
    jobname = name + "-" + str(self.jobid) 
    containers = int(self.config['IMDB']['CONTAINERS_PER_JOB'])
    restarts = int(self.config['IMDB']['RESTARTS_PER_JOB'])

    jobconf = JobConfiguration(self.config, "imdb", jobname, index, size, containers, bid, 1.0, restarts)
    self.jobid += 1
    return jobconf

