package net.ellisw.quvault.server.scope

import scala.collection.mutable.ArrayBuffer
import scala.xml._

import net.ellisw.quvault.server.scope._


class QuRendererMathlib(spec: QuRenderSpec, result: QuRenderResult, state: QuRenderState) extends QuRenderer(spec, result, state) {
  
  override protected def render(node: Node, ctx: QuRenderContext): NodeSeq = {
    val sAnswerMode = (node \ "@answerMode").text

    val questions = new ArrayBuffer[Node]()

    val sTitle = (node \ "@title").text
    val givens = (node \ "given").text.split(',')
    val finds = (node \ "find").text.split(',')
    val bShowGiven = ((node \ "@showGiven").text != "0") && (givens.length > 0)
    val bShowFind = ((node \ "@showFind").text != "0") && (finds.length > 0)

    val xhtmlTitle =
      if (!sTitle.isEmpty)
        (<div class="question-title">{sTitle}</div>)
      else
        Text("")
    
    if (bShowGiven || bShowFind) {
      val nodesInner = new ArrayBuffer[Node]()
      if (bShowGiven && givens.length > 0)
        nodesInner += (<div>Given: {joinVars(givens, "variable", ", ", ctx)}</div>)
      if (bShowFind && finds.length > 0)
        nodesInner += (<div>Find: {joinVars(finds, "variable", ", ", ctx)}</div>)

      questions += (<div class="text">{nodesInner}</div>)
    }

    if (finds.length > 0) {
      state.questionIndex += 1
      
      if (spec.mode != QuAnswerMode.Blank) {
        questions += (<div class="answer-mode">(Answer Mode: matlab-style)</div>)
        questions += Text("\n")
      }
      
      sAnswerMode match {
        case "rhs" => questions += transform_rhs(node, finds, ctx)
        case "equation" => questions += transform_equation(node, givens, finds, ctx)
        case "function" => questions += transform_function(node, givens, finds, ctx)
      }
    }
  
    (<div class="questionMathlib">
       {xhtmlTitle}
       {handle_children(node, ctx) :: questions :: Nil}
     </div>)
  }
  
  private def transform_rhs(node: Node, finds: Array[String], ctx: QuRenderContext): Node = {
    val xmlAnswer = for (find <- finds) yield { transform_rhs_var(node, find, ctx) }
    
    (
      <div>
        <table class="questionMathlibRhs" cellpadding="0" cellspacing="0">
          {xmlAnswer}
        </table>
        {getScoreXhtml(ctx)}
      </div>
    )
  }
  
	private def transform_rhs_var(node: Node, find: String, ctx: QuRenderContext): Node = {
		val sFieldName = "ans_" + state.questionIndex + "_" + find
		val sAnswer = if (spec.answers.contains(sFieldName)) spec.answers(sFieldName) else ""
		val nAnswerCols = sAnswer.length
		val sEditCols = Math.max(60, nAnswerCols).toString

		val xhtmlName = getVarXhtml(find, "find", ctx)
		val xhtmlAnswer = spec.mode match {
			case QuAnswerMode.Blank => Text("")
			case QuAnswerMode.Answer => Text(sAnswer)
			case QuAnswerMode.Edit =>
				//SHtml.text(QuRequestVars.vars(sFieldName), QuRequestVars.vars(sFieldName) = _, "maxlength" -> "80", "size" -> sEditCols)
				result.fields += new QuFieldData(sFieldName, QuFieldKind.SingleLine, Map("maxlength" -> "80", "size" -> sEditCols))
				Elem("qu", sFieldName, null, xml.TopScope)
			case QuAnswerMode.Solution => renderSolution(node)
		}

		if (spec.bShowSolutions)
			(<tr><td class="find">{xhtmlName}&nbsp;=&nbsp;</td><td>{xhtmlAnswer}</td><td>{renderSolution(node, find)}</td></tr>)
		else
			(<tr><td class="find">{xhtmlName}&nbsp;=&nbsp;</td><td>{xhtmlAnswer}</td></tr>)
	}
  
  private def transform_equation(node: Node, givens: Array[String], finds: Array[String], ctx: QuRenderContext): Node = {
    val xmlAnswer = spec.mode match {
      case QuAnswerMode.Blank => Text("")
      case QuAnswerMode.Answer => (<span>user answer</span>)
      case QuAnswerMode.Edit => renderTextarea(node, finds.length)  
      case QuAnswerMode.Solution => renderSolution(node) 
    }
    
    (
      <div>
        <div class="questionMathlibEquation">
            {xmlAnswer}
        </div>
        {getScoreXhtml(ctx)}
      </div>
    )
  }
  
  private def transform_function(node: Node, givens: Array[String], finds: Array[String], ctx: QuRenderContext): Node = {
    val xmlFinds = joinStringsAsNodes(finds, "funcVar", ", ")
    val xmlGivens = joinStringsAsNodes(givens, "funcVar", ", ")
    
    val xmlAnswer = spec.mode match {
      case QuAnswerMode.Blank => Text("...")
      case QuAnswerMode.Answer => (<span>user answer</span>)
      case QuAnswerMode.Edit => renderTextarea(node, finds.length)  
      case QuAnswerMode.Solution => renderSolution(node) 
    }
    
    (
      <div>
        <div class="questionMathlibFunction">
          <b>function</b> [{xmlFinds}] = myAnswer({xmlGivens})
          <div style="margin-left: 2em">
            {xmlAnswer}
          </div>
          <b>end</b>
        </div>
        {getScoreXhtml(ctx)}
      </div>
    )
  }
  
	private def renderTextarea(node: Node, nFinds: Int): Node = {
		try {
			val sFieldName = "ans_" + state.questionIndex
			val sAnswer = if (spec.answers.contains(sFieldName)) spec.answers(sFieldName) else ""
			val asLines = sAnswer.split('\n')
			val nAnswerRows = asLines.length
			val nAnswerCols = asLines.foldLeft(0) { (n, s) => Math.max(n, s.length) }
			val sEditRows = Math.max(nFinds, nAnswerRows).toString
			val sEditCols = Math.max(60, nAnswerCols).toString
			//val xhtmlTextarea = SHtml.textarea(QuRequestVars.vars(sFieldName), QuRequestVars.vars(sFieldName) = _, "cols" -> sEditCols, "rows" -> sEditRows)
			result.fields += new QuFieldData(sFieldName, QuFieldKind.MultiLine, Map("cols" -> sEditCols, "rows" -> sEditRows))
			val xhtmlTextarea = Elem("qu", sFieldName, null, xml.TopScope)

			if (spec.bShowSolutions)
				(<table cellpadding="0" cellspacing="0">
						<tr><td>{xhtmlTextarea}</td>
							<td>{renderSolution(node)}</td></tr>
				 </table>)
			else
				xhtmlTextarea
		}
		catch {
			case ex => ex.printStackTrace; (<span>EXCEPTION</span>)
		}
	}
  
  private def renderSolution(node: Node): Node = {
    var sSolutionVars = ""
    var sSolutionAll = ""
    for (nodeSolution <- (node \\ "solution")) {
      val sVarName = (nodeSolution \ "@name").text.trim
      val sVarSolution = nodeSolution.text.trim
      if (!sVarName.isEmpty)
        sSolutionVars += sVarSolution + "\n"
      else
        sSolutionAll = sVarSolution
    }

    (<pre>{if (!sSolutionAll.isEmpty) sSolutionAll else sSolutionVars}</pre>)
  }
  
  private def renderSolution(node: Node, sFind: String): Node = {
    var sSolution = ""
    for (nodeSolution <- (node \\ "solution")) {
      val sVarName = (nodeSolution \ "@name").text.trim
      if (sVarName == sFind) {
	    sSolution = nodeSolution.text.trim
	  }
    }
    (<span style="font-family: monospace">{sSolution}</span>)
  }
  
  private def getScoreXhtml(ctx: QuRenderContext): NodeSeq = {
    val sFieldName = "ans_" + state.questionIndex

    //println("spec.scores = " + spec.scores)
    //if (spec.scores != null)
    //  println("spec.scores.contains(" + sFieldName + ") = " + spec.scores.contains(sFieldName)); 

    val xhtmlScores = new ArrayBuffer[Node]
    if (spec.scores != null && spec.scores.contains(sFieldName)) {
      val score = spec.scores(sFieldName);
      val bScores = spec.bShowScores
      val bValids = spec.bShowValidations
      val bEither = (bScores || bValids)
      if (bScores && score.asCorrect.length > 0)
        xhtmlScores += (<tr><td class="score-correct">Correct:</td><td>{joinVars(score.asCorrect, "variable", ", ", ctx)}</td></tr>)
      if (bScores && score.asSemiCorrect.length > 0)
        xhtmlScores += (<tr><td class="score-semi-correct">Semi-Correct:</td><td>{joinVars(score.asSemiCorrect, "variable", ", ", ctx)}</td></tr>)
      if (bEither && score.asMissing.length > 0)
        xhtmlScores += (<tr><td class="score-missing">Missing:</td><td>{joinVars(score.asMissing, "variable", ", ", ctx)}</td></tr>)
      if (bScores && score.asWrong.length > 0)
        xhtmlScores += (<tr><td class="score-wrong">Wrong:</td><td>{joinVars(score.asWrong, "variable", ", ", ctx)}</td></tr>)
      if (bEither && score.sErrors != null && !score.sErrors.isEmpty)
        xhtmlScores += (<tr><td class="score-error">Errors:</td><td><pre>{score.sErrors}</pre></td></tr>)
    }
    
    if (xhtmlScores.size > 0)
      (<table class="score">{xhtmlScores}</table>)
    else
      Text("")
  }
}
