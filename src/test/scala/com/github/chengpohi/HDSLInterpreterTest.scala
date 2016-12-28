package com.github.chengpohi

import java.io.File

import org.jsoup.Jsoup
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

/**
  * Created by xiachen on 25/10/2016.
  */
class HDSLInterpreterTest extends FlatSpec with Matchers {
  val doc = Jsoup.parse(new File(this.getClass.getResource("/test.html").getFile), "utf-8")
  val interpreter: HDSLInterpreter = new HDSLInterpreter(doc)

  import com.github.chengpohi.html.helper.HtmlParserHelper._

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
    val result: String = interpreter.intercept(
      """
      select attr "tt" where tag eq "img" as "imgs"
      """)
    val result2: String = interpreter.intercept(
      """
      select attr "src" where tag eq "img" and attr eq "itemprop" -> "screenshot" under attr eq "metrics-loc" -> "iPhone" as "imgs"
      """)

    val result3: String = interpreter.intercept(
      """
      select attr "src" where tag eq "img" and attr eq "itemprop" -> "screenshot" under attr eq "metrics-loc" -> "iPad" as "imgs"
      """)

    result should be("""{"imgs":"www.haha.com"}""")
    result2 should be("""{"imgs":["1.jpeg","2.jpeg","3.jpeg","4.jpeg","5.jpeg"]}""")
    result3 should be("""{"imgs":["6.jpeg","7.jpeg","8.jpeg","9.jpeg","10.jpeg"]}""")
  }

  it should "parse nest object" in {
    val result: String = interpreter.intercept(
      """
      nest(
        select to text where clazz eq "customerReviewTitle" as "title",
        select attr "aria-label" where clazz eq "rating" under clazz eq "customer-review" as "rating",
        select to text where clazz eq "content" under clazz eq "customer-review" as "content",
        select to text where clazz eq "user-info" under clazz eq "customer-review" as "userInfo"
      ) as "reviews"
      """)
    val result2: String = interpreter.intercept(
      """
      nest(
        select to text where clazz eq "in-app-title" under clazz eq "in-app-purchases" as "name",
        select to text where clazz eq "in-app-price" under clazz eq "in-app-purchases" as "price"
      ) as "purchases"
      """)
    val result3: String = interpreter.intercept(
      """
      nest(
        select attr "src" where tag eq "img" and clazz eq "artwork" under clazz eq "swoosh" as "img",
        select attr "href" where tag eq "a" and clazz eq "name" under clazz eq "swoosh" as "link",
        select to text where clazz eq "name" under clazz eq "swoosh" as "name",
        select to text where clazz eq "genre" under clazz eq "swoosh" as "genre"
      ) as "apps"
      """)
    result should startWith("""{"reviews":[{"title":"Stealing our money!","rating":"1 star","content":"play","userInfo":"by Marmeehayden"}""")
    result2 should startWith("""{"purchases":[{"name":"Extra Moves","price":"$0.99"},""")
    result3 should startWith("""{"apps":[{"img":"https://s.mzstatic.com/htmlResources/d0cf036/frameworks/images/p.png","link":"h""")
  }

  it should "intercept files" in {
    val source: String = Source.fromURL(this.getClass.getResource("/selectors.txt")).getLines().mkString("")
    val result: Map[String, Any] = interpreter.intercept(source)
    result.size should be(14)
    result.keys should contain("reviews")
  }
  it should "return empty when not found element" in {
    val result: Map[String, Any] = interpreter.intercept("""select to text where id eq "notfoundid" as "menu"""".stripMargin)
    result should be(Map("menu" -> List()))
    val result3: String = interpreter.intercept(
      """
      nest(
        select attr "src" where tag eq "img" and clazz eq "notfound" under clazz eq "swoosh" as "img",
        select attr "href" where tag eq "a" and clazz eq "name" under clazz eq "swoosh" as "link",
        select to text where clazz eq "name" under clazz eq "swoosh" as "name",
        select to text where clazz eq "genre" under clazz eq "swoosh" as "genre"
      ) as "apps"
      """)
    result3 should not contain "src"
    val result4: String = interpreter.intercept(
      """
      nest(
        select attr "src" where tag eq "img" and clazz eq "notfound" under clazz eq "swoosh" as "img",
        select attr "href" where tag eq "a" and clazz eq "notfound" under clazz eq "swoosh" as "link",
        select to text where clazz eq "name" under clazz eq "notfound" as "name",
        select to text where clazz eq "genre" under clazz eq "notfound" as "genre"
      ) as "apps"
      """)
    result4.contains("[]") should be (true)
  }
}
