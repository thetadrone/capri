package common

trait Clock {
  def getTimeMillis(): Long
}

class SystemClock extends Clock {
  override def getTimeMillis(): Long = System.currentTimeMillis()
}

class DummyClock extends Clock {
  var now = 0L

  override def getTimeMillis(): Long = {
    val crt = now
    now += 100
    crt
  }
}
