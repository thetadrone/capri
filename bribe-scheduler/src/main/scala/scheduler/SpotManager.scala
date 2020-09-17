package scheduler

import capri.advisor.BidAdvisor

import scala.collection.mutable
import scala.collection.mutable.HashMap
import cluster.{Cluster, KubernetesCluster}
import common.{Clock, EventLoop, SchedConfig, SystemClock}

class SpotManager(client: Cluster, clock: Clock) extends Runnable {

  private val RESTARTS_ALLOWED = 4

  val activePods = new mutable.HashSet[String]
  val finishedPods = new mutable.HashSet[String]

  private val scheduler = new BribeScheduler(client, this, clock)
  
  private val appToListener = new HashMap[String, AppStatusListener]()

  // private val bidAdvisor = new BidAdvisor(SchedConfig.CAPRI_PARAM_FILE)

  private val slowdownAdvice = new mutable.HashMap[String, Float]

  private val submitTimestamp = new mutable.HashMap[String, Long]

  private val driverRestarts = new mutable.HashMap[String, Int]

  private val execRestarts = new mutable.HashMap[String, Int]

  private val executorsCount = new mutable.HashMap[String, Int]

  val eventLoop = new EventLoop[PodInfo](name = "events") {
    override def onReceive(nextPod: PodInfo): Unit = {
      if (nextPod.phase == "Pending") {
        markPodAsSubmitted(nextPod)
      } else if (nextPod.phase == "Succeeded") {
        markPodAsFinished(nextPod)
      } else if (nextPod.phase == "Running") {
        // ignore everything else
      } else if (nextPod.phase == "Failed") {
        // ignore everything else
      } else {
        // do nothing
      }
    }

    override def onError(e: Throwable): Unit = {}
  }

  val schedLoop = new EventLoop[PodInfo]("sched") {

    override def onReceive(nextPod: PodInfo): Unit = {
      //println("Next pod to schedule is " + nextPod.name)
      val ret = scheduler.allocate(nextPod)
      if (!ret) {
        this.post(nextPod)
      }
    }

    override def onError(e: Throwable): Unit = {}
  }

  override def run(): Unit = {
    println(s"Starting the bribe pod controller")

    schedLoop.start()
    client.intercept(eventLoop)
  }

  def markPodAsSubmitted(pod: PodInfo): Unit = {
    if (activePods.contains(pod.name)) {
      return
    }

    activePods += pod.name

    if (!appToListener.contains(pod.appid)) {
      val listener = new AppStatusListener(pod.appid)
      appToListener(pod.appid) = listener
    }

    val podEvent =
      SparkListenerPodSubmit(
        pod.appid,
        pod.name,
        pod.bid,
        pod.slowdown,
        pod.isDriver,
        clock.getTimeMillis())

    if (pod.isDriver) {
      executorsCount(pod.appid) = pod.children
    }

    postEvent(pod.appid, podEvent)

    schedLoop.post(pod)
  }

  def markPodAsFinished(pod: PodInfo): Unit = {
    if (finishedPods.contains(pod.name)) {
      return
    }

    finishedPods += pod.name
    if (pod.isDriver) {
      scheduler.deallocate(pod)
    }
  }

  def postEvent(appid: String, event: SparkListenerEvent): Unit = {
    if (!appToListener.contains(appid)) {
      return
    }
    val listener = appToListener(appid)
    event match {
      case podSubmit: SparkListenerPodSubmit =>
        //println("PodSubmit " + System.currentTimeMillis() + " " +
        //  podSubmit.appid + " " + podSubmit.name)
        if (podSubmit.isDriver) {
          val lowHighRange = new Array[Float](2)
          /*val estim = bidAdvisor
            .estimateSlowdown(podSubmit.bid.toFloat, lowHighRange)
          if (!slowdownAdvice.contains(podSubmit.appid)) {
	          slowdownAdvice(podSubmit.appid) = estim
          }*/
          submitTimestamp(podSubmit.appid) = System.currentTimeMillis()
          listener.onDriverSubmit(podSubmit)
        } else {
          listener.onExecutorSubmit(podSubmit)
        }

      case podStart: SparkListenerPodStart =>
        //println("PodStart " + System.currentTimeMillis() + " " +
        //  podStart.appid + " " + podStart.name)
        if (podStart.isDriver) {
          listener.onDriverStart(podStart)
          if (driverRestarts.contains(podStart.appid)) {
            driverRestarts(podStart.appid) += 1
          } else {
            driverRestarts(podStart.appid) = 1
          }
        } else {
          if (execRestarts.contains(podStart.appid)) {
            execRestarts(podStart.appid) += 1
          } else {
            execRestarts(podStart.appid) = 1
          }
          listener.onExecutorStart(podStart)
        }

      case podPreempt: SparkListenerPodPreempt =>
        //println("PodPreempt " + System.currentTimeMillis() + " " +
        //  podPreempt.appid + " " + podPreempt.name)
        if (podPreempt.isDriver) {
          listener.onDriverPreempt(podPreempt)
          if (driverRestarts.contains(podPreempt.appid)) {
            val r = driverRestarts(podPreempt.appid)
            // println(podPreempt.appid + " " + r)
            if (r == RESTARTS_ALLOWED) {
              val complete = new SparkListenerPodComplete(podPreempt.appid, 
                podPreempt.name, podPreempt.bid, podPreempt.slowdown, 
                podPreempt.isDriver, podPreempt.time)

              listener.onDriverComplete(complete)
              val slowdown = listener.getSlowdown
              val servTime = listener.serviceTime
              val respTime = listener.responseTime
              val waitTime = listener.totalWaitTime
              val wasteTime = listener.wastedServiceTime
              val runtime = listener.runtime

              println("JobFinished " + System.currentTimeMillis() + " " +
                submitTimestamp(podPreempt.appid) + " " +
                appid + " " +
                executorsCount(podPreempt.appid) + " " +
                podPreempt.bid + " " +
                -1 + " " +
                servTime + " " +
                respTime + " " +
                runtime + " " +
                waitTime + " " +
                wasteTime + " " +
                driverRestarts(podPreempt.appid) + " " +
                execRestarts(podPreempt.appid) + " " +
                0)
            }
          }
        } else {
          listener.onExecutorPreempt(podPreempt)
        }

      case podComplete: SparkListenerPodComplete =>
        //println("PodComplete " + System.currentTimeMillis() + " " +
        //  podComplete.appid + " " + podComplete.name + " " + podComplete.isDriver)

        if (podComplete.isDriver) {
          listener.onDriverComplete(podComplete)
          val slowdown = listener.getSlowdown
          //val bidAdvice = bidAdvisor.getBidAdvice(slowdown)
          val servTime = listener.serviceTime
          val respTime = listener.responseTime
          val waitTime = listener.totalWaitTime
          val wasteTime = listener.wastedServiceTime
          val runtime = listener.runtime

          //val ret = bidAdvisor.update(bidAdvice, respTime, servTime)
          println("JobFinished " + System.currentTimeMillis() + " " +
            submitTimestamp(podComplete.appid) + " " +
            appid + " " +
            executorsCount(podComplete.appid) + " " +
            podComplete.bid + " " +
            slowdown + " " +
            servTime + " " +
            respTime + " " +
            runtime + " " +
            waitTime + " " +
            wasteTime + " " +
            driverRestarts(podComplete.appid) + " " +
            execRestarts(podComplete.appid) + " " + 1)
        } else {
          listener.onExecutorComplete(podComplete)
        }
    }
  }

  def getTotalWaitTime(name: String): Long = {
    val listener = appToListener(name)
    listener.totalWaitTime
  }

  def getTotalRunTime(name: String): Long = {
    val listener = appToListener(name)
    listener.totalRunTime
  }

  def getRuntime(name: String): Long = {
    val listener = appToListener(name)
    listener.runtime
  }

  def getWastedServiceTime(name: String): Long = {
    val listener = appToListener(name)
    listener.wastedServiceTime
  }

  def getServiceTime(name: String): Long = {
    val listener = appToListener(name)
    listener.serviceTime
  }
}

object SpotManager {

  def main(argStrings: Array[String]): Unit = {
    val master = if (argStrings.size > 0) Some(argStrings(1)) else None
    
    println(s"Starting the bribe pod controller " + master)
 
   (new SpotManager(new KubernetesCluster(Some(master)), new SystemClock())).run()
  }
}

