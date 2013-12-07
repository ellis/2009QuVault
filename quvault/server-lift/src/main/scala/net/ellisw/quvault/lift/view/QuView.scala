package net.ellisw.quvault.lift.view

import java.net.URLEncoder

import scala.xml._
import scala.collection.mutable.ArrayBuffer

import net.liftweb.http._
import net.liftweb.util.HttpHelpers

import net.ellisw.quvault.lift._
import net.ellisw.quvault.lift.snippet._
import net.ellisw.quvault.vault.ProbServer

class QuView extends LiftView {
  override def dispatch = {
    case _ => render _
  }
  
  def render: NodeSeq = {
    try {
      render2
    }
    catch {
      case ex: Exception => ex.printStackTrace(); (<div>exception</div>) 
    }
  }
  
  private def render2: NodeSeq = {
    println("QuView.render")
    val sSource = S.param("_source") openOr ""
    val sFolder = S.param("_folder") openOr ""
    val sObject = S.param("_object") openOr ""
    var sAction = S.param("_action") openOr ""
    if (QuRequestVars.sAction.is != null)
      sAction = QuRequestVars.sAction.is
    if (sAction == "" || sAction == "index")
      sAction = "config"

    println("render2: sSource=%s, sFolder=%s, sObject=%s, sAction=%s", sSource, sFolder, sObject, sAction);
    
    var contents = new ArrayBuffer[Node]()
    if (sSource != "" && sFolder != "" && sObject != "" && sAction != "") {
      if (sSource == "standard") {
        val xmlQuestion = getQuestionXml(sFolder, sObject)
        contents ++= (sAction match {
          case "config" =>     config(xmlQuestion)
          case "view" =>       viewWithSurround(xmlQuestion, QuAnswerMode.Blank)
          case "viewans" =>    viewWithSurround(xmlQuestion, QuAnswerMode.Answer)
          case "viewsln" =>    viewWithSurround(xmlQuestion, QuAnswerMode.Solution)
          case "work" =>       viewWithSurround(xmlQuestion, QuAnswerMode.Edit)
        })
      }
    }

    println("render2: done")
    contents
  }
  
  private def getQuestionXml(sFolder: String, sObject: String): Node = {
    val sPath = List(sFolder, sObject, "xml-and-params").mkString("/")

    var sQuery =
    if (S.request.isDefined) {
      val req = S.request.open_!
      val exclude = List("_source", "_folder", "_object", "_action")
      val reqParams = req.params.filter(pair => {
        val name = pair._1;
        (!name.startsWith("ans_") && !exclude.contains(name))
      })
      (reqParams.map {case (n, v) => urlEncode(n) +"="+ urlEncode(v.first) }).mkString("&")
    }
    else
      null
    println("sQuery:"+ sQuery)
    
    //println("sPath: "+ sPath)
    val args = Array(sPath + "?" + sQuery)
    val sResponse = ProbServer.getStringResponse(args)
    //println("sResponse: " + sResponse)
    val elemResponse = XML.loadString(sResponse)
    //println("elemResponse: " + elemResponse.toString)
    elemResponse
  }
  
  private def urlEncode(in : String) = URLEncoder.encode(in, "UTF-8")
  
  private def config(xmlQuestion: Node): NodeSeq = {
    val xmlSpecs = (xmlQuestion \ "param-specs")
    val xmlParams = (xmlQuestion \ "params")
    val r = new QuRenderParams
    val xhtmlParams =
      if (xmlSpecs.length > 0 && xmlParams.length == 1) {
        val mapParams = getParamMap(xmlParams.first)
        r.render(xmlSpecs, mapParams)
      }
      else
        Text("")
    
    val sQuery = QuRequestVars.sQuery.is
    println("config: sQuery: " + sQuery)
    (<lift:surround with="config" at="content">
      <table>
      <tr>
      <td valign="top" id="config">
      {xhtmlParams}
      </td>

      <td valign="top">
      <div id="question">
      <form method="post">{SHtml.submit("Work", () => on_work_clicked(sQuery))} Answer the questions</form>
      <hr/>
      {view(xmlQuestion, QuAnswerMode.Blank)}
      <hr/>
      <form method="post">{SHtml.submit("Work", () => on_work_clicked(sQuery))} Answer the questions</form>
      </div> <!-- /question -->
      </td></tr>
      </table>
    </lift:surround>)
  }
  
  private def view(xmlQuestion: Node, mode: QuAnswerMode.Value): NodeSeq = {
    //var scores: scala.collection.mutable.Map[String, String] = null
    var scores: scala.collection.Map[String, ScoreData] = null
    println("QuRequestVars.bScore.is: "+ QuRequestVars.bShowScores.is)
    if (QuRequestVars.bShowValidations.is || QuRequestVars.bShowScores.is) {
      val spec = new QuRenderSpec(xmlQuestion, null, null, mode,
        QuRequestVars.bShowValidations.is, QuRequestVars.bShowScores.is, QuRequestVars.bShowSolutions.is)
      val scorer = new QuScorer(spec);
      scorer.score
      scores = scorer.scores
      println("scores: "+ scores)
    }
    
    val spec = new QuRenderSpec(xmlQuestion, null, scores, mode,
      QuRequestVars.bShowValidations.is, QuRequestVars.bShowScores.is, QuRequestVars.bShowSolutions.is)
    val r = new QuRenderer(spec)
    r.render
  }
  
  private def viewWithButtons(xmlQuestion: Node, mode: QuAnswerMode.Value): NodeSeq = {
    val contents = view(xmlQuestion, mode)
    val bShowSolutions = !QuRequestVars.bShowSolutions.is;
    if (mode == QuAnswerMode.Edit) {
      (<form method="post">
        {getInstructionsXhtml(xmlQuestion)}
        {contents}
        <hr/>
        {SHtml.submit("Validate", on_validate_clicked _)}
        {SHtml.submit("Score", on_score_clicked _)}
        {SHtml.submit("Solutions", () => on_solutions_clicked(bShowSolutions))}
        {SHtml.submit("Configure", on_configure_clicked _)}
      </form>)
    }
    else
      contents
  }
  
  private def viewWithSurround(xmlQuestion: Node, mode: QuAnswerMode.Value): NodeSeq = {
    val contents = viewWithButtons(xmlQuestion, mode)
    (<lift:surround with="default" at="content">{contents}</lift:surround>)
  }
  
  private def getParamMap(xmlParams: Node): scala.collection.Map[String, String] = {
    val map = scala.collection.mutable.Map[String, String]()
    val xmlChildren = (xmlParams \\ "param")
    var asParams = new ArrayBuffer[String]
    xmlChildren.foreach((node) => {
      val name = (node \ "@name").text
      val value = (node \ "@value").text
      map(name) = value
      asParams += name + "=" + value;
    })
    
    QuRequestVars.sQuery(asParams.mkString("&")) 
    println("getParamMap: QuRequestVars.sQuery: " + QuRequestVars.sQuery.is)
    
    map
  }
  
  private def getInstructionsXhtml(xmlQuestion: Node): NodeSeq = {
    val r = new QuRendererInstructions
    r.render(xmlQuestion)
  }
  
  private def on_work_clicked(sQuery: String) {
    S.redirectTo("work?" + sQuery)
  }
  
  private def on_validate_clicked() {
    QuRequestVars.bShowValidations(true)
  }
  
  private def on_score_clicked() {
    QuRequestVars.bShowScores(true)
  }
  
  private def on_solutions_clicked(bShow: Boolean) {
    QuRequestVars.bShowSolutions(bShow)
  }
  
  private def on_configure_clicked() {
    val req = S.servletRequest openOr null
    println("req.getQueryString: " + req.getQueryString)
    S.redirectTo("config?" + req.getQueryString)
  }
}
