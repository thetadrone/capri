package scheduler

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.mutable.{ArrayBuffer, HashSet}
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class TestSuite extends FunSuite with BeforeAndAfter {
  /*
  test("single-core node with increasing bids") {
    val nodes = new HashSet[Instance]
    nodes.add(new Instance(new InstanceId("inst-0"), 1, 1.0))

    val client = new DummyPodClient()
    val sched = new BribeScheduler(client, nodes.toSeq)

    val pod0 = new SparkPod("bob", "pi-0", 0.1, 1.0, 3, 1, 1.0, 0, false, 0, true)
    val pod1 = new SparkPod("alice", "pi-1", 0.2, 1.0, 3, 1, 1.0, 0, false, 0, true)
    val pod2 = new SparkPod("tom", "pi-2", 0.3, 1.0, 3, 1, 1.0, 0, false, 0, true)
    sched.submitPod(pod0)
    assert(client.bindings === Map("pi-0" -> "inst-0"))
    sched.submitPod(pod1)
    assert(client.bindings === Map("pi-1" -> "inst-0"))
    sched.submitPod(pod2)
    assert(client.bindings === Map("pi-2" -> "inst-0"))
    sched.removePod(pod2)
    sched.submitPod(pod1)
    assert(client.bindings === Map("pi-1" -> "inst-0"))
    sched.removePod(pod1)
    sched.submitPod(pod0)
    assert(client.bindings === Map("pi-0" -> "inst-0"))
    sched.removePod(pod0)
    assert(client.bindings === Map())
  }

  test(testName = "single-core node with decreasing bids") {
    val nodes = new HashSet[Instance]
    nodes.add(new Instance(new InstanceId("inst-0"), 1, 1.0))

    val client = new DummyPodClient()
    val sched = new BribeScheduler(client, nodes.toSeq)
    val podList = new ArrayBuffer[SparkPod]()
    (9 to 0 by -1).foreach { i =>
      val pod = new SparkPod("bob", "pi-" + i, i.toDouble / 10, 1.0, 3, 1, 1.0, 0, false, 0, true)
      podList.append(pod)
      sched.submitPod(pod)
      assert(client.bindings === Map("pi-9" -> "inst-0"))
    }
    val revList = podList.reverse
    (9 to 1 by -1).foreach { i =>
      sched.removePod(revList(i))
      sched.submitPod(revList(i-1))
      assert(client.bindings === Map("pi-" + (i-1) -> "inst-0"))
    }
  }

  test("single-core node with random bids") {
    val nodes = new HashSet[Instance]
    nodes.add(new Instance(new InstanceId("inst-0"), 1, 1.0))
    val client = new DummyPodClient()
    val sched = new BribeScheduler(client, nodes.toSeq)
    val rand = new Random(100)
    val buffer = new ArrayBuffer[Int]
    (0 to 30).foreach { i => buffer.append(rand.nextInt(10)) }

    var pos = 0
    buffer.foreach { i =>
      val bid = i.toDouble / 10
      val pod = new SparkPod("bob", "pi-" + i, bid, 1.0, 3, 1, 1.0, 0, false, 0, true)
      sched.submitPod(pod)
      val max = buffer.slice(0, pos + 1).max
      assert(client.bindings === Map("pi-" + max -> "inst-0"))
      pos += 1
    }
  }

  test("single-core with drivers and executors") {
    val nodes = new HashSet[Instance]
    nodes.add(new Instance(new InstanceId("inst-0"), 1, 1.0))

    val client = new DummyPodClient()
    val sched = new BribeScheduler(client, nodes.toSeq)

    // pods of the first application
    sched.submitPod(new SparkPod("bob", "pi-0", 0.2, 1.0, 3, 1, 1.0, 0, true, 0, true))
    sched.submitPod(new SparkPod("bob", "pi-1", 0.2, 1.0, 3, 1, 1.0, 0, false, 0, true))
    sched.submitPod(new SparkPod("bob", "pi-2", 0.2, 1.0, 3, 1, 1.0, 0, false, 0, true))

    // pods of the second application
    sched.submitPod(new SparkPod("bob", "rd-0", 0.6, 1.0, 3, 1, 1.0, 0, true, 0, true))
    sched.submitPod(new SparkPod("bob", "rd-1", 0.6, 1.0, 3, 1, 1.0, 0, false, 0, true))
    sched.submitPod(new SparkPod("bob", "rd-2", 0.6, 1.0, 3, 1, 1.0, 0, false, 0, true))

    assert(client.bindings === Map("rd-0" -> "inst-0"))
  }

  test("multi-core node with increasing bids") {
    val nodes = new HashSet[Instance]
    nodes.add(new Instance(new InstanceId("inst-0"), 4, 4.0))
    val client = new DummyPodClient()
    val sched = new BribeScheduler(client, nodes.toSeq)
    val rand = new Random(100)
    val buffer = new ArrayBuffer[Int]
    (0 to 30).foreach { i => buffer.append(rand.nextInt(90)) }

    var i = 0
    buffer.toArray.sortWith(_ < _).foreach { x =>
      val bid = x.toDouble / 100
      sched.submitPod(new SparkPod("bob", "pi-" + i, bid, 1.0, 3, 1, 1.0, 0, false, 0, true))
      i += 1
    }

    sched.submitPod(new SparkPod("bob", "km-0", 0.9, 1.0, 3, 1, 1.0, 0, false, 0, true))
    sched.submitPod(new SparkPod("bob", "km-1", 0.9, 1.0, 3, 1, 1.0, 0, false, 0, true))
    sched.submitPod(new SparkPod("bob", "km-2", 0.9, 1.0, 3, 1, 1.0, 0, false, 0, true))
    sched.submitPod(new SparkPod("bob", "km-3", 0.9, 1.0, 3, 1, 1.0, 0, false, 0, true))

    assert(client.bindings.keySet.toArray.sorted === Array("km-0", "km-1", "km-2", "km-3"))
  }

  test("multi-core node with decreasing bids") {
    val nodes = new HashSet[Instance]
    nodes.add(new Instance(new InstanceId("inst-0"), 4, 4.0))
    val client = new DummyPodClient()
    val sched = new BribeScheduler(client, nodes.toSeq)
    val rand = new Random(100)
    val buffer = new ArrayBuffer[Int]
    (0 to 30).foreach { i => buffer.append(rand.nextInt(90)) }

    sched.submitPod(new SparkPod("bob", "km-0", 0.9, 1.0, 3, 1, 1.0, 0, false, 0, true))
    sched.submitPod(new SparkPod("bob", "km-1", 0.9, 1.0, 3, 1, 1.0, 0, false, 0, true))
    sched.submitPod(new SparkPod("bob", "km-2", 0.9, 1.0, 3, 1, 1.0, 0, false, 0, true))
    sched.submitPod(new SparkPod("bob", "km-3", 0.9, 1.0, 3, 1, 1.0, 0, false, 0, true))

    var i = 0
    buffer.toArray.sortWith(_ < _).reverse.foreach { x =>
      val bid = x.toDouble / 100
      sched.submitPod(new SparkPod("bob", "pi-" + i, bid, 1.0, 3, 1, 1.0, 0, false, 0, true))
      i += 1
    }

    assert(client.bindings.keySet.toArray.sorted === Array("km-0", "km-1", "km-2", "km-3"))
  }

  test("scheduling pods from multiple threads") {
    val nodes = new HashSet[Instance]
    nodes.add(new Instance(new InstanceId("inst-0"), 1, 1.0))
    val rand = new Random(100)
    val userBids = new ArrayBuffer[(String, Double)]
    val threads = new ArrayBuffer[Thread]
    val client = new DummyPodClient()
    val sched = new BribeScheduler(client, nodes.toSeq)
    for (i <- 0 until 100) {
      val t = new Thread {
        override def run {
          val bid = rand.nextInt(100).toDouble/100
          this.synchronized {
            userBids.append(("km-" + i, bid))
          }
          println("km-" + i + " " + bid)
          sched.submitPod(new SparkPod("bob", "km-" + i, bid, 1.0, 3, 1, 1.0, 0, false, 0, true))
        }
      }
      threads.append(t)
      t.start
      Thread.sleep(rand.nextInt(100)) // slow the loop down a bit
    }

    for(i <- 0 until 100) {
      threads(i).join()
    }

    println("Max " + userBids.maxBy(_._2)._1)
    assert(client.bindings === Map(userBids.maxBy(_._2)._1 -> "inst-0"))
  }
  */
}
