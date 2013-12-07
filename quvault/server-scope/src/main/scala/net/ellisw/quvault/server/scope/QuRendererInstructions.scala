package net.ellisw.quvault.server.scope

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.LinkedHashSet
import scala.xml._


class QuRendererInstructions {

  def render(xmlQuestions: Node): NodeSeq = {
    val xmlInstructionList = xmlQuestions \\ "instructions"
    val xmlEntryModeList = xmlQuestions \\ "entryMode"
    
    //println("xmlInstructionList: " + xmlInstructionList)
    
    val xhtml = new ArrayBuffer[Node]
    
    //println("xmlInstructionList.size: " + xmlInstructionList.size)
    if (xmlInstructionList.size > 0) {
      val setKeywords = new LinkedHashSet[String] 
      for (xml <- xmlInstructionList) {
        val asKeywords = (xml \ "@keywords").text.split(",").map(_.trim)
        //println("asKeywords: " + asKeywords)
        setKeywords ++= asKeywords
      }
      
      //println("setKeywords: " + setKeywords)
      for (sKeyword <- setKeywords) {
        sKeyword match {
          case "ee" => xhtml += render_instruction_ee
        }
      }
    }
    
    if (xmlEntryModeList.size > 0) {
      val setKeywords = new LinkedHashSet[String] 
      for (xml <- xmlEntryModeList) {
        setKeywords += (xml \ "@mode").text.trim
      }
      
      val xhtmlRows = new ArrayBuffer[Node]
      for (sKeyword <- setKeywords) {
        sKeyword match {
          case "mathlib" => xhtmlRows += (<a href="/help_mathlib.html" target="help">Matlab-Style</a>)
        }
      }

      xhtml += (
        <div style="margin-bottom: 1em">
          <b>Answer Modes:</b>
          Some question require that you type your answer in a special format.  Click the following link(s) for more information:<br/>
          {xhtmlRows}
        </div>
      )
    }
    
    if (xhtml.size > 0)
      (<div>{xhtml}<hr/></div>)
    else
      xhtml
  }

  private def render_instruction_ee: Node = {
    (
<div style="margin-bottom: 1em">
<p><b>Electronics Instructions</b></p>
<p>
Below is a description of the naming convention used for electrical ciruits.
The &quot;Example&quot; column shows how the variable will be displayed in descriptive text.
The &quot;Code&quot; column shows how to name the variable when typing your answers.
Variables names are <i>case-sensitive</i>.
</p>
<table class="vartable">
<tr><th>Symbol</th><th>Example</th><th>Code</th><th>Description</th></tr>
<tr>
  <td>Lower-case <span class="variable">i</span> denotes the current through an element</td>
  <td><span class="variable">i<sub>R1</sub></span></td><td class="vartable-code">iR1</td>
  <td>Current through <i>R1</i></td></tr>
<tr>
  <td>Lower-case <span class="variable">p</span> denotes the power dissipated by an element</td>
  <td><span class="variable">p<sub>R1</sub></span></td>
  <td class="vartable-code">pR1</td><td>Power dissipated by <i>R1</i></td></tr>
<tr>
  <td>Lower-case <span class="variable">v</span> denotes the voltage drop between two nodes</td>
  <td><span class="variable">v<sub>a</sub></span></td><td class="vartable-code">va</td>
  <td>Voltage drop from <i>a</i> to <i>ground</i></td></tr>
<tr>
  <td></td>
  <td><span class="variable">v<sub>ab</sub></span></td>
  <td class="vartable-code">vab</td><td>Voltage drop from <i>a</i> to <i>b</i></td></tr>
<tr>
  <td>Capital <span class="variable">Z</span> denotes combined impedance</td>
  <td><span class="variable">Z<sub>R1,R2</sub></span></td><td class="vartable-code">ZR1R2</td>
  <td>Combined impedance of <i>R1</i> and <i>R2</i></td></tr>
</table>
</div>
    )
  }
}
