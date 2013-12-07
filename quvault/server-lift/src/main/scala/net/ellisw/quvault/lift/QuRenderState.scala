package net.ellisw.quvault.lift

import scala.collection.mutable.ArrayBuffer


class QuRenderState {
  var enumerationLevel = 0;
  var questionIndex = 0;
  val answers = new ArrayBuffer[String]
}
