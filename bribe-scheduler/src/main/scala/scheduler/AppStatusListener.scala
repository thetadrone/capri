package scheduler

import scala.collection.mutable.HashMap


class AppStatusListener(appid: String) {

  private val nameToMetrics = new HashMap[String, PodMetrics]

  case class PodMetrics(name: String) {
    var submitTime: Long = 0L
    var startTime: Long = 0L
    var finishTime: Long = 0L

    var isDriver = false
    var isComplete = false

    def waitTime: Long = startTime - submitTime

    def runTime: Long = finishTime - startTime
  }

  def onDriverSubmit(event: SparkListenerPodSubmit): Unit = {
    val metrics = PodMetrics(event.name)
    metrics.submitTime = event.time
    metrics.isDriver = true
    nameToMetrics(event.name) = metrics
  }

  def onDriverStart(event: SparkListenerPodStart): Unit = {
    if (!nameToMetrics.contains(event.name)) {
      throw new IllegalStateException("Pod information is missing.")
    }
    nameToMetrics(event.name).startTime = event.time
  }

  def onDriverComplete(event: SparkListenerPodComplete): Unit = {
    if (!nameToMetrics.contains(event.name)) {
      throw new IllegalStateException("Pod information is missing.")
    }
    nameToMetrics(event.name).finishTime = event.time
    nameToMetrics(event.name).isComplete = true

    nameToMetrics.foreach {
      case (k, v) if v.finishTime == 0L && !v.isDriver =>
        nameToMetrics(k).finishTime = event.time
        nameToMetrics(k).isComplete = true
      case _ =>
    }
  }

  def onDriverPreempt(event: SparkListenerPodPreempt): Unit = {
    if (!nameToMetrics.contains(event.name)) {
      throw new IllegalStateException("Pod information is missing.")
    }
    nameToMetrics(event.name).finishTime = event.time
    nameToMetrics(event.name).isComplete = false

    nameToMetrics.foreach {
      case (k, v) if v.finishTime == 0 && !v.isDriver =>
        nameToMetrics(k).finishTime = event.time
        nameToMetrics(k).isComplete = false
      case _ =>
    }
  }

  def onExecutorSubmit(event: SparkListenerPodSubmit): Unit = {
    val metrics = PodMetrics(event.name)
    metrics.submitTime = event.time
    metrics.isDriver = false
    nameToMetrics(event.name) = metrics
  }

  def onExecutorStart(event: SparkListenerPodStart): Unit = {
    if (!nameToMetrics.contains(event.name)) {
      throw new IllegalStateException("Pod information is missing.")
    }
    nameToMetrics(event.name).startTime = event.time
  }

  def onExecutorComplete(event: SparkListenerPodComplete): Unit = {
    if (!nameToMetrics.contains(event.name)) {
      throw new IllegalStateException("Pod information is missing.")
    }
    nameToMetrics(event.name).finishTime = event.time
    nameToMetrics(event.name).isComplete = true
  }

  def onExecutorPreempt(event: SparkListenerPodPreempt): Unit = {
    if (!nameToMetrics.contains(event.name)) {
      throw new IllegalStateException("Pod information is missing.")
    }
    nameToMetrics(event.name).finishTime = event.time
    nameToMetrics(event.name).isComplete = false
  }

  /**
    * Total waiting time of an application is given by the sum of the waiting time
    * of each driver pod.
    */
  def totalWaitTime: Long = {
    nameToMetrics.values.filter {
      case v if v.isDriver => true
      case _ => false
    }.map(_.waitTime).sum
  }

  /**
    * Similarly to the waiting time, the total runtime including the wasted time of
    * an application is the sum of the runtimes of each driver pod.
    * @return
    */
  def totalRunTime: Long = {
    nameToMetrics.values.filter {
      case v if v.isDriver => true
      case _ => false
    }.map(_.runTime).sum
  }

  /**
    * The actual runtime of the application without any interruptions.
    */
  def runtime: Long = {
    nameToMetrics.values.filter {
      case v if v.isDriver && v.isComplete => true
      case _ => false
    }.map(_.runTime).sum
  }

  /**
    * Total wasted service time of all pods, including drivers.
    */
  def wastedServiceTime: Long = {
    nameToMetrics.values.filter {
      case v if v.finishTime > 0 && v.startTime > 0 && !v.isComplete => true
      case x => false
    }.map(_.runTime).sum
  }

  /**
    * Total service time including wasted service time of all pods.
    * @return
    */
  def serviceTime: Long = {
    nameToMetrics.values.filter {
      case v if v.finishTime > 0 && v.startTime > 0 => true
      case _ => false
    }.map(_.runTime).sum
  }

  /**
   * The total response time of an application is the time elapsed between
   * the first driver pod was submitted and the completion time of the last
   * submitted driver pod.
   */
  def responseTime: Long = {
    val left = nameToMetrics.values.filter {
      case v if v.isDriver => true
      case _ => false
    }.map(_.submitTime).min

    val right = nameToMetrics.values.filter {
      case v if v.isDriver => true
      case _ => false
    }.map(_.finishTime).max

    right - left
  }

  def getSlowdown: Float = {
    (1.0 * responseTime / runtime).toFloat
  }
}
