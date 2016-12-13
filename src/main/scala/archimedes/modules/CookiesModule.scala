package archimedes.modules

import archimedes.{Metrics, NettyHttpModel, NettyHttpRequestModel, NettyHttpResponseModel}

/**
  * Created by waseemh on 11/29/16.
  */
class CookiesModule extends NettySubscriberModule {

  override def onNext(value: NettyHttpModel) = value match  {

    case v1: NettyHttpRequestModel =>
      v1.request.headers().entries().forEach(e =>
        if(e.getKey.equalsIgnoreCase("cookie")) {
          Metrics.incr("CookiesSentSize",e.getValue.length)
          Metrics.incr("cookiesSentCount")
        }

      )

    case v2: NettyHttpResponseModel =>
      v2.response.headers().entries().forEach(e =>
        if(e.getKey.equalsIgnoreCase("set-cookie")) {
          Metrics.incr("cookiesReceivedSize",e.getValue.length)
          Metrics.incr("cookiesReceivedCount")
        }
      )
  }
}