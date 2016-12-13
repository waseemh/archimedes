package archimedes.modules

import archimedes.NettyHttpModel
import org.openqa.selenium.WebDriver
import rx.lang.scala.Subscriber

/**
  * Created by waseemh on 11/29/16.
  */

trait Module
abstract class SubscriberModule[T] extends Subscriber[T] with Module
abstract class NettySubscriberModule extends SubscriberModule[NettyHttpModel]
abstract class WebDriverSubscriberModule extends SubscriberModule[WebDriver]