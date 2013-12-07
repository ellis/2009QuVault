package net.ellisw.quvault.lift

import scala.collection.mutable.ArrayBuffer
import scala.xml._

import net.ellisw.quvault.networkpainter.CircuitPainterSvg


class QuRenderer(spec: QuRenderSpec, val state: QuRenderState) {
  private val enumerationLevelTypes = List("1", "A", "a", "i")

  def this(spec: QuRenderSpec) = this(spec, new QuRenderState())
  
  def render: NodeSeq = {
    render(spec.xml.first, new QuRenderContext(null))
  }
  
  protected def render(node: Node, ctx: QuRenderContext): NodeSeq = {
    transform_container(node, ctx)
  }

  // Each object can have params, vars, resources
  // - Handle the hierarchical structures of params, vars, resources first
  // - process the node according to its type
  //   - process the nodes children
  //     - handle enumeration level, index, on/off
  
  private def handle_node(node: Node, ctx: QuRenderContext): Option[NodeSeq] = {
    // println("node: "+ node.label)
    val xhtml =
      node.label match {
        case "container" => Some(transform_container(node, new QuRenderContext(ctx)))
        case "text" => Some(transform_text(node, new QuRenderContext(ctx)))
        case "questionContainer" => Some(transform_questionContainer(node, new QuRenderContext(ctx)))
        case "questionMathlib" => Some(new QuRendererMathlib(spec, state).render(node, new QuRenderContext(ctx)))
        case "resource" => handle_resource(node, ctx); None
        case "variable" => handle_variable(node, ctx); None
        case _ => None
      }
    xhtml
  }
  
  protected def handle_children(parent: Node, ctx: QuRenderContext): NodeSeq = {
    var enumeration = false
    var enumerationIndex = 1
    
    val children = new ArrayBuffer[Node]()
    var enumerationChildren: ArrayBuffer[Node] = null
    
    def endEnumeration(children: ArrayBuffer[Node], enumerationChildren: ArrayBuffer[Node]) {
      state.enumerationLevel -= 1
      // println("-1 " + enumerationLevel)
      if (enumerationChildren.length > 1) {
        children += (<ol class="containerItems" start={enumerationIndex.toString} type={enumerationLevelTypes(state.enumerationLevel % 4)}>{
          for (c <- enumerationChildren) yield { (<li>{c}</li>) }
        }</ol>)
      }
      else {
        children ++= enumerationChildren;
      }
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
          // println("+1 " + enumerationLevel + " " + parent.label)
        }
      }

      val xhtml = handle_node(node, ctx)

      if (xhtml.isDefined) {
        if (enumerated != enumeration) {
          if (enumerated) {
            enumerationChildren = new ArrayBuffer[Node]()
          }
          else {
            endEnumeration(children, enumerationChildren)
            enumerationChildren = null
          }
          enumeration = enumerated
        }
        
        if (enumeration)
          enumerationChildren ++= xhtml.get
        else
          children ++= xhtml.get
      }
    }
    
    if (enumeration) {
      // println("end")
      endEnumeration(children, enumerationChildren)
      enumerationChildren = null
      enumeration = false
    }
    
    children
  }
  
  private def handle_resource(node: Node, ctx: QuRenderContext) {
    val name: String = (node \ "@name").text
    ctx.resources.+=((name, node))
  }

  private def handle_variable(node: Node, ctx: QuRenderContext) {
    val name: String = (node \ "@name").text
    ctx.variables.+=((name, node))
  }

  private def transform_container(node: Node, ctx: QuRenderContext): Node = {
    (<div class="container">{handle_children(node, ctx)}</div>)
  }
  
  private def transform_text(node: Node, ctx: QuRenderContext): Node = {
    val children = transform_text_sub(node.child, ctx)
    (<div class="text">{children}</div>)
  }
  
  private def transform_text_sub(nodes: NodeSeq, ctx: QuRenderContext): NodeSeq = {
    def update(node: Node): Node = node match {
      case Elem(_, "resource", _, _, _*) => transform_resource(node, ctx)
      case Elem(prefix @ _, label @ _, attributes @ _, scope @ _, child @ _*) => Elem(prefix, label, attributes, scope, child.map(update(_)): _*)
      case _ => node
    }
    
    for (child <- nodes) yield {
      update(child)
    }
  }
  
  private def transform_resource(node: Node, ctx: QuRenderContext): Node = {
      val name = (node \ "@name").text
      val resource = ctx.getResource(name)
      if (resource.isDefined) {
        val sType = (resource.get \ "@type").text
        sType match {
          case "network" =>
            val xmlNetwork = (resource.get \ "network")
            val sSvg = (new CircuitPainterSvg()).convertToSvg(xmlNetwork.toString, false)
            val xmlSvg = XML.loadString(sSvg)
            (<span>{xmlSvg}</span>)
          case "vartable" =>
            val asVars = resource.get.text.split(',').map(_.trim)
            (
              <table class="vartable">
                <tr><th>Symbol</th>{
                spec.mode match {
                  case QuAnswerMode.Blank => Text("")
                  case _ => (<th>Code</th>)
                }}<th>Description</th></tr>
                {
                  asVars.map(sVar => (
                    <tr>
                      <td>{getVarXhtml(sVar, "variable", ctx)}</td>{
                      spec.mode match {
                        case QuAnswerMode.Blank => Text("")
                        case _ => (<td class="vartable-code">{sVar}</td>)
                      }}<td>{ctx.getVariableDescriptionXhtml(sVar)}</td>
                    </tr>
                  ))
                }
              </table>
            )
          case _ =>
            (<span>RESOURCE TYPE UNKNOWN: <pre>{node.toString}</pre></span>)
        }
      }
      else
        (<span>RESOURCE ERROR: <pre>{node.toString}</pre></span>)
  }
  
  private def transform_questionContainer(node: Node, ctx: QuRenderContext): Node = {
    val title = (node \ "@title").text
    (<div class="questionContainer">{
      title match {
        case "" => handle_children(node, ctx)
        case _ => (<div class="questionContainerTitle">{title}</div>) ++ handle_children(node, ctx) 
      }
    }</div>)
  }
  
  
  protected def joinStringsAsNodes(list: Seq[String], sClass: String, sSeparator: String): NodeSeq = {
    var xml = new ArrayBuffer[Node]()
    var i = 0
    while (i < list.length) {
      if (i > 0)
        xml += Text(sSeparator)
      xml += (<span class={sClass}>{list(i)}</span>)
      i += 1
    }
    xml
  }
  
  protected def joinVars(list: Seq[String], sClass: String, sSeparator: String, ctx: QuRenderContext): NodeSeq = {
    var xml = new ArrayBuffer[Node]()
    var i = 0
    while (i < list.length) {
      if (i > 0)
        xml += Text(sSeparator)
      xml += getVarXhtml(list(i), sClass, ctx)
      i += 1
    }
    xml
  }

  protected def getVarXhtml(name: String, sClass: String, ctx: QuRenderContext): Node = {
    val nameXhtml = ctx.getVariableNameXhtml(name)
    (<span class={sClass}>{nameXhtml}</span>)
  }
  
}
