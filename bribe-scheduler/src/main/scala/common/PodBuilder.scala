package common

import java.util

import io.kubernetes.client.models._
import io.kubernetes.client.custom.Quantity

object PodBuilder {

  def buildPod(podName: String, bidValue: Double, cpu: Int, memory: Double): V1Pod = {
    buildPod("app", podName, bidValue, 1.0, 3, "Pending", "executor", 0, cpu, memory)
  }

  def buildPod(appid: String, podName: String, bidValue: Double,
      role: String, cpu: Int, memory: Double): V1Pod = {
    buildPod(appid, podName, bidValue, 1.0, 3, "Pending", role, 0, cpu, memory)
  }

  def buildPod(
      userId: String,
      podName: String,
      bidValue: Double,
      expectedSlowdown: Double,
      numRestarts: Int,
      phase: String,
      role: String,
      children: Int,
      cpu: Int,
      memory: Double): V1Pod = {

    // set the pod resource requirements
    val spec: V1PodSpec = new V1PodSpec()
    spec.setSchedulerName("BribeScheduler")
    val containers = new util.LinkedList[V1Container]()
    val cont = new V1Container()
    val resources = new V1ResourceRequirements()

    val request = new util.HashMap[String, Quantity]
    val cpuQuant = new Quantity(s"${cpu}")
    request.put("cpu", cpuQuant)
    val memQuant = new Quantity(s"${memory * 1024 * 1024 * 1024}")
    request.put("memory", memQuant)

    resources.setRequests(request)
    resources.setLimits(request)
    cont.setResources(resources)
    containers.add(cont)
    spec.setContainers(containers)

    val metadata = new V1ObjectMeta()
    metadata.setName(podName)
    val status = new V1PodStatus()
    status.setPhase(phase)

    // set the scheduling information
    val labels = new util.HashMap[String, String]
    labels.put("userId", userId)
    labels.put("bidValue", bidValue.toString)
    labels.put("expectedSlowdown", expectedSlowdown.toString)
    labels.put("numRestarts", numRestarts.toString)
    labels.put("spark-role", role)
    labels.put("numExecutors", children.toString)
    metadata.setLabels(labels)

    val newPod = new V1Pod()
    newPod.setApiVersion("v1")
    newPod.setKind("job")
    newPod.setMetadata(metadata)
    newPod.setSpec(spec)
    newPod.setStatus(status)

    return newPod
  }
}
