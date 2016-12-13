package archimedes.modules

import java.util.Map.Entry

import archimedes._
import io.netty.handler.codec.http.HttpHeaders

import scala.annotation.tailrec

/**
  * Created by waseemh on 11/26/16.
  */
class HeadersModule extends NettySubscriberModule {

  override def onNext(value: NettyHttpModel) = value match {

    case v1: NettyHttpRequestModel =>
      val headersCount = getHeadersCount(v1.request.headers())
      val headersSize = getHeadersSize(v1.request.headers())
      Metrics.incr("headersRequestCount",headersCount)
      Metrics.incr("headersTotalCount",headersCount)
      Metrics.incr("headersRequestSize",headersSize)
      Metrics.incr("headersTotalSize",headersSize)

    case v2: NettyHttpResponseModel =>
      val headersCount = getHeadersCount(v2.response.headers())
      val headersSize = getHeadersSize(v2.response.headers())
      Metrics.incr("headersResponseCount",headersCount)
      Metrics.incr("headersTotalCount",headersCount)
      Metrics.incr("headersResponseSize",headersSize)
      Metrics.incr("headersTotalSize",headersSize)

  }

  type HeaderKey = String
  type HeaderValue = String

  private [this] val getHeaderLength: (HeaderKey, HeaderValue) => Int = (headerKey, headerValue) =>
    (headerKey + ": " + headerValue + "\r\n").length

  def getHeadersSize(httpHeaders: HttpHeaders): Int = {
    import collection.JavaConverters._
    @tailrec
    def headerLengthAggregator(lengthAggregator: Int, remainingEntries: List[Entry[String, String]]): Int = {
      remainingEntries match {
        case x :: xs => headerLengthAggregator(lengthAggregator + getHeaderLength(x.getKey, x.getValue), xs)
        case Nil => lengthAggregator
      }
    }

    headerLengthAggregator(lengthAggregator = 0, httpHeaders.entries().asScala.toList)
  }

  def getHeadersCount(httpHeaders: HttpHeaders): Int = {
    httpHeaders.entries().size();
  }

}