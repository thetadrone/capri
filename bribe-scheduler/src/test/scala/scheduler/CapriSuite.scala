package scheduler

import capri.advisor.BidAdvisor
import capri.interfaces.{Capri, CapriFactory}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

@RunWith(classOf[JUnitRunner])
class CapriSuite extends FunSuite with BeforeAndAfter {
  val capriModel: Capri = CapriFactory.create()

  capriModel.update("test", 0.4f, 10, 20)
  println(capriModel.getModelParameters.size)
  capriModel.getModelParameters.foreach { p => println(p + " ") }

  val bidAdvisor = new BidAdvisor("capri.cfg")
  val lowHighRange = new Array[Float](2)
  val slowDown = bidAdvisor.estimateSlowdown(0.5f, lowHighRange)
  println(lowHighRange(0) + " " + lowHighRange(1))
}
