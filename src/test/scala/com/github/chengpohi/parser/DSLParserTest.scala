package com.github.chengpohi.parser

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by xiachen on 24/10/2016.
  */
class DSLParserTest extends FlatSpec with Matchers {
  val hdslParser = new HDSLParser
  it should "parse texts" in {
    val result = hdslParser.parse("""select to text where id eq "ac-gn-menuanchor-close" as "menu"""")
  }
}
