package archimedes.modules

import java.net.URL

import archimedes.{Metrics, NettyHttpModel, NettyHttpRequestModel, NettyHttpResponseModel}
import io.netty.handler.codec.http.HttpMethod

import scala.util.{Failure, Success, Try}

/**
  *
  * Created by waseemh on 11/23/16.
  */
class RequestModule extends NettySubscriberModule {

  val domains = scala.collection.mutable.Set[String]()

  override def onNext(transaction: NettyHttpModel): Unit = {

    transaction match {

      case v1: NettyHttpRequestModel => {

        if (v1.request.getMethod.equals(HttpMethod.CONNECT)) return;

        Metrics.incr("totalRequests")

        if (v1.messageInfo.isHttps) Metrics.incr("httpsRequests")

        getDomain(v1.messageInfo.getUrl)

        v1.request.getMethod match {
          case HttpMethod.GET => Metrics.incr("getRequests")
          case HttpMethod.POST => Metrics.incr("postRequests")
          case HttpMethod.PUT => Metrics.incr("putRequests")
          case HttpMethod.HEAD => Metrics.incr("headRequests")
          case HttpMethod.DELETE => Metrics.incr("deleteRequests")
          case HttpMethod.TRACE => Metrics.incr("traceRequests")
          case HttpMethod.OPTIONS => Metrics.incr("optionsRequests")
          case _ => return;
        }
      }

      case v2: NettyHttpResponseModel => {
        import collection.JavaConverters._
        val gzipResponses = v2.response.headers().entries().asScala.toList.count(e => e.getKey.equalsIgnoreCase("content-encoding") && e.getValue.equals("gzip"))
        Metrics.incr("gzipResponses",gzipResponses)

        v2.response.getStatus.code() match {
          case 301 | 302 | 302 => Metrics.incr("redirectRequests")
          case 404 => Metrics.incr("notFoundRequests")
          case _ =>
        }
      }

    }

  }

  def getDomain(url: String): Unit = {
    Try(new URL(url)) match {
      case Success(value) =>  {
        if(!domains.contains(value.getHost)) Metrics.incr("domainsCount")
        domains+=value.getHost
      }
      case Failure(ex) =>
    }
  }


}