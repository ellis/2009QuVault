package net.ellisw.quvault.lift

import scala.collection.mutable.ArrayBuffer
import scala.xml._

import net.liftweb.http.SHtml

import net.ellisw.quvault.lift._
import net.ellisw.quvault.parser.MathlibScorer


class QuScorerMathlib(spec: QuRenderSpec, state: QuRenderState, scores: scala.collection.mutable.Map[String, ScoreData]) extends QuScorer(spec, state, scores) {
  
  override protected def score(node: Node, ctx: QuRenderContext) {
    val sAnswerMode = (node \ "@answerMode").text
    val finds = (node \ "find").text.split(',')
    val givens = (node \ "given").text.split(',')

    if (finds.length > 0) {
      state.questionIndex += 1
      sAnswerMode match {
        case "rhs" => handle_rhs(node, givens, finds, ctx)
        case "equation" => handle_function(node, givens, finds, ctx)
        case "function" => handle_function(node, givens, finds, ctx)
      }
    }
  
    handle_children(node, ctx)
  }
  
  private def handle_rhs(node: Node, givens: Array[String], finds: Array[String], ctx: QuRenderContext) {
    val mapSolutions = scala.collection.mutable.Map[String, String]()
    for (nodeSolution <- (node \\ "solution")) {
      val sVarName = (nodeSolution \ "@name").text.trim
      val sSolution = nodeSolution.text.trim
      mapSolutions(sVarName) = sSolution
    }
    
    val data = new ScoreData
    data.sErrors = "";
    
    for (sFind <- finds) {
      val sFieldName = "ans_" + state.questionIndex + "_" + sFind
      val sAnswer = QuRequestVars.vars(sFieldName)
      if (sAnswer != null && !sAnswer.isEmpty) {
        var sSolution = ""
        if (mapSolutions.contains(""))
          sSolution += mapSolutions("") + "\n"
        if (mapSolutions.contains(sFind))
          sSolution += mapSolutions(sFind)
      
        val scorer = new MathlibScorer
        val anFindMarks = scorer.score(
          java.util.Arrays.asList(givens: _*),
          java.util.Arrays.asList(sFind),
          sSolution, sFind + " = " + sAnswer)

        (anFindMarks(0) match {
          case 0 => data.asCorrect
          case 1 => data.asSemiCorrect
          case 2 => data.asMissing
          case 3 => data.asWrong
        }) += sFind

        val sErrors = scorer.getErrors();
        if (sErrors != null)
          data.sErrors += sErrors;
      }
      else {
        data.asMissing += sFind
      }
    }

    val sQuestionName = "ans_" + state.questionIndex
    scores(sQuestionName) = data
  }
  
  private def handle_function(node: Node, givens: Array[String], finds: Array[String], ctx: QuRenderContext) {
    val sFieldName = "ans_" + state.questionIndex
    val sSolution = (node \ "solution").text
    
    val sAnswer = QuRequestVars.vars(sFieldName)
    scores(sFieldName) = checkAnswers(sSolution, sAnswer, givens, finds)
  }
  
  private def checkAnswers(sSolution: String, sAnswer: String, givens: Array[String], finds: Array[String]): ScoreData = {
    val scorer = new MathlibScorer
    val anFindMarks = scorer.score(
      java.util.Arrays.asList(givens: _*),
      java.util.Arrays.asList(finds: _*),
      sSolution, sAnswer)

    val data = new ScoreData
    for (i <- 0 until anFindMarks.length) {
      (anFindMarks(i) match {
        case 0 => data.asCorrect
        case 1 => data.asSemiCorrect
        case 2 => data.asMissing
        case 3 => data.asWrong
      }) += finds(i)
    }
    data.sErrors = scorer.getErrors();
    
    data
  }
}
