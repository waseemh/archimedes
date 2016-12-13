package archimedes

import io.netty.handler.codec.http.{HttpRequest, HttpResponse}
import net.lightbody.bmp.util.{HttpMessageContents, HttpMessageInfo}

/**
  * Created by waseemh on 11/24/16.
  *
  **/

trait NettyHttpModel {
  val contents: HttpMessageContents
  val messageInfo: HttpMessageInfo
}

case class NettyHttpRequestModel(contents: HttpMessageContents, messageInfo: HttpMessageInfo, request: HttpRequest) extends NettyHttpModel

case class NettyHttpResponseModel (contents: HttpMessageContents, messageInfo: HttpMessageInfo, response: HttpResponse) extends NettyHttpModel