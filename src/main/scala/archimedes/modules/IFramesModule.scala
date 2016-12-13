package archimedes.modules
import archimedes.Metrics
import org.openqa.selenium.{By, WebDriver}

/**
  * Created by waseemh on 12/3/16.
  */
class IFramesModule extends WebDriverSubscriberModule {

  override def onNext(wd: WebDriver) = {
    Metrics.incr("iframesNumber", wd.findElements(By.xpath("//iframe")).size())
  }

}
