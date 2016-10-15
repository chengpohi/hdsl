/**
  * chengpohi@gmail.com
  */
package com.github.chengpohi.api.dsl

import org.jsoup.nodes.Document

/**
  * htmlparser
  * Created by chengpohi on 15/10/2016.
  */
trait HtmlParserBase {
  val doc: Document
}

trait Definition {
  var key: String = "key"
  def execute: Map[String, Any]
  def as(k: String): Definition = {
    key = k
    this
  }
}

trait HtmlParserDefinition extends HtmlParserBase {
  case class IdDefinition(id: String) extends Definition {
    override def execute: Map[String, String] = {
      val text: String = doc.getElementById(id).text()
      Map(key -> text)
    }
  }
}

object DSL {
  def apply(d: Definition): Map[String, Any] = d.execute
}
