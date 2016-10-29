package com.github.chengpohi.parser


import com.github.chengpohi.api.dsl.{Definition, HtmlParserDefinition}
import fastparse.noApi._
import org.jsoup.nodes.Document

/**
  * Created by xiachen on 24/10/2016.
  */
class HDSLParser(_doc: Document) extends ParserBasic with HtmlParserDefinition {

  import WhitespaceApi._

  private def mergeConditions(j: (String, String, Option[String])) = (j._1, j._2 + j._3.map(t => " " + t).getOrElse(""))

  private val whereCondition = P("where" ~/ (variable ~ "eq" ~/ string ~ ("->" ~ string).? ~ "and".?).rep(1)).map(i => i.map(mergeConditions))
  private val underCondition = P("under" ~/ variable ~ "eq" ~/ string ~ ("->" ~ string).?).map(j => mergeConditions(j))
  private val asCondition = P("as" ~ string)
  private val toTextParser = P("to" ~ "text" ~ whereCondition ~/ underCondition.? ~/ asCondition.?).map(t => {
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
  private val attrParser = P("attr" ~ string ~ whereCondition ~/ underCondition.? ~/ asCondition.?).map(t => {
    val definition: Definition = AttrDefinition(t._1)
    t._2.map(i => {
      val _tag = definition.tagMatcher(i._1)
      definition.where(_tag).eq(i._2)
    })
    t._3.map(i => {
      val _tag = definition.tagMatcher(i._1)
      definition.under(_tag).eq(i._2)
    })
    t._4.map(i => definition.as(i))
    definition
  })

  private val selectParser = P("select" ~ (toTextParser | attrParser))
  private val nestParser = P("nest" ~ "(" ~ selectParser.rep(1, sep = ",") ~ ")" ~ asCondition).map(i => {
    val nestDefinition = NestDefinition(i._1)
    nestDefinition.as(i._2)
    nestDefinition
  })

  private val hdslParser = P(selectParser | nestParser)

  def parse(source: String): Parsed[Definition] = hdslParser.parse(source)

  override val doc: Document = _doc
}
