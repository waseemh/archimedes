package archimedes

import rx.lang.scala.Subscriber
import rx.lang.scala.subjects.PublishSubject

/**
  *
  * Created by waseemh on 11/23/16.
  */
class EventBus[T] {

  val _requestBus = PublishSubject[T]
  val _responseBus = PublishSubject[T]
  var _pageLoadedBus = PublishSubject[T]

  def send(e: String, o : T) {
    bus(e).onNext(o)
  }

  def subscribe(e: String, subscriber: Subscriber[T]) = {
    bus(e).subscribe(subscriber)
  }

  def bus(e: String) = e match {
      case "onRequest" => _requestBus
      case "onResponse" => _responseBus
      case "onPageLoaded" => _pageLoadedBus
      case _ => _requestBus
    }

}