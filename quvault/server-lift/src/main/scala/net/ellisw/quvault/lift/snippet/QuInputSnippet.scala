package net.ellisw.quvault.lift.snippet

import scala.collection.mutable.ArrayBuffer
import scala.xml._

import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.util.Helpers._

class QuInputSnippet {
  def render(in: NodeSeq): NodeSeq = {
    println("QuInputSnippet.render")
    val sAction = S.param("action") openOr ""
    //println("1")
    /*
    var answers: Map[String, String] = null
    if (S.request.isDefined) {
      val req = S.request.open_!
      // Get all the "ans_*" values
      val ansParams = req.params.filter(_._1.startsWith("ans_"))
      // Only keep the first value for each answer (creates a List)
      val ansParamsAsList: Iterable[(String, String)] = ansParams.map((pair) => (pair._1, pair._2.first))
      // Turn it back into a map
      answers = ansParamsAsList.foldLeft(scala.collection.immutable.Map[String, String]()) { (map, pair) => map(pair._1) = pair._2 }
    }
    println("answers:"+ answers)
    println("QuRequestVars.answers.is:"+ QuRequestVars.answers.is.mkString(", "))
    */
    
    //    xhtml = (<form action="/" method="post">{xhtmlQuestion}<input type="submit" value="Score"/></form>)
    val bindings = new ArrayBuffer[BindParam]()
    bindings += ("submit_score" -> SHtml.submit("Score", () => ()))
    //println("2")

    val b = bind("qu", in, bindings : _*)
    //println("3")
    b
  }
}
