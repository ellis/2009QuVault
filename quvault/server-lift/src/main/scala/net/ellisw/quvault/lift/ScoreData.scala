package net.ellisw.quvault.lift

import scala.collection.mutable.ArrayBuffer

class ScoreData {
  val asCorrect = new ArrayBuffer[String] 
  val asSemiCorrect = new ArrayBuffer[String] 
  val asMissing = new ArrayBuffer[String] 
  val asWrong = new ArrayBuffer[String]
  var sErrors: String = _
}
