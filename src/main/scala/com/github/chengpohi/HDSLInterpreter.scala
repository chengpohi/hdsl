package com.github.chengpohi

import com.github.chengpohi.parser.HDSLParser
import fastparse.core.Parsed.{Failure, Success}

/**
  * Created by xiachen on 24/10/2016.
  */
class HDSLInterpreter(hdslParser: HDSLParser) {
  def intercept(source: String): Map[String, Any] = {
    val parseResult = hdslParser.parse(source.trim)
    parseResult match {
      case Success(f, state) => f.execute
      case Failure(_, _, t) => Map("error" -> t.traced.trace)
    }
  }
}
