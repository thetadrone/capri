#!/bin/bash

for i in `seq 1 100`; do
 echo "Try $i"
 kubectl delete --all pods
 #sleep 1
done

