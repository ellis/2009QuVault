package net.ellisw.quvault.lift

import scala.xml.Node

object QuAnswerMode extends Enumeration {
  val Blank, Answer, Edit, Solution = Value
} 

class QuRenderSpec(
  val xml: Node,
  val answers: Map[String, String], // REFACTOR: this value isn't currently used -- but we should use it instead of QuRequestVars.answers in the QuRenderer* classes
  val scores: scala.collection.Map[String, ScoreData],
  val mode: QuAnswerMode.Value,
  val bShowValidations: Boolean,
  val bShowScores: Boolean,
  val bShowSolutions: Boolean
)
