package net.ellisw.quvault.server.scope

import scala.collection.mutable.ArrayBuffer

sealed class ScoreData {
  val asCorrect = new ArrayBuffer[String] 
  val asSemiCorrect = new ArrayBuffer[String] 
  val asMissing = new ArrayBuffer[String] 
  val asWrong = new ArrayBuffer[String]
  var sErrors: String = _
}
