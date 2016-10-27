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

case object clazz extends AttrType

case object attr extends AttrType

case object tag extends AttrType

trait Definition {
  var _key: String = "key"
  var _underCondition: Boolean = false
  var _type: SelectType = text
  var _attrType: ArrayBuffer[AttrType] = new ArrayBuffer[AttrType]()
  var _v: ArrayBuffer[String] = new ArrayBuffer[String]()
  var _underAttrType: ArrayBuffer[AttrType] = new ArrayBuffer[AttrType]()
  var _underV: ArrayBuffer[String] = new ArrayBuffer[String]()

  def execute: Map[String, Any]

  def as(k: String): Definition = {
    _key = k
    this
  }

  def to(k: SelectType): Definition = {
    _type = k
    this
  }

  def eq(s: String): Definition = {
    _underCondition match {
      case true => _underV.append(s)
      case false => _v.append(s)
    }
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

  def under(attrType: AttrType): Definition = {
    _underAttrType.append(attrType)
    _underCondition = true
    this
  }

  def attrInterpreter(as: ArrayBuffer[(AttrType, String)], es: List[Element]): List[Element] = as.foldLeft(es)((a, k) => k._1 match {
    case `id` =>
      a.map(_.getElementById(k._2))
    case `attr` =>
      val s: Array[String] = k._2.split(" ")
      a.flatMap(_.getElementsByAttributeValue(s(0), s(1)))
    case `tag` =>
      a.flatMap(_.getElementsByTag(k._2))
    case `clazz` => a.flatMap(_.getElementsByClass(k._2))
  })


  def dslMatcher(es: List[Element]): List[Element] = _underAttrType.isEmpty match {
    case true =>
      attrInterpreter(_attrType.zip(_v), es)
    case false =>
      val underEs: List[Element] = attrInterpreter(_underAttrType.zip(_underV), es)
      attrInterpreter(_attrType.zip(_v), underEs)
  }

  def tagMatcher(_tag: String): AttrType = _tag match {
    case "id" => id
    case "attr" => attr
    case "tag" => tag
    case "clazz" => clazz
  }
}

trait HtmlParserDefinition extends HtmlParserBase {

  case class TextDefinition() extends Definition {
    override def execute: Map[String, Any] = {
      val e = dslMatcher(List(doc.body()))
      e.size match {
        case 1 => Map(_key -> e.head.text())
        case _ => Map(_key -> e.map(_.text()).filter(!_.isEmpty))
      }
    }
  }

  case class AttrDefinition(_a: String) extends Definition {
    override def execute: Map[String, Any] = {
      val results = dslMatcher(List(doc.body())).map(_.attr(_a)).filter(!_.isEmpty)
      results.size match {
        case 1 => Map(_key -> results.head)
        case _ => Map(_key -> results)
      }
    }
  }

  case class NestDefinition(definitions: List[Definition]) extends Definition {
    override def execute: Map[String, Any] = {
      val res = definitions
        .map(d => d.execute.asInstanceOf[Map[String, List[String]]].head)
        .map(k => k._2.map(i => (k._1, i))).transpose.map(_.toMap)
      Map(_key -> res)
    }
  }

}

object DSL {
  def apply(d: Definition): Map[String, Any] = d.execute

  def apply(d: List[Definition]): List[Map[String, Any]] = d.map(_.execute)
}
