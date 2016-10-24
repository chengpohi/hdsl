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
      select to text where attr eq "itemprop" -> "description" as "desc"
    }
    val e2: String = DSL {
      select to text where attr eq "itemprop" -> "name" and tag eq "h1" as "name"
    }
    val e3: String = DSL {
      select to text where attr eq "more-text" -> "more" and attr eq "metrics-loc" -> "Titledbox_Description" as "more"
    }

    e should be("""{"desc":"Candy Crush Saga"}""")
    e2 should be("""{"name":"Candy Crush Saga"}""")
    e3 should be("""{"more":"Description Close Menu Candy Crush Saga"}""")
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
    val result2: String = DSL {
      select attr "src" where tag eq "img" and attr eq "itemprop" -> "screenshot" under attr eq "metrics-loc" -> "iPhone" as "imgs"
    }

    val result3: String = DSL {
      select attr "src" where tag eq "img" and attr eq "itemprop" -> "screenshot" under attr eq "metrics-loc" -> "iPad" as "imgs"
    }

    result should be("""{"imgs":"www.haha.com"}""")
    result2 should be("""{"imgs":["1.jpeg","2.jpeg","3.jpeg","4.jpeg","5.jpeg"]}""")
    result3 should be("""{"imgs":["6.jpeg","7.jpeg","8.jpeg","9.jpeg","10.jpeg"]}""")
  }
}
