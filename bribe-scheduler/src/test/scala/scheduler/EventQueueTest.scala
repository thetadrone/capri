package scheduler

import java.util

import cluster.MockLocalNode
import common.PodBuilder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.mutable.ArrayBuffer
import io.kubernetes.client.models._
import io.kubernetes.client.custom.Quantity

@RunWith(classOf[JUnitRunner])
class EventQueueTest extends FunSuite with BeforeAndAfter {

  def submitApp(n: Int, userId: String, bid: Double): ArrayBuffer[V1Pod] = {
    val app = new ArrayBuffer[V1Pod]
    (0 to n-1).foreach { idx =>
      if (idx == 0) {
        app += PodBuilder.buildPod(userId, userId + "-" + idx, bid, 1, 1,
          "Pending", "driver", n-1, 1, 1)
      } else {
        app += PodBuilder.buildPod(userId, userId + "-" + idx, bid, 1, 1,
          "Pending", "exec", 0, 1, 1)
      }
    }
    app
  }

  test("basic") {
    val listener = new PodListener(new MockLocalNode())

    listener.eventLoop.start()
    val app = submitApp(3, "user1", 0.5)
    app.foreach { p => listener.eventLoop.post(p) }
  }
}
