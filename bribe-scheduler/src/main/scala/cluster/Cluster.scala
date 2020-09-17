package cluster

import common.EventLoop
import scheduler.{Instance, PodInfo}

import scala.collection.mutable

abstract class Cluster {

  val nodes: List[Instance]

  def createBinding(id: String, node: String): Boolean

  def deleteBinding(id: String): Boolean

  def intercept(eventLoop: EventLoop[PodInfo]): Unit

  def listPods: mutable.HashMap[String, String]
}
