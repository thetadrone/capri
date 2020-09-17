package scheduler

import org.scalatest.{BeforeAndAfter, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import scala.collection.mutable.ArrayBuffer

@RunWith(classOf[JUnitRunner])
class NodeSuite extends FunSuite with BeforeAndAfter{

  var node: Instance = _

  before {
    node = new Instance(new InstanceId("mars"), 8, 15)
  }

  def isNodeIdle(): Unit = {
    assert(node.getFreeCores() == 8 && node.getFreeMemoryGB() == 15)
  }

  test("node resource configuration") {
    isNodeIdle()
  }

  test("allocate a pod on a node") {
    node.placePod("rover-0", "bob", 1, 4)
    assert(node.getFreeCores() == 7 && node.getFreeMemoryGB() == 11)
    node.unplacePod("rover-0")
    isNodeIdle()
  }

  test("allocate and deallocate all sequentially") {
    var pods: ArrayBuffer[String] = new ArrayBuffer[String]()
    (1 to 5).foreach { i =>
      pods.append("rover-" + i)
      node.placePod("rover-" + i, "bob", 1, 2)
    }
    assert(node.getFreeCores() == 3 && node.getFreeMemoryGB() == 5)

    pods.foreach { p => node.unplacePod(p) }
    isNodeIdle()
  }

  test("random allocate and deallocate") {
    var pods: ArrayBuffer[String] = new ArrayBuffer[String]()
    (1 to 3).foreach { i =>
      val id = "rover-" + i
      pods.append(id)
      node.placePod(id, "bob", 1, 5)
    }
    assert(node.getFreeCores() == 5 && node.getFreeMemoryGB() == 0)
    node.unplacePod(pods(0))
    assert(node.getFreeCores() == 6 && node.getFreeMemoryGB() == 5)
    node.unplacePod(pods(1))
    assert(node.getFreeCores() == 7 && node.getFreeMemoryGB() == 10)

    (1 to 3).foreach { i =>
      val id = "mars-" + i
      pods.append(id)
      node.placePod(id, "bob", 1, 1)
    }

    assert(node.getFreeCores() == 4 && node.getFreeMemoryGB() == 7)
    node.unplacePod(pods(2))
    assert(node.getFreeCores() == 5 && node.getFreeMemoryGB() == 12)
    node.unplacePod(pods(3))
    assert(node.getFreeCores() == 6 && node.getFreeMemoryGB() == 13)
    node.unplacePod(pods(4))
    assert(node.getFreeCores() == 7 && node.getFreeMemoryGB() == 14)
    node.unplacePod(pods(5))
    assert(node.getFreeCores() == 8 && node.getFreeMemoryGB() == 15)
  }
}
