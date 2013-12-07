package net.ellisw.quvault.lift.snippet

object SampleData {
  def getQuestionXml() = {
    val xml = (<container>
	<text><h1>Homework 1</h1></text>
	<container>
		<text><h2>Part I</h2></text>
		<questionContainer enumerated="true" title="Series Resistance" source="local" uri="PT_SeriesElements(mode=RS)">
			<params>rab=1</params>
			<variable name="rab">
				<xhtml>r<sub>ab</sub></xhtml>
				<description>resistance between nodes <i>a</i> and <i>b</i></description>
			</variable>
			<resource name="network" type="network"><network><resistor dx="+1"/></network></resource>
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
}
