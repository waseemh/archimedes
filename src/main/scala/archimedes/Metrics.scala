package archimedes

import java.util.concurrent.atomic.AtomicLong

import scala.collection.{mutable,immutable}

/**
  * Created by waseemh on 11/24/16.
  */
object Metrics extends Metrics {

  private val counterMap = new mutable.HashMap[String, Counter]()

  /**
    * Find or create a counter with the given name.
    */
  def getCounter(name: String): Counter = counterMap.synchronized {
    counterMap.get(name) match {
      case Some(counter) => counter
      case None =>
        val counter = new Counter
        counterMap += (name -> counter)
        counter
    }
  }

  def incr1(name: String): () => Long = { () => incr(name, 1) }

  override def incr(name: String, count: Long): Long = {
    getCounter(name).value.addAndGet(count)
  }

  def counters: Map[String,Long] = {
    immutable.HashMap(counterMap.map { case (k, v) => (k, v.value.get) }.toList: _*)
  }

  /**
    * Returns a formatted String containing Memory statistics of the form
    * name: value
    */
  def metrics: String = {
    val out = new mutable.ListBuffer[String]()
    for ((key, value) <- counters) {
      out += (key + ": " + value.toString)
    }
    out.mkString("\n")
  }


}

trait Metrics {
  def incr(name: String, count: Long): Long
  def incr(name: String) : Long = incr(name,1)
}

trait Measurement

class Counter extends Measurement {
  var value = new AtomicLong

  def incr() = value.addAndGet(1)
  def incr(n: Long) = value.addAndGet(n)
  def apply(): Long = value.get()
  def update(n: Long) = value.set(n)
  def reset() = update(0L)
}