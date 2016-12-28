package com.github.chengpohi.html.parser

import fastparse.all._

/**
  * elasticshell
  * Created by chengpohi on 7/11/16.
  */
trait ParserBasic {
  val WSChars = P(CharsWhile("\u0020\u0009".contains(_)))
  val Newline = P(StringIn("\r\n", "\n"))
  val WL0 = P(NoTrace((WSChars | Newline).rep))
  val WhitespaceApi = new fastparse.WhitespaceApi.Wrapper(WL0)
  val StringChars = NamedFunction(!"\"".contains(_: Char), "StringChars")
  val AlphaChars = NamedFunction(!"\"\\?".contains(_: Char), "StringChars")
  val CollectionChars = NamedFunction(!"[],()\"\\".contains(_: Char), "CollectionChars")

  val strChars = P(CharsWhile(StringChars))
  val alphaChars = P(CharsWhile(AlphaChars))
  val collectionChars = P(CharsWhile(CollectionChars))
  val variableChars = P(CharIn('a' to 'z', 'A' to 'Z'))
  val string = P("\"" ~ strChars.rep(1).! ~ "\"")
  val variable = P(variableChars.rep(1)).!
}

case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
  def apply(t: T): V = f(t)

  override def toString(): String = name
}
