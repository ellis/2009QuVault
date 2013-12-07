package net.ellisw.quvault.core;

import scala.collection.mutable.ArrayBuffer

class QuContainer(parent: QuObject) extends QuObject(parent) {
  val children = new ArrayBuffer[QuObject]();

  def createContainer(): QuContainer = {
    val o = new QuContainer(this)
    children += o
    o 
  }

  def createTextHtml(html: String): QuTextHtml = {
    val o = new QuTextHtml(html, this)
    children += o
    o 
  }
/*
	public QuProblem createProblem() {
		QuProblem o = new QuProblem(this);
		children.add(o);
		return o; 
	}
	
	public QuQuestionMathlib createQuestionMathlib() {
		QuQuestionMathlib o = new QuQuestionMathlib(this);
		children.add(o);
		return o; 
	}
	
	public QuText createText() {
		QuText o = new QuText(this);
		children.add(o);
		return o; 
	}
 */
}
