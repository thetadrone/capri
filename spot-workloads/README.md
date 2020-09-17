**This repository provides a simple tool for running experimental workloads on the Capri spot market**

---

## Prerequisites
Running this tool assumes the following:

1. A Kubernetes cluster up and running.
2. A Docker image with the Spark binaries installed on each node of the cluster.
3. A workload trace with the size (total CPU time) of each job placed under the **Traces** directory.
4. A dataset stored in S3.

## Setting up the experiment configuration

You first need to configure various parameters inside the **config.ini** file. The file consists of multiple sections:

1. SPARK framework: these parameters refer to each Spark instance that is deployed in the Kubernetes cluster.
2. AWS: these parameters are set for accessing the datasets stored in S3.
3. Kubernetes: address of the master node and the Spark image identifier.
4. Workload: the jar, dataset, and path to the dataset queries.
5. Experiment: arrival process, bid distribution, query size distribution, regime type. 

## Collecting a workload trace

To collect a workload trace you need to run the workload in sequential mode one query or job at a time in the idle cluster.

1. Configure the workload parameters.
2. Execute the **collect_trace.py** script.

---

