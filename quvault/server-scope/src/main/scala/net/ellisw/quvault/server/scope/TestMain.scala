/*
 * TestMain.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.ellisw.quvault.server.scope

import scala.xml._

object TestMain {

	/**
	 * @param args the command line arguments
	 */
	def main(args: Array[String]) :Unit = {
		var xml: Node = null
		if (args.length > 0) {
			xml = XML.loadString(args(0))
		}
		else {
			xml = (
				<container>
					<questionContainer>
						<questionMathlib enumerated="1" title="Complex Expressions" answerMode="equation">
							<entryMode mode="mathlib"/>
							<find>i,vC,vR</find>
						</questionMathlib>
						<instructions keywords="ee"/>
					</questionContainer>
				</container>)
		}

		val spec = new QuRenderSpec(xml, Map[String, String](), Map[String, ScoreData](), QuAnswerMode.Edit, false, false, false)
		val r = new QuRenderer(spec)
		val result = r.render

		println("XML:")
		println(result.xhtml.toString)
		println()

		println("INPUTS:")
		for (d <- result.fields) {
			println("\t" + d.id + ": " + d.kind + ", " + d.attrs)
		}
		println()

		println("INSTRUCTIONS:")
		val xhtmlInstructions = new QuRendererInstructions().render(xml)
		println(xhtmlInstructions)
		println()
	}

}
