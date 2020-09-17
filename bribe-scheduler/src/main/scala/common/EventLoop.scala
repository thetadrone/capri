package common

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.PriorityBlockingQueue

import scala.util.control.NonFatal

abstract class EventLoop[E: Ordering](name: String) {

  private val eventQueue: PriorityBlockingQueue[E] = new PriorityBlockingQueue[E]()

  private val stopped = new AtomicBoolean(false)

  private val eventThread = new Thread(name) {
    setDaemon(true)

    override def run(): Unit = {
      try {
        while (!stopped.get) {
          val event = eventQueue.take()
          try {
            onReceive(event)
          } catch {
            case NonFatal(e) =>
              try {
                onError(e)
              } catch {
                case NonFatal(e) => println("Unexpected error in " + name, e)
              }
          }
        }
        println("EXIT QUEUE")
      } catch {
        case ie: InterruptedException => // exit even if eventQueue is not empty
        case NonFatal(e) => println("Unexpected error in " + name, e)
      }
    }

  }

  def start(): Unit = {
    if (stopped.get) {
      throw new IllegalStateException(name + " has already been stopped")
    }
    // Call onStart before starting the event thread to make sure it happens before onReceive
    onStart()
    eventThread.start()
  }

  def stop(): Unit = {
    if (stopped.compareAndSet(false, true)) {
      eventThread.interrupt()
      var onStopCalled = false
      try {
        eventThread.join()
        // Call onStop after the event thread exits to make sure onReceive happens before onStop
        onStopCalled = true
        onStop()
      } catch {
        case ie: InterruptedException =>
          Thread.currentThread().interrupt()
          if (!onStopCalled) {
            // ie is thrown from `eventThread.join()`. Otherwise, we should not call `onStop` since
            // it's already called.
            onStop()
          }
      }
    } else {
      // Keep quiet to allow calling `stop` multiple times.
    }
  }

  def post(event: E): Unit = {
    eventQueue.put(event)
  }

  def isActive: Boolean = eventThread.isAlive

  protected def onStart(): Unit = {}

  protected def onStop(): Unit = {}

  protected def onReceive(event: E): Unit

  protected def onError(e: Throwable): Unit

}
