package scheduler

import cluster.{MockLocalNode, MockDistributedCluster, Cluster}
import common.{PodBuilder, Clock, DummyClock}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.mutable.PriorityQueue

@RunWith(classOf[JUnitRunner])
class QueueManagerSuite extends FunSuite with BeforeAndAfter {

  private val clock: Clock = new DummyClock()

  private val client: Cluster = new MockDistributedCluster(3)

  private val local: Cluster = new MockLocalNode

  test("multiple application drivers complete without preemption") {
    val queue = new SpotManager(client, false, clock)

    val fooDriver = new PodInfo(PodBuilder.buildPod("foo", "driver-foo", 0.5, "driver", 4, 8.0))
    val barDriver = new PodInfo(PodBuilder.buildPod("bar", "driver-bar", 0.5, "driver", 4, 8.0))
    val zooDriver = new PodInfo(PodBuilder.buildPod("zoo", "driver-zoo", 0.5, "driver", 4, 8.0))

    queue.submitPod(fooDriver)
    queue.submitPod(barDriver)
    queue.submitPod(zooDriver)

    queue.completePod(zooDriver)
    queue.completePod(barDriver)
    queue.completePod(fooDriver)

    assert(queue.getTotalWaitTime(fooDriver.appid) == 100 &&
          queue.getTotalWaitTime(barDriver.appid) == 100 &&
          queue.getTotalWaitTime(zooDriver.appid) == 100)

    assert(queue.getTotalRunTime(zooDriver.appid) == 100 &&
          queue.getTotalRunTime(barDriver.appid) == 400 &&
          queue.getTotalRunTime(fooDriver.appid) == 700)

    assert(queue.getWastedServiceTime(zooDriver.appid) == 0)
    assert(queue.getServiceTime(fooDriver.appid) == 700)
  }

  test("single-application pods completes without preemption") {
    val queue = new SpotManager(client, false, clock)

    val fooDriver = new PodInfo(PodBuilder.buildPod("foo", "foo-driver", 0.5, "driver", 4, 8.0))
    val fooExec = new PodInfo(PodBuilder.buildPod("foo", "foo-exec", 0.5, "executor", 4, 8.0))

    queue.submitPod(fooDriver)
    queue.submitPod(fooExec)

    queue.completePod(fooDriver)

    assert(queue.getTotalWaitTime(fooDriver.appid) == 100)
    assert(queue.getTotalRunTime(fooDriver.appid) == 300)
    assert(queue.getRuntime(fooDriver.appid) == 300)
  }

  test("sequence of drivers with preemption") {
    val queue = new SpotManager(local, false, clock)

    val fooDriver0 = new PodInfo(PodBuilder.buildPod("foo", "foo-driver-0", 0.5, "driver", 4, 8.0))
    val fooDriver1 = new PodInfo(PodBuilder.buildPod("foo", "foo-driver-1", 0.5, "driver", 4, 8.0))
    val barDriver = new PodInfo(PodBuilder.buildPod("bar", "bar-driver", 0.7, "driver", 4, 8.0))

    queue.submitPod(fooDriver0)
    assert(local.listPods.keySet == Set("foo-driver-0"))

    queue.submitPod(barDriver)
    // simulate the job re-submissions
    queue.submitPod(fooDriver1)
    assert(local.listPods.keySet == Set("bar-driver"))

    queue.completePod(barDriver)
    assert(local.listPods.keySet == Set("foo-driver-1"))

    queue.completePod(fooDriver1)
    assert(local.listPods.keySet == Set.empty)
  }

  test("application pods completes after preemption") {
    val queue = new SpotManager(client, false, clock)

    val fooDriver = new PodInfo(PodBuilder.buildPod("foo", "foo-driver", 0.5, "driver", 4, 8.0))
    val fooExec = new PodInfo(PodBuilder.buildPod("foo", "foo-exec", 0.5, "executor", 4, 8.0))

    queue.submitPod(fooDriver)
    queue.submitPod(fooExec)
    assert(client.listPods.keySet == Set("foo-driver", "foo-exec"))

    val barDriver = new PodInfo(PodBuilder.buildPod("bar", "bar-driver", 0.7, "driver", 4, 8.0))
    val barExec = new PodInfo(PodBuilder.buildPod("bar", "bar-exec", 0.7, "executor", 4, 8.0))

    queue.submitPod(barDriver)
    assert(client.listPods.keySet == Set("foo-driver", "foo-exec", "bar-driver"))

    queue.submitPod(barExec)
    assert(client.listPods.keySet == Set("foo-driver", "bar-driver", "bar-exec"))

    queue.submitPod(fooExec)
    assert(client.listPods.keySet == Set("bar-driver", "bar-exec", "foo-driver"))

    queue.completePod(barDriver)
    assert(client.listPods.keySet == Set("foo-driver", "foo-exec"))

    val zooDriver = new PodInfo(PodBuilder.buildPod("zoo", "zoo-driver", 0.75, "driver", 4, 8.0))
    val zooExec0 = new PodInfo(PodBuilder.buildPod("zoo", "zoo-exec-0", 0.75, "executor", 4, 8.0))
    val zooExec1 = new PodInfo(PodBuilder.buildPod("zoo", "zoo-exec-1", 0.75, "executor", 4, 8.0))

    queue.submitPod(zooDriver)
    assert(client.listPods.keySet == Set("zoo-driver", "foo-exec", "foo-driver"))
    queue.submitPod(zooExec0)
    assert(client.listPods.keySet == Set("zoo-driver", "zoo-exec-0", "foo-driver"))
    queue.submitPod(zooExec1)
    assert(client.listPods.keySet == Set("zoo-driver", "zoo-exec-0", "zoo-exec-1"))
  }

  test("driver is preempted by the executer of a higher bid job") {
    val client: Cluster = new MockDistributedCluster(2)
    val queue = new SpotManager(client, false, clock)

    val fooDriver = new PodInfo(PodBuilder.buildPod("foo", "foo-driver", 0.5, "driver", 4, 8.0))
    val barDriver = new PodInfo(PodBuilder.buildPod("bar", "bar-driver", 0.8, "driver", 4, 8.0))
    queue.submitPod(fooDriver)
    queue.submitPod(barDriver)

    assert(client.listPods.keySet == Set("foo-driver", "bar-driver"))
    val fooExec = new PodInfo(PodBuilder.buildPod("foo", "foo-exec", 0.5, "executor", 4, 8.0))
    val barExec = new PodInfo(PodBuilder.buildPod("bar", "bar-exec", 0.8, "executor", 4, 8.0))

    queue.submitPod(fooExec)
    assert(client.listPods.keySet == Set("foo-driver", "bar-driver"))
    queue.submitPod(barExec)
    assert(client.listPods.keySet == Set("bar-exec", "bar-driver"))
  }
}
