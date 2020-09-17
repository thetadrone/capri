package scheduler

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

@RunWith(classOf[JUnitRunner])
class AppStatusListenerSuite extends FunSuite with BeforeAndAfter {

  test("single executor job runs to completion") {
    val listener = new AppStatusListener("foo")
    listener.onDriverSubmit(SparkListenerPodSubmit("driver", 0, 0, true, 0))
    listener.onDriverStart(SparkListenerPodStart("driver", 0, 0, true, 100))

    listener.onExecutorSubmit(SparkListenerPodSubmit("exec-1", 0, 0, false, 100))
    listener.onExecutorStart(SparkListenerPodStart("exec-1", 0, 0, false, 200))
    listener.onExecutorComplete(SparkListenerPodComplete("exec-1", 0, 0, false, 1000))

    listener.onDriverComplete(SparkListenerPodComplete("driver", 0, 0, true, 1000))

    assert(listener.totalWaitTime == 100)
    assert(listener.totalRunTime == 900)
    assert(listener.runtime == 900)
    assert(listener.wastedServiceTime == 0)
    assert(listener.serviceTime == 1700)
  }

  test("single executor job with executor preemption") {
    val listener = new AppStatusListener("foo")
    listener.onDriverSubmit(SparkListenerPodSubmit("driver", 0, 0, true, 0))
    listener.onDriverStart(SparkListenerPodStart("driver", 0, 0, true, 100))

    listener.onExecutorSubmit(SparkListenerPodSubmit("exec-1", 0, 0, false, 100))
    listener.onExecutorStart(SparkListenerPodStart("exec-1", 0, 0, false, 200))
    listener.onExecutorPreempt(SparkListenerPodPreempt("exec-1", 0, 0, false, 500))

    listener.onExecutorSubmit(SparkListenerPodSubmit("exec-2", 0, 0, false, 500))
    listener.onExecutorStart(SparkListenerPodStart("exec-2", 0, 0, false, 500))
    listener.onExecutorComplete(SparkListenerPodComplete("exec-2", 0, 0, false, 1000))

    listener.onDriverComplete(SparkListenerPodComplete("driver", 0, 0, true, 1000))

    assert(listener.totalWaitTime == 100)
    assert(listener.totalRunTime == 900)
    assert(listener.runtime == 900)
    assert(listener.wastedServiceTime == 300)
    assert(listener.serviceTime == 1700)
  }

  test("single executor job with driver preemption") {
    val listener = new AppStatusListener("foo")
    listener.onDriverSubmit(SparkListenerPodSubmit("driver-1", 0, 0, true, 0))
    listener.onDriverStart(SparkListenerPodStart("driver-1", 0, 0, true, 100))

    listener.onExecutorSubmit(SparkListenerPodSubmit("exec-1", 0, 0, false, 100))
    listener.onExecutorStart(SparkListenerPodStart("exec-1", 0, 0, false, 200))

    listener.onDriverPreempt(SparkListenerPodPreempt("driver-1", 0, 0, true, 1000))

    listener.onDriverSubmit(SparkListenerPodSubmit("driver-2", 0, 0, true, 1000))
    listener.onDriverStart(SparkListenerPodStart("driver-2", 0, 0, true, 1000))

    listener.onExecutorSubmit(SparkListenerPodSubmit("exec-2", 0, 0, false, 1000))
    listener.onExecutorStart(SparkListenerPodStart("exec-2", 0, 0, false, 1000))
    listener.onExecutorComplete(SparkListenerPodComplete("exec-2", 0, 0, false, 2000))

    listener.onDriverComplete(SparkListenerPodComplete("driver-2", 0, 0, true, 2000))

    assert(listener.totalWaitTime == 100)
    assert(listener.totalRunTime == 1900)
    assert(listener.runtime == 1000)
    assert(listener.wastedServiceTime == 1700)
    assert(listener.serviceTime == 3700)
  }
}
