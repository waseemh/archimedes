package archimedes.modules

import archimedes.Metrics
import org.openqa.selenium.{JavascriptExecutor, WebDriver}

/**
  * Created by waseemh on 12/3/16.
  */
class SessionStorageModule extends WebDriverSubscriberModule {

  override def onNext(wd: WebDriver) = {
    val sessionStorageSize = wd
      .asInstanceOf[JavascriptExecutor]
      .executeScript("return window.sessionStorage.length;")
      .asInstanceOf[Long]
    Metrics.incr("sessionStorageEntries",sessionStorageSize)
  }
}
