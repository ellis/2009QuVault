package net.ellisw.quvault.vault;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import net.ellisw.quvault.core.IProblemTempl;
import net.ellisw.quvault.core.ProblemData;
import net.ellisw.quvault.core.ProblemHeader;
import net.ellisw.quvault.core.ProblemParamSpecs;
import net.ellisw.quvault.core.ProblemParams;
import net.ellisw.quvault.core.QuContainer;
import net.ellisw.quvault.core.QuProblem;
import net.ellisw.quvault.core.QuQuestionMathlib;
import net.ellisw.quvault.core.QuestionMatlibData;


public class PT_ThreeResistorCircuit implements IProblemTempl {
	public static void setupHeaders(List<ProblemHeader> list) {
		list.add(new ProblemHeader("PT_ThreeResistorCircuit", "Three-Resistor Circuit", "QuVault Team", "series,parallel,resistance"));
	}
	
	public boolean setInitialParams(Map<String, String> params) {
		return true;
	}

	public void setupParamSpecs(ProblemParamSpecs specs) {
		specs.addVar("rab", "Find r<sub>ab</sub>");
		specs.addVar("rbc", "Find r<sub>bc</sub>");
		specs.addVar("rac", "Find r<sub>ac</sub>");
		specs.addVar("vab", "Find v<sub>ab</sub>");
		specs.addVar("vbc", "Find v<sub>bc</sub>");
		specs.addVar("vac", "Find v<sub>ac</sub>");
		specs.addVar("iR1", "Find i<sub>R1</sub>");
		specs.addVar("iR2", "Find i<sub>R2</sub>");
		specs.addVar("iR3", "Find i<sub>R3</sub>");
		specs.addVar("iE",  "Find i<sub>E</sub>");
		specs.addVar("pR1", "Find p<sub>R1</sub>");
		specs.addVar("pR2", "Find p<sub>R2</sub>");
		specs.addVar("pR3", "Find p<sub>R3</sub>");
		specs.addVar("pE",  "Find p<sub>E</sub>");
	}
	
	public void loadParamDefaults(ProblemParams params) {
		params.put("rab", "1");
		params.put("rbc", "1");
		params.put("rac", "1");
		params.put("vab", "1");
		params.put("vbc", "1");
		params.put("vac", "1");
		params.put("iR1", "1");
		params.put("iR2", "1");
		params.put("iR3", "1");
		params.put("iE", "1");
		params.put("pR1", "1");
		params.put("pR2", "1");
		params.put("pR3", "1");
		params.put("pE", "1");
	}
	
	public void loadRandomParams(Map<String, String> params) {
		/*
		int iDecade0 = rnd.nextInt(5) + 2;
		double[] E12 = {		  	 
			1.0, 1.2, 1.5,
			1.8, 2.2, 2.7,
			3.3, 3.9, 4.7,
			5.6, 6.8, 8.2
		};
		String sValues = "";
		for (int i = 0; i < nCount; i++) {
			if (i > 0)
				sValues += ",";
			
			if (values[i] == -1) {
				sValues += "sym";
			}
			else {
				if (values[i] == 0) {
					int iDecade = rnd.nextInt(3) - 1 + iDecade0;
					int iFactor = rnd.nextInt(E12.length);
					double nValue = E12[iFactor] * Math.pow(10, iDecade);
					values[i] = nValue;
				}
				sValues += Double.toString(values[i]);
			}
		}
		*/
	}

	public void setupProblemData(QuContainer container) {
		Map<String, String> params = new HashMap<String, String>();
		container.fillParamMap(params);
		
		assert(params.containsKey("R"));
		assert(params.containsKey("C"));
		
		QuProblem problem = container.createProblem();
		problem.setTitle("Three-Resistor Circuit");
		problem.setKeywords("series,parallel,resistance");
		problem.createText().setHtml("<resource name='network'/>");

		Document doc = DocumentHelper.createDocument();
		Element network = doc.addElement("network");
		network.addElement("circle").addAttribute("label-left", "b");
		network.addElement("source").addAttribute("dy", "+1").addAttribute("label-left", "E");
		network.addElement("circle").addAttribute("label-left", "a");
		network.addElement("resistor").addAttribute("dx", "+1").addAttribute("label-left", "R1");
		network.addElement("circle").addAttribute("label-left", "c");
		network.addElement("line").addAttribute("dx", "+1");
		network.addElement("circle").addAttribute("label-left", "e");
		network.addElement("resistor").addAttribute("dx", "+1").addAttribute("label-left", "R1");
		network.addElement("circle").addAttribute("label-right", "vout");
		network.addElement("move").addAttribute("dx", "-1");
		network.addElement("capacitor").addAttribute("dy", "-1").addAttribute("label-left", "C");
		network.addElement("circle").addAttribute("label-right", "gnd");
		problem.setResource("network", "network", doc.asXML());

		problem.setVar("E");
		problem.setVar("R1");
		problem.setVar("R2");
		problem.setVar("R3");
		problem.setVar("rab", "r<sub>ab</sub>", "resistance between nodes <i>a</i> and <i>b</i>");
		problem.setVar("rbc", "r<sub>bc</sub>", "resistance between nodes <i>b</i> and <i>c</i>");
		problem.setVar("rac", "r<sub>ac</sub>", "resistance between nodes <i>a</i> and <i>c</i>");
		problem.setVar("vab", "v<sub>ab</sub>", "voltage drop from node <i>a</i> to <i>b</i>");
		problem.setVar("vbc", "v<sub>bc</sub>", "voltage drop from node <i>b</i> to <i>c</i>");
		problem.setVar("vac", "v<sub>ac</sub>", "voltage drop from node <i>a</i> to <i>c</i>");
		problem.setVar("iR1", "i<sub>R1</sub>", "current thru R1");
		problem.setVar("iR2", "i<sub>R2</sub>", "current thru R2");
		problem.setVar("iR3", "i<sub>R3</sub>", "current thru R3");
		problem.setVar("iE",  "i<sub>E</sub>",  "current thru E");
		problem.setVar("pR1", "p<sub>R1</sub>", "power dissipated by R1");
		problem.setVar("pR2", "p<sub>R2</sub>", "power dissipated by R2");
		problem.setVar("pR3", "p<sub>R3</sub>", "power dissipated by R3");
		problem.setVar("pE",  "p<sub>E</sub>",  "power dissipated by E");

		String sSolution = "";
		sSolution += "rab = R1; rbc = (1/R2 + 1/R3)^-1; rac = rab + rbc; ";
		sSolution += "vab = E * rab / rac; vbc = E * rbc / rac; vac = E; ";
		sSolution += "iR1 = vab / rab; iR2 = vbc / R2; iR3 = vbc / R3; iE = iR1; ";
		sSolution += "pR1 = iR1 * vR1; pR2 = iR2 * vR2; pR3 = iR3 * vR3; pE = iE * E; ";
		
		QuQuestionMathlib q;
		q = problem.createQuestionMathlib();
		q.addGivenVar("E");
		q.addGivenVar("R1");
		q.addGivenVar("R2");
		q.addGivenVar("R3");
		if (paramIsTrue(params, "rab")) q.addFindVar("rab");
		if (paramIsTrue(params, "rbc")) q.addFindVar("rbc");
		if (paramIsTrue(params, "rac")) q.addFindVar("rac");
		if (paramIsTrue(params, "vab")) q.addFindVar("vab");
		if (paramIsTrue(params, "vbc")) q.addFindVar("vbc");
		if (paramIsTrue(params, "vac")) q.addFindVar("vac");
		if (paramIsTrue(params, "iR1")) q.addFindVar("iR1");
		if (paramIsTrue(params, "iR2")) q.addFindVar("iR2");
		if (paramIsTrue(params, "iR3")) q.addFindVar("iR3");
		if (paramIsTrue(params, "iE"))  q.addFindVar("iE");
		if (paramIsTrue(params, "pR1")) q.addFindVar("pR1");
		if (paramIsTrue(params, "pR2")) q.addFindVar("pR2");
		if (paramIsTrue(params, "pR3")) q.addFindVar("pR3");
		if (paramIsTrue(params, "pE"))  q.addFindVar("pE");
		q.setSolution(sSolution);
	}
	
	private boolean paramIsTrue(Map<String, String> params, String name) {
		if (params.containsKey(name)) {
			String value = params.get(name);
			if (value != null) {
				value = value.toLowerCase();
				if (value.equals("1"))
					return true;
			}
		}
		return false;
	}
}
