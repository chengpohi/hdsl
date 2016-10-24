package com.github.chengpohi.parser


import fastparse.noApi._

/**
  * Created by xiachen on 24/10/2016.
  */
class HDSLParser extends Basic{
  import WhitespaceApi._

  val text = P("select" ~ "to" ~ "text" ~ "where")

  def parse(source: String) = text.parse(source)
}
