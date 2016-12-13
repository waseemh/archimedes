package archimedes

import archimedes.modules._
import io.netty.handler.codec.http.{HttpRequest, HttpResponse}
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.filters.{RequestFilter, ResponseFilter}
import net.lightbody.bmp.util.{HttpMessageContents, HttpMessageInfo}
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}

/**
  * Created by waseemh on 11/23/16.
  */
class Archimedes {

  val nettyEventBus = new EventBus[NettyHttpModel]
  val wdEventBus = new EventBus[WebDriver]

  nettyEventBus.subscribe("onRequest",new RequestModule)
  nettyEventBus.subscribe("onRequest",new HeadersModule)
  nettyEventBus.subscribe("onRequest",new CookiesModule)

  nettyEventBus.subscribe("onResponse",new RequestModule)
  nettyEventBus.subscribe("onResponse",new HeadersModule)
  nettyEventBus.subscribe("onResponse",new AssetsModule)
  nettyEventBus.subscribe("onResponse",new CookiesModule)

  wdEventBus.subscribe("onPageLoaded",new NavigationTimingsModule)
  wdEventBus.subscribe("onPageLoaded",new LocalStorageModule)
  wdEventBus.subscribe("onPageLoaded",new SessionStorageModule)
  wdEventBus.subscribe("onPageLoaded",new IFramesModule)

  def measure(url: String) {

    // start the proxy
    val proxy = new BrowserMobProxyServer();
    proxy.setTrustAllServers(true)
    proxy.setMitmDisabled(false)

    proxy.start(0);

    // get the Selenium proxy object
    val seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

    val capabilities = DesiredCapabilities.chrome()

    //set proxy
    capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

    proxy.addRequestFilter(new RequestFilter {
      override def filterRequest(request: HttpRequest, contents: HttpMessageContents, messageInfo: HttpMessageInfo): HttpResponse = {
        nettyEventBus.send("onRequest",new NettyHttpRequestModel(contents,messageInfo,request))
        return null;
      }})

    proxy.addResponseFilter(new ResponseFilter {
      override def filterResponse(response: HttpResponse, contents: HttpMessageContents, messageInfo: HttpMessageInfo): Unit = {
        nettyEventBus.send("onResponse",new NettyHttpResponseModel(contents,messageInfo,response))
      }
    })

    val webdriver = new FirefoxDriver(capabilities);

    webdriver.get(url);

    wdEventBus.send("onPageLoaded",webdriver)

    webdriver.quit();

    System.out.println(Metrics.metrics)

  }

}