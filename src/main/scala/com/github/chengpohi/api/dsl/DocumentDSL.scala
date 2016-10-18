/**
  * chengpohi@gmail.com
  */
package com.github.chengpohi.api.dsl

import org.jsoup.nodes.Document

/**
  * htmlparser
  * Created by chengpohi on 15/10/2016.
  */
class DocumentDSL(d: Document) extends HtmlParserDefinition {
  override val doc: Document = d
  case object select {
    def id(id: String): IdDefinition = IdDefinition(id)
    def attr(key: (String, String)): AttrDefinition = AttrDefinition(key)
  }
  case object elements {
  }
  implicit class PathRoute(key: String) {
    def /(value: String): (String, String) = {
      (key, value)
    }
  }
}
