package scheduler

import io.kubernetes.client.models.V1Pod

case class PodInfo(pod: V1Pod) extends Ordered[PodInfo] {

  private val ts = System.currentTimeMillis()

  private val requests = pod.getSpec.getContainers.get(0).getResources.getRequests
  private val limits = pod.getSpec.getContainers.get(0).getResources.getRequests

  val cpuRequest = requests.get("cpu").getNumber.intValue()
  val memRequest = requests.get("memory").getNumber.doubleValue() / 1024 / 1024 / 1024
 
  val cpuLimit = limits.get("cpu").getNumber.intValue()
  val memLimit = limits.get("memory").getNumber.doubleValue() / 1024 / 1024 / 1024
  
  val cpu = cpuLimit
  val memory = memLimit
  
  // the name of the pod is a unique identifier in Kubernetes namespace
  // a preempted pod receives a different name when is restarted
  val name = pod.getMetadata.getName
  // Pending, Running, and Succeeded
  val phase = pod.getStatus.getPhase

  // pods are handled by a pluggable scheduler
  val sched = pod.getSpec.getSchedulerName

  val appid = pod.getMetadata.getLabels.get("userId")

  val slowdown = pod.getMetadata.getLabels.get("expectedSlowdown").toDouble
  val restarts = pod.getMetadata.getLabels.get("numRestarts").toInt

  def isDriver: Boolean = pod.getMetadata.getLabels.get("spark-role") == "driver"

  val bid = if (isDriver) {
    pod.getMetadata.getLabels.get("bidValue").toDouble + 0.000001
  } else {
    pod.getMetadata.getLabels.get("bidValue").toDouble
  }

  val children = if (isDriver) {
    // the Spark driver submits requests for executor pods
    pod.getMetadata.getLabels.get("numExecutors").toInt
  } else {
    0
  }

  def podId: String = name.split("-")(1)

  override def compare(that: PodInfo): Int = {
    -this.bid.compareTo(that.bid)
  }
}
