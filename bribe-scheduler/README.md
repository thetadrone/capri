# Bribe Scheduler #

The Bribe Scheduler is a Kubernetes scheduler that implements a priority 
based scheduling discipline based on the pod bid values and uses the runtime 
performance of previous jobs to build a bid advisor.


### Setting up the Kubernetes cluster ###

Install [Java 8](https://askubuntu.com/questions/764849/how-can-i-install-jdk-8u91-linux-x64-tar-gz-on-ubuntu):
```bash
sudo mkdir /usr/java
sudo tar xvzf  jdk-8u191-linux-x64.tar.gz -C /usr/java
JAVA_HOME=/usr/java/jdk1.8.0_191/
sudo update-alternatives --install /usr/bin/java java ${JAVA_HOME%*/}/bin/java 20000
sudo update-alternatives --install /usr/bin/javac javac ${JAVA_HOME%*/}/bin/javac 20000
update-alternatives --config java
java -version
```

Install Docker, sbt, git, python3:
```bash
sudo apt-get update
sudo apt-get install -y docker.io
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
sudo apt install python3-pip (optional)
pip3 install numpy (optional)
```

Install the Kubernetes master node (see [instructions](https://kubernetes.io/docs/setup/independent/install-kubeadm/)):
```bash
sudo apt-get update && sudo apt-get install -y apt-transport-https curl
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
sudo su
cat <<EOF >/etc/apt/sources.list.d/kubernetes.list
deb https://apt.kubernetes.io/ kubernetes-xenial main
EOF
sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl
```

Install a virtual network overlay (see [instructions](https://docs.projectcalico.org/v3.2/getting-started/kubernetes/)):
```bash
sudo kubeadm init --pod-network-cidr=192.168.0.0/16
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
kubectl apply -f https://docs.projectcalico.org/v3.2/getting-started/kubernetes/installation/hosted/etcd.yaml
kubectl apply -f https://docs.projectcalico.org/v3.2/getting-started/kubernetes/installation/rbac.yaml
kubectl apply -f https://docs.projectcalico.org/v3.2/getting-started/kubernetes/installation/hosted/calico.yaml
kubectl taint nodes --all node-role.kubernetes.io/master-
kubectl create clusterrolebinding role --clusterrole=admin --serviceaccount=default:default --namespace=default
kubectl get nodes -o wide
```

Decommision a Kubernetes node:
```bash
kubectl drain <node> --delete-local-data --force --ignore-daemonsets
kubectl delete node <node>
kubeadm reset
```

