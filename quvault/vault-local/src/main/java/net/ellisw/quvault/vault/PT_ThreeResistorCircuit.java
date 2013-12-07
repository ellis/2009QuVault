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
import net.ellisw.network.Var;
import net.ellisw.quvault.vault.IProblemTempl;
import net.ellisw.quvault.vault.ProblemHeader;
import net.ellisw.quvault.vault.ProblemParamSpecs;
import net.ellisw.quvault.vault.QuContainer;
import net.ellisw.quvault.vault.QuQuestionMathlib;


public class PT_ThreeResistorCircuit implements IProblemTempl {
	private Map<String, String> params = new HashMap<String, String>();
	private boolean bNeedNewNetwork;
	private Network network;

	
	public static void setupHeaders(List<ProblemHeader> list) {
		list.add(new ProblemHeader("PT_ThreeResistorCircuit", "Three-Resistor Circuit", "QuVault Team", "series,parallel,resistance"));
	}
	
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
	
	public boolean setInitialParams(Map<String, String> params) {
		return true;
	}
	
	public boolean setupParamSpecs(int iLevel, ProblemParamSpecs specs) {
		switch (iLevel) {
		case 0:
			specs.addOption("source", "Source")
			.addValue("V", "Voltage")
			.addValue("I", "Current");
			specs.addOption("structure", "Network Structure")
			.addValue("sp", "R1+(R2|R3)")
			.addValue("ps", "R1|(R2+R3)");
			break;
		case 1:
			updateNetwork();
			
			String[] asVarNames = network.getVarNamesSorted();
			
			ProblemParamSpecs.VarChecklist listFind = specs.addChecklist("finds", "Find");
			ProblemParamSpecs.VarChecklist listGiven = specs.addChecklist("givens", "Given");
			for (String sVarName : asVarNames) {
				listFind.addValue(sVarName, sVarName);
				listGiven.addValue(sVarName, sVarName);
			}
			break;
		default:
			return false;
		}
		
		return true;
	}
	
	public boolean loadParamDefaults(int iLevel, Map<String, String> defaults) {
		switch (iLevel) {
		case 0:
			defaults.put("source", "V");
			defaults.put("structure", "sp");
			break;
		case 1:
			updateNetwork();

			String[] asElemNames = network.getElemNamesSorted();
			String sGivens = "";
			for (String sElemName : asElemNames) {
				if (!sGivens.isEmpty())
					sGivens += ",";
				sGivens += sElemName;
			}

			List<String> listElemNames = new ArrayList<String>();
			for (String sElemName : asElemNames)
				listElemNames.add(sElemName);

			String[] asVarNames = network.getVarNamesSorted();
			String sFinds = "";
			for (String sVarName : asVarNames) {
				if (!listElemNames.contains(sVarName) && !sVarName.startsWith("iZ") && !sVarName.startsWith("pZ")) {
					if (!sFinds.isEmpty())
						sFinds += ",";
					sFinds += sVarName;
				}
			}
			
			defaults.put("finds", sFinds);
			defaults.put("givens", sGivens);

			break;
		default:
			return false;
		}
		
		return true;
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
		container.fillParamMap(params);
		
		QuProblemElec problem = container.createProblemElec();
		problem.setTitle("Three-Resistor Circuit");
		problem.setKeywords("series,parallel,resistance");
		problem.createText().setHtml("<resource name='network'/>");

		updateNetwork();
		DisplayParams display = new DisplayParams();
		display.displayCurrents = DisplayCurrents.All;
		display.displayNodes = DisplayNodes.All;

		// Add all network variable to the problem
		
		String[] asVarNames = network.getVarNamesSorted(); 
		Map<String, Var> vars = network.getVars();
		for (String sVarName : asVarNames) {
			Var var = vars.get(sVarName);
			problem.setVar(sVarName, var.getNameXhtml(), var.getDescription());
		}

		QuQuestionMathlib q;
		q = problem.createQuestionMathlib();
		q.setAnswerMode(QuQuestionMathlib.AnswerMode.EquationBlock);

		String[] asGivens = params.get("givens").split(",");
		Arrays.sort(asGivens, String.CASE_INSENSITIVE_ORDER);
		for (String sGiven : asGivens) {
			sGiven = sGiven.trim();
			if (!sGiven.isEmpty() && vars.containsKey(sGiven)) {
				q.addGivenVar(sGiven);
				if (sGiven.charAt(0) == 'i')
					display.currents.add(sGiven);
			}
		}
		
		String[] asFinds = params.get("finds").split(",");
		Arrays.sort(asFinds, String.CASE_INSENSITIVE_ORDER);
		for (String sFind : asFinds) {
			sFind = sFind.trim();
			if (!sFind.isEmpty() && vars.containsKey(sFind)) {
				q.addFindVar(sFind);
				if (sFind.charAt(0) == 'i')
					display.currents.add(sFind);
			}
		}
		
		/*
		// List of variables to display in the variable table
		List<String> asTableVars = selectVariablesForVartable(asGivens, asFinds);
		String sTableVars = CoreUtils.join(asTableVars, ",");
		problem.setResource("vartable", "vartable", DocumentHelper.createText(sTableVars));
		*/
		
		Solver solver = network.getFindables(asGivens);
		String sSolution = solver.getSolution(asFinds);
		q.setSolution(null, sSolution);

		problem.setResource("network", "network", network.getDisplayXml(display));
	}
	
	private void updateNetwork() {
		System.out.println("updateNetwork: " + params);
		if (bNeedNewNetwork) {
			bNeedNewNetwork = false;
			
			network = new Network();
			network.addNode("a", 0, 1);
			network.addNode("b", 2, 1);
			network.addNode("0", 1, 0);

			// Choose source type
			if (params.get("source").equals("I"))
				network.addElem("I", "0", "a", 0, 0, Direction.Up);
			else
				network.addElem("V",  "0", "a", 0, 0, Direction.Up);
			
			// Choose resistor arrangement
			if (params.get("structure").equals("sp")) {
				network.addElem("R1", "a", "b", 0, 1, Direction.Right);
				network.addElem("R2", "b", "0", 1, 1, Direction.Down);
				network.addElem("R3", "b", "0", 2, 1, Direction.Down);
			}
			else {
				network.addElem("R1", "a", "0", 1, 1, Direction.Down);
				network.addElem("R2", "a", "b", 1, 1, Direction.Right);
				network.addElem("R3", "b", "0", 2, 1, Direction.Down);
			}

			network.createVars();
		}
	}
	
	/*
	private List<String> selectVariablesForVartable(String[] asGivens, String[] asFinds) {
		List<String> asAll = new ArrayList<String>();
		List<String> asVars = new ArrayList<String>();
		
		asAll.addAll(Arrays.asList(asGivens));
		asAll.addAll(Arrays.asList(asFinds));
		Collections.sort(asAll, String.CASE_INSENSITIVE_ORDER);
		
		boolean bI = false, bP = false, bV1 = false, bV2 = false, bZ = false;
		for (String s : asAll) {
			char c = s.charAt(0);
			if (c == 'i') {
				if (!bI) {
					bI = true;
					asVars.add(s);
				}
			}
			else if (c == 'p') {
				if (!bP) {
					bP = true;
					asVars.add(s);
				}
			}
			else if (c == 'v') {
				if (s.length() == 2) {
					if (!bV1) {
						bV1 = true;
						asVars.add(s);
					}
				}
				else  if (s.length() == 3) {
					if (!bV2) {
						bV2 = true;
						asVars.add(s);
					}
				}
			}
			else if (c == 'Z') {
				if (!bZ) {
					bZ = true;
					asVars.add(s);
				}
			}
		}
		
		return asVars;
	}
	*/
	
	/*
	private boolean paramIsTrue(Map<String, String> params, String name, String sValue) {
		if (params.containsKey(name)) {
			String value = params.get(name);
			if (value != null) {
				value = value.toLowerCase();
				List<String> asValues = Arrays.asList(value.split(","));
				if (asValues.contains(sValue))
					return true;
			}
		}
		return false;
	}
	*/
}
