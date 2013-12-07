package net.ellisw.quvault.vault;

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


public class PT_RCSeries implements IProblemTempl {
	public static void setupHeaders(List<ProblemHeader> list) {
		list.add(new ProblemHeader("PT_RCSeries", "RC Series Circuit", "QuVault Team", "series,resistance,capacitance"));
	}
	
	public boolean setInitialParams(Map<String, String> params) {
		return true;
	}

	public void setupParamSpecs(ProblemParamSpecs specs) {
		specs.addVar("R", "Resistor Value");
		specs.addVar("C", "Capacitor Values");
	}
	
	public void loadParamDefaults(ProblemParams params) {
		params.put("R", "sym");
		params.put("C", "sym");
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
		Map<String, String> params = container.fillParamMap();
		assert(params.containsKey("R"));
		assert(params.containsKey("C"));

		QuProblem problem = container.createProblem();
		problem.setTitle("Series RC Circuit");
		problem.setKeywords("series,resistance,capacitance");
		problem.createText().setHtml("<resource name='network'/>");

		Document doc = DocumentHelper.createDocument();
		Element network = doc.addElement("network");
		network.addElement("circle").addAttribute("label-left", "vin");
		network.addElement("resistor").addAttribute("dx", "+1").addAttribute("label-left", "R");
		network.addElement("line").addAttribute("dx", "+1");
		network.addElement("circle").addAttribute("label-right", "vout");
		network.addElement("move").addAttribute("dx", "-1");
		network.addElement("capacitor").addAttribute("dy", "-1").addAttribute("label-left", "C");
		network.addElement("circle").addAttribute("label-right", "gnd");
		problem.setResource("network", "network", doc.asXML());

		problem.setVar("dvout", "&Delta;v<sub>out</sub>", "change in output voltage");
		problem.setVar("R", "R", null);
		problem.setVar("C", "C", null);
		problem.setVar("vin", "vin", null);
		problem.setVar("vout", "vout", null);
		problem.setVar("dt", "dt", null);
		problem.setVar("tau", "&tau", "time constant");
		problem.setVar("HC", "H<sub>C</sub>(s)", "transfer function for the capacitor");
		problem.setVar("HR", "H<sub>R</sub>(s)", "transfer function for the resistor");
		problem.setVar("ZR", "Z<sub>R</sub>(s)", "impedance of the resistor");
		problem.setVar("ZC", "Z<sub>C</sub>(s)", "impedance of the capacitor");
		problem.setVar("HR", "H<sub>R</sub>(s)", "transfer function for the resistor");
		problem.setVar("VR", "V<sub>R</sub>(s)", "voltage drop across the resistor");
		problem.setVar("HC", "H<sub>C</sub>(s)", "transfer function for the capacitor");
		problem.setVar("VC", "V<sub>C</sub>(s)", "voltage drop across the capacitor");

		String sSolution;
		QuQuestionMathlib q;
		
		q = problem.createQuestionMathlib();
		q.setTitle("Numeric Solution");
		q.addFindVar("dvout");
		q.addGivenVar("R");
		q.addGivenVar("C");
		q.addGivenVar("vin");
		q.addGivenVar("vout");
		q.addGivenVar("dt");
		sSolution = "dvout = (vin - vout)/(R*C)*dt";
		q.setSolution(sSolution);

		q = problem.createQuestionMathlib();
		q.setTitle("Time Constant");
		q.addFindVar("tau");
		q.addGivenVar("R");
		q.addGivenVar("C");
		sSolution = "tau = R * C";
		q.setSolution(sSolution);

		q = problem.createQuestionMathlib();
		q.setTitle("Transfer function for the capacitor");
		q.addFindVar("HC");
		q.addGivenVar("R");
		q.addGivenVar("C");
		q.addGivenVar("s");
		sSolution = "HC = 1/(1 + R*C*s)";
		q.setSolution(sSolution);

		q = problem.createQuestionMathlib();
		q.setTitle("Transfer function for the resistor");
		q.addFindVar("HR");
		q.addGivenVar("R");
		q.addGivenVar("C");
		q.addGivenVar("s");
		sSolution = "HC = 1/(1 + R*C*s)";
		q.setSolution(sSolution);

		q = problem.createQuestionMathlib();
		q.setTitle("Everything in terms of s");
		q.addFindVar("ZR");
		q.addFindVar("ZC");
		q.addFindVar("HR");
		q.addFindVar("VR");
		q.addFindVar("HC");
		q.addFindVar("VC");
		q.addGivenVar("R");
		q.addGivenVar("C");
		q.addGivenVar("s");
		sSolution =
			"ZR = R;" +
			"ZC = 1/(s*C);" +
			"HR = ZR / (ZR + ZC);" +
			"VR = vin * HR;" +
			"HC = ZC / (ZR + ZC);" +
			"HC = 1/(1 + R*C*s);";
		q.setSolution(sSolution);
	}
}
