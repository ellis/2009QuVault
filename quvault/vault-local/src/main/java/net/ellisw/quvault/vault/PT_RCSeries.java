package net.ellisw.quvault.vault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ellisw.network.Direction;
import net.ellisw.network.DisplayCurrents;
import net.ellisw.network.DisplayNodes;
import net.ellisw.network.DisplayParams;
import net.ellisw.network.Network;
import net.ellisw.network.Solver;
import net.ellisw.quvault.vault.IProblemTempl;
import net.ellisw.quvault.vault.ProblemHeader;
import net.ellisw.quvault.vault.ProblemParamSpecs;
import net.ellisw.quvault.vault.QuContainer;
import net.ellisw.quvault.vault.QuQuestionMathlib;


public class PT_RCSeries implements IProblemTempl {
	private Map<String, String> params = new HashMap<String, String>();
	private boolean bNeedNewNetwork;
	private Network network;
	//private LinkedHashMap<String, Expression> mapSolutions;

	
	public static void setupHeaders(List<ProblemHeader> list) {
		list.add(new ProblemHeader("PT_RCSeries", "RC Series Circuit", "QuVault Team", "series,resistance,capacitance"));
	}
	
	@Override
	public void setParams(Map<String, String> params) {
		System.out.println("setParams: " + params);
		if (params != null && updateMapFromMap(this.params, params))
			bNeedNewNetwork = true;
	}

	private boolean updateMapFromMap(Map<String, String> dest, Map<String, String> source) {
		boolean bChanged = false;
		for (Entry<String, String> entry : source.entrySet()) {
			String sName = entry.getKey();
			String sValue = entry.getValue();
			if (!dest.containsKey(sName) || !dest.get(sName).equals(sValue)) {
				dest.put(entry.getKey(), entry.getValue());
				bChanged = true;
			}
		}
		return bChanged;
	}
	
	@Override
	public boolean setInitialParams(Map<String, String> params) {
		return true;
	}

	@Override
	public boolean setupParamSpecs(int level, ProblemParamSpecs specs) {
		specs.addVar("R", "Resistor Value");
		specs.addVar("C", "Capacitor Values");
		return false;
	}
	
	@Override
	public boolean loadParamDefaults(int level, Map<String, String> params) {
		params.put("R", "sym");
		params.put("C", "sym");
		return false;
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
		assert(params.containsKey("R"));
		assert(params.containsKey("C"));

		container.fillParamMap(params);

		QuProblemElec problem = container.createProblemElec();
		problem.setTitle("Series RC Circuit");
		problem.setKeywords("series,resistance,capacitance");
		problem.createText().setHtml("<resource name='network'/>");

		updateNetwork();
		DisplayParams display = new DisplayParams();
		display.displayCurrents = DisplayCurrents.All;
		display.displayNodes = DisplayNodes.None;

		List<String> asGivens = Arrays.asList(new String[] { "V", "R", "C", "w" });
		Solver solver = network.getFindables(asGivens);
		qPhasors1(problem, solver, asGivens);
		qPhasors2(problem, solver, asGivens);
		
		/*
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
		q.setSolution(null, sSolution);

		q = problem.createQuestionMathlib();
		q.setTitle("Time Constant");
		q.addFindVar("tau");
		q.addGivenVar("R");
		q.addGivenVar("C");
		sSolution = "tau = R * C";
		q.setSolution(null, sSolution);

		q = problem.createQuestionMathlib();
		q.setTitle("Transfer function for the capacitor");
		q.addFindVar("HC");
		q.addGivenVar("R");
		q.addGivenVar("C");
		q.addGivenVar("s");
		sSolution = "HC = 1/(1 + R*C*s)";
		q.setSolution(null, sSolution);

		q = problem.createQuestionMathlib();
		q.setTitle("Transfer function for the resistor");
		q.addFindVar("HR");
		q.addGivenVar("R");
		q.addGivenVar("C");
		q.addGivenVar("s");
		sSolution = "HC = 1/(1 + R*C*s)";
		q.setSolution(null, sSolution);

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
		q.setSolution(null, sSolution);
		*/

		problem.setResource("network", "network", network.getDisplayXml(display));
	}
	
	private QuQuestionMathlib qPhasors1(QuProblemElec problem, Solver solver, List<String> asGivens) {
		QuQuestionMathlib q;

		q = problem.createQuestionMathlib();
		q.setAnswerMode(QuQuestionMathlib.AnswerMode.EquationBlock);
		q.setTitle("Complex Calculations");
		q.createText().setHtml(
				"Use complex impedances to calculate the following values.");

		q.setGivens(asGivens);
		
		List<String> asFinds = qPhasors1_getFindables(solver);
		q.setFinds(asFinds);
		
		String sSolution = solver.getSolution(asFinds);
		q.setSolution(null, sSolution);

		return q;
	}
	
	private List<String> qPhasors1_getFindables(Solver solver) {
		List<String> asFindables = new ArrayList<String>();

		for (Solver.Item item : solver.exprs) {
			if (!item.bEnabled || item.bGiven || !item.bHave)
				continue;
			
			String sVarName = item.var.getName();
			if (!sVarName.startsWith("iZ") && !sVarName.startsWith("pZ") && !sVarName.endsWith("'"))
				asFindables.add(sVarName);
		}
		
		return asFindables;
	}
	
	private QuQuestionMathlib qPhasors2(QuProblemElec problem, Solver solver, List<String> asGivens) {
		QuQuestionMathlib q;

		q = problem.createQuestionMathlib();
		q.setAnswerMode(QuQuestionMathlib.AnswerMode.SingleLineRhsPerVar);
		q.setTitle("Complex Expressions");
		q.createText().setHtml(
				"Use complex impedances to find expressions for the following variables.");

		q.setGivens(asGivens);
		
		List<String> asFinds = Arrays.asList(new String[] { "i", "vC", "vR" });
		q.setFinds(asFinds);
		
		for (String sVarName : asFinds) {
			String sSolution = solver.getSingleExpressionFor(sVarName);
			q.setSolution(sVarName, sSolution);
		}

		return q;
	}

	private void updateNetwork() {
		System.out.println("updateNetwork: " + params);
		if (bNeedNewNetwork) {
			bNeedNewNetwork = false;
			
			network = new Network();
			network.addNode("a", 0, 1);
			network.addNode("b", 1, 1);
			network.addNode("0", 1, 0);
			
			network.addElem("V", "0", "a", 0, 0, Direction.Up);
			network.addElem("R", "a", "b", 0, 1, Direction.Right);
			network.addElem("C", "b", "0", 1, 1, Direction.Down);

			Map<String, String> translations = new HashMap<String, String>();
			translations.put("va", "V");
			translations.put("vab", "vR");
			translations.put("vb", "vC");
			translations.put("iV", "i");
			translations.put("iR", "i");
			translations.put("iC", "i");
			translations.put("iZCR", "i");
			network.createVars(translations);
		}
	}
}
