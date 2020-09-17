package scheduler

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Instance(id: InstanceId, cpu: Int, mem: Double) {

  // resource counters
  var usedMemoryGB: Double = 0.0
  var usedCpuCores: Int = 0

  private var pods: ArrayBuffer[String] = new ArrayBuffer[String]
  var podToUser = new mutable.HashMap[String, String]
  var podToMem = new mutable.HashMap[String, Double]
  var podToCores = new mutable.HashMap[String, Int]

  def getFreeMemoryGB(): Double = mem - usedMemoryGB

  def getFreeCores(): Int = cpu - usedCpuCores

  def getAllPods(): Array[String] = pods.toArray

  def placePod(name: String, user: String, cpu: Int, mem: Double): Unit = {
    if (getFreeCores() < cpu) {
      throw new IllegalStateException("Not enough free cores to schedule pod")
    }
    if (getFreeMemoryGB() < mem) {
      throw new IllegalStateException("Not enough free memory to schedule pod")
    }
    usedCpuCores += cpu
    usedMemoryGB += mem
    pods.append(name)
    podToMem(name) = mem
    podToCores(name) = cpu
    podToUser(name) = user
    // println("[Node] Placed pod " + name + " from user " + user + " on " + id.getId)
  }

  def unplacePod(name: String): Unit = {
    if (!pods.contains(name)) {
      throw new IllegalArgumentException("No pod with id " + name + " scheduled on this node.")
    }

    val index = pods.indexOf(name)
    pods.remove(index)
    val mem = podToMem.remove(name)
    usedMemoryGB -= mem.get
    val cpu = podToCores.remove(name)
    usedCpuCores -= cpu.get
    val user = podToUser(name)
    podToUser.remove(name)
    // println("[Node] Unplaced pod " + name + " from user " + user + " from " + id.getId)
  }

  def getName(): String = id.getId

  def unplacePodsByUser(userName: String): Unit = {
    var selectedPods = new ArrayBuffer[String]
    podToUser.foreach {
      case (name: String, user: String) =>
        if (user == userName) selectedPods.append(name)
    }
    selectedPods.foreach { name =>
      unplacePod(name)
    }
  }
}
