package cluster

import common.EventLoop
import scheduler.{Instance, InstanceId, PodInfo}

import scala.collection.mutable
import scala.collection.mutable.HashMap

class MockLocalNode extends Cluster {

  private val bindings = new HashMap[String, String]

  /**
    * Initialize the local cluster with a single instance
    * configured with 1 cpu and 1 GB.
    */
  override val nodes: List[Instance] = {
    val id = new InstanceId("node0")
    List(new Instance(id, 4, 8.0))
  }

  override def createBinding(id: String, hostname: String): Boolean = {
    bindings(id) = hostname
    true
  }

  override def deleteBinding(id: String): Boolean = {
    bindings.remove(id)
    true
  }

  override def intercept(eventLoop: EventLoop[PodInfo]): Unit = { }

  override def listPods: mutable.HashMap[String, String] = bindings
}
