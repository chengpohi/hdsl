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
    val e4: String = DSL {
      select to text where attr eq "itemprop" -> "price" as "price"
    }
    val e5: String = DSL {
      select to text where attr eq "itemprop" -> "datePublished" as "publishDate"
    }
    val e6: String = DSL {
      select to text where attr eq "itemprop" -> "softwareVersion" as "version"
    }

    val e7: String = DSL {
      select to text where attr eq "itemprop" -> "reviewCount" as "reviewCount"
    }
    val e8: String = DSL {
      select to text where clazz eq "rating-count" under clazz eq "rating" as "ratings"
    }

    e should be("""{"desc":"Candy Crush Saga"}""")
    e2 should be("""{"name":"Candy Crush Saga"}""")
    e3 should be("""{"more":"Description Close Menu Candy Crush Saga"}""")
    e4 should be("""{"price":"Free"}""")
    e5 should be("""{"publishDate":"Oct 19, 2016"}""")
    e6 should be("""{"version":"1.86.0"}""")
    e7 should be("""{"reviewCount":"780 Ratings"}""")
    e8 should be("""{"ratings":["780 Ratings","873648 Ratings"]}""")
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

  it should "select elements by class" in {
    val e: String = DSL {
      select to text where clazz eq "customerReviewTitle" as "reviewTitle"
    }
    val e2: String = DSL {
      select to text where clazz eq "content" under clazz eq "customer-review" as "reviewContent"
    }
    val e3: String = DSL {
      select attr "aria-label" where clazz eq "rating" under clazz eq "customer-review" as "ratings"
    }
    val e4: String = DSL {
      select to text where clazz eq "genre" and tag eq "a" under id eq "left-stack" as "genre"
    }
    val e5: String = DSL {
      select to text where clazz eq "language" as "language"
    }
    e should be("""{"reviewTitle":["Stealing our money!","Won't load on ipod touch 4th gen since 'update'","Re-rating to 1"]}""")
    e2 should be("""{"reviewContent":["play","world","haha"]}""")
    e3 should be("""{"ratings":["1 star","1 star","1 star"]}""")
    e4 should be("""{"genre":"Games"}""")
    e5 should be("""{"language":"Languages: English"}""")
  }

  it should "select elements as nest object" in {
    val e: String = DSL {
      nest(
        select to text where clazz eq "customerReviewTitle" as "title",
        select attr "aria-label" where clazz eq "rating" under clazz eq "customer-review" as "rating",
        select to text where clazz eq "content" under clazz eq "customer-review" as "content",
        select to text where clazz eq "user-info" under clazz eq "customer-review" as "userInfo"
      ) as "reviews"
    }

    e should startWith("""{"reviews":[{"title":"Stealing our money!","rating":"1 star","content":"play","userInfo":"by Marmeehayden"}""")
    println(e)
  }
}
