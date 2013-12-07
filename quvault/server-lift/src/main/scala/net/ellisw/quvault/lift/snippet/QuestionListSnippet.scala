package net.ellisw.quvault.lift.snippet

import scala.xml._
import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.util.Helpers._

import net.ellisw.quvault.vault.ProbServer


class QuestionListSnippet {
  def render(in: NodeSeq): NodeSeq = {
    val sResponse = ProbServer.getStringResponse(Array("questions/"))
    val elemResponse = XML.loadString(sResponse)
    //println("elemResponse: " + elemResponse.toString)
    
    val xmlHeaders = (elemResponse \ "header")
    val itemTemplate = chooseTemplate("qu", "table_item", in)
    bind("qu", in,
      "table" -> renderTableItems(xmlHeaders, itemTemplate))
  }
  
  private def renderTableItems(xmlHeaders: NodeSeq, itemTemplate: NodeSeq): NodeSeq = {
    xmlHeaders.flatMap(xmlHeader => {
      val sTitle = (xmlHeader \ "@title").text
      val sId = (xmlHeader \ "@id").text
      val sLink = "/view/standard/questions/" + sId + "/"
      bind("item", itemTemplate,
        "title" -> (<a href={sLink}>{sTitle}</a>))
    })
  }
}
