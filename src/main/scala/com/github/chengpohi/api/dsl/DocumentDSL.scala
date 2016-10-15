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
  case object element {
    def id(id: String): Definition = IdDefinition(id)
  }
}
