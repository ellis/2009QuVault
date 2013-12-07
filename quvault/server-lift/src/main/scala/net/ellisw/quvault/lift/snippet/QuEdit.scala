package net.ellisw.quvault.lift.snippet

import scala.xml._
import net.liftweb.http._
import net.liftweb.util._
import Helpers._

class QuEdit {
  def getXml(in: NodeSeq): NodeSeq = {
    //val xml = S.param("source").map(_) openOr SampleData.getQuestionXml.toString
    //var sXml = xml.toString
    var sXml = S.param("source") openOr SampleData.getQuestionXml.toString
    // REFACTOR: how to efficiently calculated the number of lines in a string?
    val nRows = sXml.split("\n").length
    
    def saveSource() { println("Ok") } 
    
	//val sCols = "40"
	//(<textarea class="textareawide" rows={sRows} cols={sCols}>{sXml}</textarea>)
	//(<textarea class="textareawide" rows={sRows.toString} wrap="off">{sXml}</textarea>)
  
    Helpers.bind("qu", in,
      "source" -> SHtml.textarea(sXml, sXml = _, "class" -> "textareawide", "rows" -> nRows.toString),
      "submit" -> SHtml.submit("Save", saveSource)
      )
  }
}
