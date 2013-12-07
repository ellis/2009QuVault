package net.ellisw.quvault

import scala.xml._
import scala.collection.mutable.ArrayBuffer

/**
 * Hello world!
 *
 */
object App {
  private var enumerationLevel = 0;
  private val enumerationLevelTypes = List("1", "A", "a", "i")
  private var questionIndex = 0;
  
  def main(args: Array[String]) {
    val xml = getHwnodes
    val xhtmlContainer = transformSeq(xml)
    val xhtml = (<html><body>{xhtmlContainer}</body></html>)
    println((new PrettyPrinter(120, 4)).format(xhtml))
  }

  private def getHwnodes() = {
    val xml = (<container>
	<text><h1>Homework 1</h1></text>
	<container>
		<text><h2>Part I</h2></text>
		<questionContainer enumerated="true" title="Series Resistance" source="local" uri="PT_SeriesElements(mode=RS)">
			<params>rab=1</params>
			<variable name="rab">
				<html>r<sub>ab</sub></html>
				<description>resistance between nodes <i>a</i> and <i>b</i></description>
			</variable>
			<resource name="network" kind="network">...</resource>
			<text>Here's a network: <resource name="network"/></text>
			<questionMathlib enumerated="true" answerMode="OneLinePerVariable">
				<find>rab,rbc,rac</find>
				<given>E,R1,R2,R3</given>
				<solution>...</solution>
				<answer variable="rab">R1</answer>
				<score variable="rab" status="correct">1</score>
				<score variable="rbc" status="wrong">0</score>
				<score variable="rac" status="semicorrect">1</score>
			</questionMathlib>
			<questionMathlib enumerated="true" answerMode="OneLinePerVariable">
				<find>rab,rbc,rac</find>
				<given>E,R1,R2,R3</given>
				<solution>...</solution>
				<answer variable="rab">R1</answer>
				<score variable="rab" status="correct">1</score>
				<score variable="rbc" status="wrong">0</score>
				<score variable="rac" status="semicorrect">1</score>
			</questionMathlib>
		</questionContainer>
	</container>
</container>)
    
    xml
  }

  // Each object can have params, vars, resources
  // - Handle the hierarchical structures of params, vars, resources first
  // - process the node according to its type
  //   - process the nodes children
  //     - handle enumeration level, index, on/off
  
  private def transformSeq(in: NodeSeq): Node = {
    transform_container(in.first)
  }
  
  private def handle_node(node: Node): Option[Node] = {
    // println("node: "+ node.label)
    val xhtml =
      node.label match {
        case "container" => Some(transform_container(node))
        case "text" => Some(transform_text(node))
        case "questionContainer" => Some(transform_questionContainer(node))
        case "questionMathlib" => Some(transform_questionMathlib(node))
        case _ => None
      }
    xhtml
  }
  
  private def handle_children(parent: Node): NodeSeq = {
    var enumeration = false
    var enumerationIndex = 1
    
    val children = new ArrayBuffer[Node]()
    var enumerationChildren: ArrayBuffer[Node] = null
    
    def endEnumeration(children: ArrayBuffer[Node], enumerationChildren: ArrayBuffer[Node]) {
          enumerationLevel -= 1
          // println("-1 " + enumerationLevel)
          if (enumerationChildren.length > 1) {
            children += (<ol class="containerItems" start={enumerationIndex.toString} type={enumerationLevelTypes(enumerationLevel % 4)}>{
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
          enumerationLevel += 1
          // println("+1 " + enumerationLevel + " " + parent.label)
        }
      }

      val xhtml = handle_node(node)

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
          enumerationChildren += xhtml.get
        else
          children += xhtml.get
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

  private def transform_container(node: Node): Node = {
    (<div class="container">{handle_children(node)}</div>)
  }
  
  private def transform_text(node: Node): Node = {
    (<div class="text">{node.child}</div>)
  }
  
  private def transform_questionContainer(node: Node): Node = {
    val title = (node \ "@title").text
    (<div class="questionContainer">{
      title match {
        case "" => handle_children(node)
        case _ => (<div class="questionContainerTitle">{title}</div>) ++ handle_children(node) 
      }
    }</div>)
  }
  
  private def transform_questionMathlib(node: Node): Node = {
    val questions = new ArrayBuffer[Node]()

    val givens = (node \ "given").text.split(',')
    if (givens.length > 0) {
      val x = for (given <- givens) yield {
        (<span class="variable">{given.trim}</span>) :: Text(", ") :: Nil
      }.flatMap(_.toList)
      questions += (<div>Given: {x}</div>)
    }

    val finds = (node \ "find").text.split(',')
    if (finds.length > 0) {
      questionIndex += 1
      questions += (<table class="questionMathlibOneVarPerLine" cellpadding="0" cellspacing="0">{
          for (find <- finds) yield {
            var name = "ans_" + questionIndex + "_" + find.trim
            (<tr><td>{find.trim}&nbsp;=&nbsp;</td><td><input name={name} type='text' value=''/></td></tr>)
          }
        }</table>)
    }
  
    (<div class="questionMathlib">{handle_children(node) :: questions :: Nil}</div>)
  }
}
