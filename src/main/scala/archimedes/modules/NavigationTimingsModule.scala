package archimedes.modules

import archimedes.Metrics
import org.openqa.selenium.{JavascriptExecutor, WebDriver}

/**
  *
  * Total time from start to load
  *   loadTime = timing.loadEventEnd - timing.fetchStart;
  * Time spent constructing the DOM tree
  *   domReadyTime = timing.domComplete - timing.domInteractive;
  * Time consumed preparing the new page
  *   readyStart = timing.fetchStart - timing.navigationStart;
  * Time spent during redirection
  *   redirectTime = timing.redirectEnd - timing.redirectStart;
  * AppCache
  *   appcacheTime = timing.domainLookupStart - timing.fetchStart;
  * Time spent unloading documents
  *   unloadEventTime = timing.unloadEventEnd - timing.unloadEventStart;
  * DNS query time
  *   lookupDomainTime = timing.domainLookupEnd - timing.domainLookupStart;
  * TCP connection time
  *   connectTime = timing.connectEnd - timing.connectStart;
  * Time spent during the request
  *   requestTime = timing.responseEnd - timing.requestStart;
  * Request to completion of the DOM loading
  *   initDomTreeTime = timing.domInteractive - timing.responseEnd;
  * Load event time
  *   loadEventTime = timing.loadEventEnd - timing.loadEventStart;
  *
  * Created by waseemh on 11/28/16.
  *
  */
class NavigationTimingsModule extends WebDriverSubscriberModule {

  override def onNext(wd: WebDriver)= {
    import collection.JavaConverters._
    val timings = wd
      .asInstanceOf[JavascriptExecutor]
      .executeScript("return window.performance.timing")
      .asInstanceOf[java.util.Map[String,Long]]
      .asScala.filter(entry => !entry._1.equals("toJSON"))

    Metrics.incr("loadTime",timings.get("loadEventEnd").get - timings.get("fetchStart").get)
    Metrics.incr("domReadyTime",timings.get("domComplete").get - timings.get("domInteractive").get)
    Metrics.incr("readyStart",timings.get("fetchStart").get - timings.get("navigationStart").get)
    Metrics.incr("redirectTime",timings.get("redirectEnd").get - timings.get("redirectStart").get)
    Metrics.incr("appcacheTime",timings.get("domainLookupStart").get - timings.get("fetchStart").get)
    Metrics.incr("unloadEventTime",timings.get("unloadEventEnd").get - timings.get("unloadEventStart").get)
    Metrics.incr("lookupDomainTime",timings.get("domainLookupEnd").get - timings.get("domainLookupStart").get)
    Metrics.incr("connectTime",timings.get("connectEnd").get - timings.get("connectStart").get)
    Metrics.incr("requestTime",timings.get("responseEnd").get - timings.get("requestStart").get)
    Metrics.incr("initDomTreeTime",timings.get("domInteractive").get - timings.get("responseEnd").get)
    Metrics.incr("loadEventTime",timings.get("loadEventEnd").get - timings.get("loadEventStart").get)
  }

}