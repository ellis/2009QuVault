package net.ellisw.quvault.server.scope

import scala.collection.mutable.ArrayBuffer
import scala.xml._


class QuScorer(spec: QuRenderSpec, val state: QuRenderState, val scores: scala.collection.mutable.Map[String, ScoreData]) {
  private val enumerationLevelTypes = List("1", "A", "a", "i")

  def this(spec: QuRenderSpec) = this(spec, new QuRenderState(), scala.collection.mutable.Map[String, ScoreData]())
  
  def score {
    score(spec.xml.first, new QuRenderContext(null))
  }
  
  protected def score(node: Node, ctx: QuRenderContext) {
    handle_container(node, ctx)
  }

  private def handle_container(node: Node, ctx: QuRenderContext) {
    handle_children(node, ctx)
  }
  
  private def handle_node(node: Node, ctx: QuRenderContext): Boolean = {
    node.label match {
      case "container" => handle_container(node, new QuRenderContext(ctx)); true
      case "questionContainer" => transform_questionContainer(node, new QuRenderContext(ctx)); true
      case "questionMathlib" => new QuScorerMathlib(spec, state, scores).score(node, new QuRenderContext(ctx)); true
      case _ => false
    }
  }
  
  protected def handle_children(parent: Node, ctx: QuRenderContext) {
    var enumeration = false
    var enumerationIndex = 1
    
    def endEnumeration {
      state.enumerationLevel -= 1
      println("-1 " + state.enumerationLevel)
      enumeration = false
    }
    
    for (node <- parent.child) {
      val enumerated = (node \ "@enumerated").text.toLowerCase match {
        case "true" => true
        case "1" => true
        case _ => false
      }

      if (enumerated != enumeration) {
        if (enumerated) {
          state.enumerationLevel += 1
          println("+1 " + state.enumerationLevel + " " + parent.label)
        }
      }

      if (handle_node(node, ctx)) {
        if (enumerated != enumeration) {
          if (!enumerated)
            endEnumeration
          enumeration = enumerated
        }
      }
    }
    
    if (enumeration) {
      println("end")
      endEnumeration
      enumeration = false
    }
  }
  
  private def transform_questionContainer(node: Node, ctx: QuRenderContext) {
    handle_children(node, ctx)
  }
}
