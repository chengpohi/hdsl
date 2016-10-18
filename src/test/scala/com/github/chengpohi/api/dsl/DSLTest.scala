/**
  * chengpohi@gmail.com
  */
package com.github.chengpohi.api.dsl

import java.io.File

import org.jsoup.Jsoup
import org.scalatest.{FlatSpec, Matchers}

/**
  * htmlparser
  * Created by chengpohi on 15/10/2016.
  */
class DSLTest extends FlatSpec with Matchers {
  val doc = Jsoup.parse(new File(this.getClass.getResource("/test.html").getFile), "utf-8")
  val documentDSL: DocumentDSL = new DocumentDSL(doc)

  import com.github.chengpohi.helper.HtmlParserHelper._
  import documentDSL._

  it should "get element by id" in {
    val e: String = DSL {
      select id "ac-gn-menuanchor-close" as "menu"
    }
    e should be("""{"menu":"Close Menu"}""")
  }

  it should "get element by attr" in {
    val e: String = DSL {
      select attr "more-text" / "More" as "fff"
    }
    e should be("""{"fff":"hello World"}""")
  }
}
