/**
  * chengpohi@gmail.com
  */
package com.github.chengpohi.api.dsl

import org.jsoup.nodes.Document

import scala.collection.JavaConversions._

/**
  * htmlparser
  * Created by chengpohi on 15/10/2016.
  */
trait HtmlParserBase {
  val doc: Document
}

trait SelectType
trait AttrType

case object text extends SelectType
case object links extends SelectType
case object src extends SelectType

case object id extends AttrType
case object attr extends AttrType
case object tag extends AttrType

trait Definition {
  var key: String = "key"
  var _type: SelectType = text
  var _attrType: AttrType = id
  var _v: String = ""
  var _attr: (String, String) = ("", "")
  def execute: Map[String, Any]
  def as(k: String): Definition = {
    key = k
    this
  }

  def to(k: SelectType): Definition = {
    _type = k
    this
  }

  def eq(s: String): Definition = {
    _v = s
    this
  }
  def eq(s: (String, String)): Definition = {
    _attr = s
    this
  }

  def where(attrType: AttrType): Definition = {
    _attrType = attrType
    this
  }
}

trait HtmlParserDefinition extends HtmlParserBase {
  case class TextDefinition() extends Definition {
    override def execute: Map[String, Any] = {
      _attrType match {
        case `id` =>
          val text: String = doc.getElementById(_v).text()
          Map(key -> text)
        case `attr` =>
          val text: String = doc.getElementsByAttributeValue(_attr._1, _attr._2).text()
          Map(key -> text)
        case `tag` =>
          val text: String = doc.getElementsByTag(_v).text()
          Map(key -> text)
      }
    }
  }
  case class AttrDefinition(a: String) extends Definition {
    override def execute: Map[String, Any] = {
      _attrType match {
        case `tag` => {
          val results: List[String] = doc.getElementsByTag(_v).iterator().toList.map(_.attr(a)).filter(!_.isEmpty)
          Map(key -> results)
        }
      }
    }
  }
}

object DSL {
  def apply(d: Definition): Map[String, Any] = d.execute
}
