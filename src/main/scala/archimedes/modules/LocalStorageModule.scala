package archimedes.modules
import archimedes.Metrics
import org.openqa.selenium.{JavascriptExecutor, WebDriver}

/**
  * Created by waseemh on 12/3/16.
  */
class LocalStorageModule extends WebDriverSubscriberModule {

  override def onNext(wd: WebDriver) = {
    val localStoragesSize = wd
      .asInstanceOf[JavascriptExecutor]
      .executeScript("return window.localStorage.length;")
      .asInstanceOf[Long]
    Metrics.incr("localStorageEntries",localStoragesSize)
  }
}
