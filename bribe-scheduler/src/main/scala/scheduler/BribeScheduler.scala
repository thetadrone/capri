package scheduler

import cluster.Cluster
import common.Clock

import scala.collection.JavaConverters._
import scala.collection.mutable.HashMap

import java.util.concurrent.ConcurrentHashMap

/**
  *  Implementation of the bribe scheduling policy that prioritizes pods
  *  based on their bid value.
  */
class BribeScheduler(
    client: Cluster,
    manager: SpotManager,
    clock: Clock) extends SchedulingPolicy {

  private object Locker

  // the set of cluster machines available for allocating pods
  private lazy val instances: List[Instance] = client.nodes

  // the instance where a given pod runs
  private val podToInstance = new HashMap[String, Instance]
  private val nameToPod = new ConcurrentHashMap[String, PodInfo]

  private val appDelay = new HashMap[String, Long]

  def runningPods: Array[PodInfo] = {
    nameToPod.values().asScala.toArray[PodInfo]
  }

  val thread = new Thread {
    override def run {
      var start = System.currentTimeMillis()
      while (true) {
        val crt = System.currentTimeMillis()
        if (crt - start >= 5000) {
          val minPod = if (nameToPod.values().asScala.nonEmpty) {
            nameToPod.values().asScala.minBy(_.bid)
          } else {
            null
          }
          val minBid = if (minPod == null) 0.0 else minPod.bid
          val freeCpu = client.nodes.map(p => p.getFreeCores).sum
          val freeMem = client.nodes.map(p => p.getFreeMemoryGB).sum
          println("ClusterState " + System.currentTimeMillis() + " " +
            minBid + " " +
            freeCpu + " " +
            freeMem + " ")
          start = crt
        }
      }
    }
  }.start

  /**
    * Allocates a given pod to an instance if there are enough resources
    * available or its bid is higher than the current minimum bid across
    * all running pods.
    */
  override def allocate(newPod: PodInfo): Boolean = Locker.synchronized {
    if (appDelay.contains(newPod.appid)) {
      val delay = System.currentTimeMillis() - appDelay(newPod.appid) >= 10000
      if (!delay) {
        return false
      }
    }
    
    val selectedNode = instances.find { n =>
      n.getFreeCores() >= newPod.cpu && n.getFreeMemoryGB() >= newPod.memory
    }
    if (selectedNode.isDefined) {
      // enough idle slots, so allocate the pod
      val isPlaced = client.createBinding(newPod.name, selectedNode.get.getName())

      if (isPlaced) {
        // println(s"Placing pod ${newPod.name} " + f"${newPod.bid}%1.6f " + s"on instance ${selectedNode.get.getName()}")
        selectedNode.get.placePod(newPod.name, newPod.appid, newPod.cpu, newPod.memory)
        podToInstance(newPod.name) = selectedNode.get
        nameToPod.put(newPod.name, newPod)

        val startPod = SparkListenerPodStart(newPod.appid,
          newPod.name, newPod.bid, newPod.slowdown,
          newPod.isDriver, clock.getTimeMillis())
        manager.postEvent(newPod.appid, startPod)
      }
      true
    } else {
      val minPod = nameToPod.values().asScala.minBy(_.bid)
      val node = podToInstance(minPod.name)
      if (minPod.bid < newPod.bid) {
        // preempt the lowest bid pod and allocate the new pod
        // println(s"Preempt pod ${minPod.appid} ${minPod.name} ${minPod.bid} from instance ${node.getName()}")
        node.unplacePod(minPod.name)
        podToInstance.remove(minPod.name)
        nameToPod.remove(minPod.name)
        client.deleteBinding(minPod.name)
        // assumes that the client application will re-submit the killed pods
        val preemptPod = SparkListenerPodPreempt(minPod.appid,
            minPod.name, minPod.bid, minPod.slowdown,
            minPod.isDriver, clock.getTimeMillis())

        appDelay(minPod.appid) = System.currentTimeMillis()
        manager.postEvent(minPod.appid, preemptPod)
        if (minPod.isDriver) {
          removeChildren(minPod, true)
        }
        val ret = allocate(newPod)
        ret
      } else {
        // new pod has a lower bid than the running pods
        false
      }
    }
  }

  override def deallocate(pod: PodInfo): Boolean = Locker.synchronized {
    val node = podToInstance(pod.name)
    node.unplacePod(pod.name)
    podToInstance.remove(pod.name)
    nameToPod.remove(pod.name)
    client.deleteBinding(pod.name)

    if (pod.isDriver) {
      removeChildren(pod)
    }
    val completePod = SparkListenerPodComplete(pod.appid,
      pod.name, pod.bid, pod.slowdown,
      pod.isDriver, clock.getTimeMillis())
    manager.postEvent(pod.appid, completePod)

    true
  }

   private def removeChildren(pod: PodInfo, driverPreempted: Boolean = false): Unit = {
     val executors = nameToPod.asScala.filter {
       case (_, v) if v.appid == pod.appid && !v.isDriver => true
       case _ => false
     }

     executors.keys.foreach { name =>
       val n = podToInstance(name)
       n.unplacePod(name)
       podToInstance.remove(name)
       nameToPod.remove(name)
       client.deleteBinding(name)
       val removeExecutor = if (driverPreempted) {
        SparkListenerPodPreempt(pod.appid, name, 0.0f, 0.0f, false, clock.getTimeMillis())
       } else {
        SparkListenerPodComplete(pod.appid, name, 0.0f, 0.0f, false, clock.getTimeMillis())
       }
       manager.postEvent(pod.appid, removeExecutor)
     }
  }
}
