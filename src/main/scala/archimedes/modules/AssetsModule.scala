package archimedes.modules

import archimedes.{Metrics, NettyHttpModel, NettyHttpResponseModel}

/**
  * Created by waseemh on 11/25/16.
  */
class AssetsModule extends NettySubscriberModule {

  override def onNext(response: NettyHttpModel) = response match {
    case v: NettyHttpResponseModel =>
      val assetType = getAssetType(v.response.headers().get("content-type"))
      val assetSize = getAssetSize(v.response.headers().get("content-length"))
      if( assetType.equals("image") && assetSize <2*1024 ) {
        Metrics.incr("smallImageFiles")
      }
      if( assetType.equals("css") && assetSize <2*1024 ) {
        Metrics.incr("smallCssFiles")
      }
      if( assetType.equals("js") && assetSize <2*1024 ) {
        Metrics.incr("smallJsFiles")
      }
      Metrics.incr(assetType + "Count")
      Metrics.incr(assetType + "Size", assetSize)
  }

  def getAssetSize(contentLengthHeader: String): Int = {
    contentLengthHeader.toInt
  }

  def getAssetType(contentTypeHeader: String): String = {
    Option(contentTypeHeader) match {
      case Some(hs) => {
        val contentType = hs.split(";")(0).toLowerCase()
        contentType match {
          case "text/html" => "html"
          case "text/xml" => "xml"
          case "text/css" => "css"
          case "application/x-javascript"
               | "application/javascript"
               | "text/javascript" => "js"
          case "application/json" => "json"
          case "image/png"
               | "image/jpeg"
               | "image/gif"
               | "image/svg+xml"
               | "image/webp" => "image"
          case "video/webm" => "video"
          case "application/font-wof"
               | "application/font-woff"
               | "application/vnd.ms-fontobject"
               | "application/x-font-opentype"
               | "application/x-font-truetype"
               | "application/x-font-ttf"
               | "application/x-font-woff"
               | "font/opentype"
               | "vfont/ttf"
               | "font/woff" => "font"
          case "image/x-icon"
               | "image/vnd.microsoft.icon" => "favicon"
          case _ => "other"
        }
      }
      case None => "other"
    }
  }
}