/*
 * QuRendererTest.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.ellisw.quvault.server.scope

import org.specs._
import org.specs.runner.JUnit4

class QuRendererSpecTest extends JUnit4(QuRendererSpec)
object QuRendererSpec extends Specification {
	"A 'container' with 'text'" should {
		val xml = <container><text>Hello</text></container>
		val spec = new QuRenderSpec(xml, Map[String, String](), Map[String, ScoreData](), QuAnswerMode.Edit, false, false, false)
		val r = new QuRenderer(spec)
		val result = r.render

		"have a no fields" in { result.fields.length must be_==(0) }
	}

	"A questionMathlib-rhs" should {
		val xml = (
			<container>
				<questionContainer>
					<questionMathlib enumerated="1" title="Complex Expressions" answerMode="rhs">
						<entryMode mode="mathlib"/>
						<find>i,vC,vR</find>
					</questionMathlib>
					<instructions keywords="ee"/>
				</questionContainer>
			</container>)

		val spec = new QuRenderSpec(xml,
			Map[String, String](),
			Map[String, ScoreData](), QuAnswerMode.Edit, false, false, false)
		val r = new QuRenderer(spec)
		val result = r.render

		"have fields" in { result.fields.length must be_==(3) }
		"have fields with proper id's" in {
			result.fields(0).id must be_==("ans_1_i")
			result.fields(1).id must be_==("ans_1_vC")
			result.fields(2).id must be_==("ans_1_vR")
		}
		"have 'SingleLine' fields" in {
			result.fields(0).kind must be_==(QuFieldKind.SingleLine)
			result.fields(1).kind must be_==(QuFieldKind.SingleLine)
			result.fields(2).kind must be_==(QuFieldKind.SingleLine)
		}
	}

	"A questionMathlib-equation" should {
		val xml = (
			<container>
				<questionContainer>
					<questionMathlib enumerated="1" title="Complex Expressions" answerMode="equation">
						<entryMode mode="mathlib"/>
						<find>i,vC,vR</find>
					</questionMathlib>
					<instructions keywords="ee"/>
				</questionContainer>
			</container>)

		val spec = new QuRenderSpec(xml,
			Map[String, String](),
			Map[String, ScoreData](), QuAnswerMode.Edit, false, false, false)
		val r = new QuRenderer(spec)
		val result = r.render

		"have one field" in { result.fields.length must be_==(1) }
		"have field with proper id" in { result.fields(0).id must be_==("ans_1") }
		"have 'MultiLine' fields" in { result.fields(0).kind must be_==(QuFieldKind.MultiLine) }
	}
}
