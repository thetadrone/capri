package scheduler

import cluster.{MockDistributedCluster, MockLocalNode}
import common.{Clock, SystemClock, PodBuilder}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

@RunWith(classOf[JUnitRunner])
class BribeSchedulerSuite extends FunSuite with BeforeAndAfter {
  private val clock: Clock = new SystemClock()

  test("pod allocation with and without preemption ") {
    val client = new MockLocalNode()
    val sched = new BribeScheduler(client, new SpotManager(client, false, clock), clock)

    // allocation successfull on an idle slot
    val pod0 = new PodInfo(PodBuilder.buildPod("pod0", 0.5, 4, 8.0))
    assert(sched.allocate(pod0))
    assert(client.listPods("pod0") == "node0")

    // new pod has a lower bid than the running pod
    val pod1 = new PodInfo(PodBuilder.buildPod("pod1", 0.25, 4, 8.0))
    assert(!sched.allocate(pod1))
    assert(client.listPods("pod0") == "node0")

    // new pod preempts the running pod
    val pod2 = new PodInfo(PodBuilder.buildPod("pod2", 0.75, 4, 8.0))
    assert(sched.allocate(pod2))
    assert(client.listPods("pod2") == "node0")
  }

  test("pod sequence with increasing bids") {
    val client = new MockLocalNode()
    val sched = new BribeScheduler(client, new SpotManager(client, false, clock), clock)

    (1 to 10).foreach { i =>
      val pod = new PodInfo(PodBuilder.buildPod(s"pod$i", 1.0 * i / 10, 4, 8.0))
      assert(sched.allocate(pod))
    }

    assert(client.listPods.size == 1 && client.listPods("pod10") == "node0")
  }

  test("pod sequence with decreasing bids") {
    val client = new MockLocalNode()
    val sched = new BribeScheduler(client, new SpotManager(client, false, clock), clock)

    sched.allocate(new PodInfo(PodBuilder.buildPod("pod10", 1.0, 4, 8.0)))
    (9 to 1).foreach { i =>
      val pod = new PodInfo(PodBuilder.buildPod(s"pod$i", 1.0 * i / 10, 4, 8.0))
      assert(!sched.allocate(pod))
    }

    assert(client.listPods.size == 1 && client.listPods("pod10") == "node0")
  }

  test("pod sequence with equal bids") {
    val client = new MockLocalNode()
    val sched = new BribeScheduler(client, new SpotManager(client, false, clock), clock)

    sched.allocate(new PodInfo(PodBuilder.buildPod("pod0", 0.5, 4, 8.0)))
    (1 to 10).foreach { i =>
      val pod = new PodInfo(PodBuilder.buildPod(s"pod$i", 0.5, 4, 8.0))
      assert(!sched.allocate(pod))
    }
    assert(client.listPods.size == 1 && client.listPods("pod0") == "node0")
  }

  test("pod requests have heterogeneous demands") {
    val client = new MockLocalNode()
    val sched = new BribeScheduler(client, new SpotManager(client, false, clock), clock)

    val pod0 = new PodInfo(PodBuilder.buildPod("pod0", 0.5, 4, 8.0))
    assert(sched.allocate(pod0))
    assert(client.listPods.size == 1 && client.listPods("pod0") == "node0")

    val pod1 = new PodInfo(PodBuilder.buildPod("pod1", 0.75, 2, 4.0))
    assert(sched.allocate(pod1))
    assert(client.listPods.size == 1 && client.listPods("pod1") == "node0")

    val pod2 = new PodInfo(PodBuilder.buildPod("pod2", 0.5, 4, 8.0))
    assert(!sched.allocate(pod2))
    assert(client.listPods.size == 1 && client.listPods("pod1") == "node0")

    val pod3 = new PodInfo(PodBuilder.buildPod("pod3", 0.5, 2, 4.0))
    assert(sched.allocate(pod3))
    assert(client.listPods.size == 2 &&
      client.listPods("pod1") == "node0" &&
      client.listPods("pod3") == "node0")

    val pod4 = new PodInfo(PodBuilder.buildPod("pod4", 0.9, 4, 8.0))
    assert(sched.allocate(pod4))
    assert(client.listPods.size == 1 && client.listPods("pod4") == "node0")

    val pod5 = new PodInfo(PodBuilder.buildPod("pod5", 1.0, 1, 1.0))
    assert(sched.allocate(pod5))
    assert(client.listPods.size == 1 && client.listPods("pod5") == "node0")
  }
}
