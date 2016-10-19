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

  it should "select element by id" in {
    val e: String = DSL {
      select to text where id eq "ac-gn-menuanchor-close" as "menu"
    }
    e should be("""{"menu":"Close Menu"}""")
  }

  it should "select element by attr" in {
    val e: String = DSL {
      select to text where attr eq "more-text" -> "More" as "fff"
    }
    e should be("""{"fff":"hello World"}""")
  }

  it should "select element by tag name" in {
    val e: String = DSL {
      select to text where tag eq "hello" as "fff"
    }
    e should be("""{"fff":"world"}""")
  }

  it should "select element attr" in {
    val result: String = DSL {
      select attr "tt" where tag eq "img" as "imgs"
    }
    result should be("""{"imgs":["www.haha.com"]}""")
  }
}
