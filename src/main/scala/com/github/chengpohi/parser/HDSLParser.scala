package com.github.chengpohi.parser


import com.github.chengpohi.api.dsl.{Definition, HtmlParserDefinition}
import fastparse.noApi._
import org.jsoup.nodes.Document

/**
  * Created by xiachen on 24/10/2016.
  */
class HDSLParser(_doc: Document) extends ParserBasic with HtmlParserDefinition {

  import WhitespaceApi._

  private val whereCondition = P("where" ~ (variable ~ "eq" ~ string ~ ("->" ~ string).? ~ "and".?).rep(1)).map(i => i.map(j => {
    (j._1, j._2 + j._3.map(t => " " + t).getOrElse(""))
  }))
  private val underCondition = P("under" ~ variable ~ "eq" ~ string)
  private val asCondition = P("as" ~ string)
  private val toTextParser = P("to" ~ "text" ~ whereCondition ~ underCondition.? ~ asCondition.?)
  private val selectParser = P("select" ~ toTextParser)
    .map(t => {
      val definition: Definition = TextDefinition()
      t._1.map(i => {
        val _tag = definition.tagMatcher(i._1)
        definition.where(_tag).eq(i._2)
      })
      t._2.map(i => {
        val _tag = definition.tagMatcher(i._1)
        definition.under(_tag).eq(i._2)
      })
      t._3.map(i => definition.as(i))
      definition
    })

  def parse(source: String): Parsed[Definition] = selectParser.parse(source)

  override val doc: Document = _doc
}
