/**
  * chengpohi@gmail.com
  */
package com.github.chengpohi.helper

import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

/**
  * htmlparser
  * Created by chengpohi on 15/10/2016.
  */
object HtmlParserHelper {

  implicit val formats = DefaultFormats
  implicit def mapToJson(m: Map[String, Any]): String = {
    write(m)
  }

}
