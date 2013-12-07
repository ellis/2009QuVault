package net.ellisw.quvault.server.scope

import scala.xml.Node

sealed class QuRenderSpec(
  val xml: Node,
  val answers: Map[String, String],
  val scores: Map[String, ScoreData],
  val mode: QuAnswerMode.Value,
  val bShowValidations: Boolean,
  val bShowScores: Boolean,
  val bShowSolutions: Boolean
)
