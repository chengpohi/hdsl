package com.github.chengpohi

import java.io.File

import org.jsoup.Jsoup
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by xiachen on 25/10/2016.
  */
class HDSLInterpreterTest extends FlatSpec with Matchers {
  val doc = Jsoup.parse(new File(this.getClass.getResource("/test.html").getFile), "utf-8")
  val interpreter: HDSLInterpreter = new HDSLInterpreter(doc)

  import com.github.chengpohi.helper.HtmlParserHelper._

  it should "parse text" in {
    val result: String = interpreter.intercept("""select to text where id eq "ac-gn-menuanchor-close" as "menu"""".stripMargin)
    result should be("""{"menu":"Close Menu"}""")

    val e: String = interpreter.intercept(
      """select to text where attr eq "itemprop" -> "description" as "desc"""")
    val e2: String = interpreter.intercept(
      """
        select to text where attr eq "itemprop" -> "name" and tag eq "h1" as "name"
      """)
    val e3: String = interpreter.intercept(
      """
      select to text where attr eq "more-text" -> "more" and attr eq "metrics-loc" -> "Titledbox_Description" as "more"
      """)
    val e4: String = interpreter.intercept(
      """
      select to text where attr eq "itemprop" -> "price" as "price"
      """)
    val e5: String = interpreter.intercept(
      """
      select to text where attr eq "itemprop" -> "datePublished" as "publishDate"
      """)
    val e6: String = interpreter.intercept(
      """
      select to text where attr eq "itemprop" -> "softwareVersion" as "version"
      """)
    e should be("""{"desc":"Candy Crush Saga"}""")
    e2 should be("""{"name":"Candy Crush Saga"}""")
    e3 should be("""{"more":"Description Close Menu Candy Crush Saga"}""")
    e4 should be("""{"price":"Free"}""")
    e5 should be("""{"publishDate":"Oct 19, 2016"}""")
    e6 should be("""{"version":"1.86.0"}""")
  }
  it should "parse attr" in {
    val result: String = interpreter.intercept("""
      select attr "tt" where tag eq "img" as "imgs"
    """)
    val result2: String = interpreter.intercept("""
      select attr "src" where tag eq "img" and attr eq "itemprop" -> "screenshot" under attr eq "metrics-loc" -> "iPhone" as "imgs"
    """)

    val result3: String = interpreter.intercept("""
      select attr "src" where tag eq "img" and attr eq "itemprop" -> "screenshot" under attr eq "metrics-loc" -> "iPad" as "imgs"
    """)

    result should be("""{"imgs":"www.haha.com"}""")
    result2 should be("""{"imgs":["1.jpeg","2.jpeg","3.jpeg","4.jpeg","5.jpeg"]}""")
    result3 should be("""{"imgs":["6.jpeg","7.jpeg","8.jpeg","9.jpeg","10.jpeg"]}""")
  }
}
