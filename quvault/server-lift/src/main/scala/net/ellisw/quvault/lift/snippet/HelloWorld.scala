package net.ellisw.quvault.lift.snippet

import scala.xml._
import _root_.net.liftweb.util.Helpers
import Helpers._

class HelloWorld {
  def howdy(in: NodeSeq): NodeSeq =
    Helpers.bind("b", in, "time" -> (new _root_.java.util.Date).toString)
}

