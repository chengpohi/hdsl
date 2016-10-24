/**
  * chengpohi@gmail.com
  */
package com.github.chengpohi.api.dsl

import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

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
  var _attrType: ArrayBuffer[AttrType] = new ArrayBuffer[AttrType]()
  var _v: ArrayBuffer[String] = new ArrayBuffer[String]()

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
    _v.append(s)
    this
  }

  def where(attrType: AttrType): Definition = {
    _attrType.append(attrType)
    this
  }

  def and(attrType: AttrType): Definition = {
    _attrType.append(attrType)
    this
  }
}

trait HtmlParserDefinition extends HtmlParserBase {

  case class TextDefinition() extends Definition {
    override def execute: Map[String, Any] = {
      val e = _attrType.zip(_v).foldLeft(List(doc.body()))((a, k) => k._1 match {
        case `id` =>
          a.map(_.getElementById(k._2))
        case `attr` =>
          val s: Array[String] = k._2.split(" ")
          a.flatMap(_.getElementsByAttributeValue(s(0), s(1)))
        case `tag` =>
          a.flatMap(_.getElementsByTag(k._2))
      })
      e.size match {
        case 1 => Map(key -> e.head.text())
        case _ => Map(key -> e.map(_.text()).filter(!_.isEmpty))
      }

    }
  }

  case class AttrDefinition(_a: String) extends Definition {
    override def execute: Map[String, Any] = {
      val results = _attrType.zip(_v).foldLeft(List(doc.body()))((a, k) => {
        k._1 match {
          case `tag` => a.flatMap(_.getElementsByTag(k._2))
        }
      })
      Map(key -> results.map(_.attr(_a)).filter(!_.isEmpty))
    }
  }

}

object DSL {
  def apply(d: Definition): Map[String, Any] = d.execute
}
