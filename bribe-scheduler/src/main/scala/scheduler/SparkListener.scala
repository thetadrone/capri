package scheduler

trait SparkListenerEvent

case class SparkListenerPodSubmit(appid: String, name: String, bid: Double, slowdown: Double,
    isDriver: Boolean, time: Long) extends SparkListenerEvent

case class SparkListenerPodStart(appid: String, name: String, bid: Double, slowdown: Double,
    isDriver: Boolean, time: Long) extends SparkListenerEvent

case class SparkListenerPodPreempt(appid: String, name: String, bid: Double, slowdown: Double,
    isDriver: Boolean, time: Long) extends SparkListenerEvent

case class SparkListenerPodComplete(appid: String, name: String, bid: Double, slowdown: Double,
    isDriver: Boolean, time: Long) extends SparkListenerEvent
