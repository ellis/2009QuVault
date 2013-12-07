package net.ellisw.quvault.lift

import scala.collection.mutable.ArrayBuffer
import scala.collection._
import scala.xml._

import net.liftweb.http.S
import net.liftweb.http.SHtml
import net.liftweb.util._


// REFACTOR: Rename class and file to QuRenderParamSpecs
class QuRenderParams {

  def render(xmlSpecsLevels: NodeSeq, mapParams: Map[String, String]): NodeSeq = {
    val mapHidden = mutable.Map[String, String]()
    val out = new ArrayBuffer[Node]
    var i = 0
    for (xmlSpecs <- xmlSpecsLevels) {
      val sLegend = "Options #" + (i + 1).toString
      out += (
        <fieldset>
          <legend>{sLegend}</legend>
          {renderLevel(xmlSpecs, mapParams, mapHidden)}
        </fieldset>
      )
      i += 1
    }
    (
      <lift:DummySnippet form="get">
        {out}
        <div id="btnSubmit">
          {SHtml.submit("Apply Changes", updateParams _)}
        </div>
      </lift:DummySnippet>)
  }
  
  private def renderLevel(xmlSpecs: Node, mapParams: Map[String, String], mapHidden: mutable.Map[String, String]): NodeSeq = {
    val out = new ArrayBuffer[Node]
    
    //mapHidden.foreach(pair => out += SHtml.hidden(() => QuRequestVars.params.is(pair._1) = pair._2))
    
    for (xmlSpec <- (xmlSpecs \ "param-spec")) {
      val sType = (xmlSpec \ "@type").text
      val sName = (xmlSpec \ "@name").text
      val sLabel = (xmlSpec \ "@label").text
      val items = (xmlSpec \ "item")
      val sValue = mapParams.get(sName).getOrElse("")
      //mapHidden(sName) = sValue
      out += (<div><b>{sLabel}:</b></div>)
      sType match {
        case "checklist" =>
          val asValues = sValue.split(",")
          val xhtmlItems = new ArrayBuffer[Node]
          for (item <- items) {
            val sItemName = (item \ "@name").text
            val sItemLabel = (item \ "@label").text
            val sCheckboxId = "_" + sName + "_" + sItemName
            //xhtmlItems ++= (<td>{SHtml.checkbox(asValues.contains(sItemName), (b: Boolean) => setChecklistItem(sName, sItemName, b))}</td><td>{sItemLabel}&nbsp;&nbsp;</td>)
            xhtmlItems += (
              <li><label for={sCheckboxId}>{
                SHtml.checkbox(asValues.contains(sItemName), (b: Boolean) => setChecklistItem(sName, sItemName, b), "id" -> sCheckboxId)
              }{
                sItemLabel
              }</label></li>
            )
            //xhtmlItems += Text(sItemLabel + " ") 
            //xhtmlItems ++= Text(" ")
          }
          //out += (<tr><td align="right">{sLabel}:</td><td><table cellpadding="0" cellspacing="0">{xhtmlItems}</table></td></tr>)
          out += (<ul class="checklist">{xhtmlItems}</ul>)
        case "option" =>
          val asValues = sValue.split(",")
          val xhtmlItems = new ArrayBuffer[Node]
          val listItemNames = items.map((item) => (item \ "@name").text)
          val sSelect: Box[String] = if (sValue != "") Full(sValue) else Empty
          val radios = SHtml.radio(listItemNames, sSelect, QuRequestVars.params.is(sName) = _);
          for (item <- items) {
            val sItemName = (item \ "@name").text
            val sItemLabel = (item \ "@label").text
            val sRadioId = "_" + sName + "_" + sItemName
            val attrId = new UnprefixedAttribute("id", sRadioId, Null)
            xhtmlItems += (
              <div><label for={sRadioId}>{radios(sItemName).first.asInstanceOf[Elem] % attrId}{sItemLabel}</label></div>)
          }
          //xhtmlItems ++= radios.flatMap(_.xhtml)
          out ++= xhtmlItems
        case _ =>
          out += (<div>{sValue}</div>) 
      }
    }
    out
  }
  
  def setChecklistItem(sVarName: String, sItemName: String, b: Boolean) {
    val params = QuRequestVars.params.is

    val asValues = params.get(sVarName) match {
      case None => Array[String]()
      case Some("") => Array[String]()
      case Some(s) => s.split(",").filter(_ != sItemName)
    }
    
    val asValuesNew =
      if (b)
        asValues ++ Array(sItemName)
      else
        asValues

    val sValueNew = asValuesNew.mkString(",")
    params(sVarName) = sValueNew
  }
  
  def updateParams {
    val params = QuRequestVars.params.is
    println("updateParams: "+ params)
    if (params.size > 0) {
      val sQuery = params.map(pair => pair._1 +"="+ pair._2).mkString("&")
      S.redirectTo("config?"+ sQuery)
    }
  }
  
}
