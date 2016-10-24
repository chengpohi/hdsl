package com.github.chengpohi

import com.github.chengpohi.parser.HDSLParser

/**
  * Created by xiachen on 24/10/2016.
  */
class HDSLInterpreter(hdslParser: HDSLParser) {
  def interpret(source: String) = hdslParser.parse(source)
}
