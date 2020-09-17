package cluster

import common.EventLoop
import scheduler.{Instance, InstanceId, PodInfo}

import scala.collection.mutable
import scala.collection.mutable.HashMap

class MockDistributedCluster(n: Int) extends Cluster {
  // pod assignments to nodes
  private val bindings = new HashMap[String, String]

  override val nodes: List[Instance] = {
    (1 to n).map { i =>
      val id = new InstanceId(s"node$i")
      new Instance(id, 4, 8.0)
    }.toList
  }

  override def deleteBinding(id: String): Boolean = {
    bindings.remove(id)
    true
  }

  override def intercept(eventLoop: EventLoop[PodInfo]): Unit = { }

  override def createBinding(id: String, node: String): Boolean = {
    bindings(id) = node
    true
  }

  override def listPods: mutable.HashMap[String, String] = bindings
}
