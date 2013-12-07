package net.ellisw.quvault.server.scope

import scala.xml._

  class QuRenderContext(val parent: QuRenderContext) {
    val resources = scala.collection.mutable.Map[String, Node]()
    val variables = scala.collection.mutable.Map[String, Node]()
    
    def getResource(name: String): Option[Node] = {
      val entry = resources.get(name)
      if (entry.isDefined) {
        entry
      }
      else if (parent != null)
        parent.getResource(name)
      else
        None
    }
    
    def getVariable(name: String): Option[Node] = {
      val entry = variables.get(name)
      if (entry.isDefined) {
        entry
      }
      else if (parent != null)
        parent.getVariable(name)
      else
        None
    }
    
    def getVariableNameXhtml(name: String): NodeSeq = {
      val entry = getVariable(name)
      if (entry.isDefined) {
        val nodeNameXhtml = entry.get \ "nameXhtml"
        val attrNameXhtml = entry.get \ "@nameXhtml"
        if (!nodeNameXhtml.isEmpty)
          nodeNameXhtml.first.child
        else if (!attrNameXhtml.isEmpty)
          XML.loadString("<root>" + attrNameXhtml.text + "</root>").child
        else
          Text(name)
      }
      else
        Text(name)
    }
    
    def getVariableDescriptionXhtml(name: String): NodeSeq = {
      val entry = getVariable(name)
      if (entry.isDefined) {
        val sDescription = entry.get.text.trim
        XML.loadString("<root>" + sDescription + "</root>").child
      }
      else
        Text("")
    }
  }
