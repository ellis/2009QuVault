package net.ellisw.quvault.vault;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import net.ellisw.quvault.vault.IProblemTempl;
import net.ellisw.quvault.vault.ProblemHeader;
import net.ellisw.quvault.vault.ProblemParamSpecs;
import net.ellisw.quvault.vault.ProblemParams;
import net.ellisw.quvault.vault.QuContainer;
import net.ellisw.quvault.vault.QuProblem;
import net.ellisw.quvault.vault.QuQuestionMathlib;

public abstract class PT_ElementConnections implements IProblemTempl {
	/*
	private ElementType elementType;
	private ConnectionType connectionType;
	
	
	public static void setupHeaders(List<ProblemHeader> list) {
		list.add(new ProblemHeader("PT_ElementConnections(mode=RS)", "Series Resistance", "QuVault Team", "series,resistance"));
		list.add(new ProblemHeader("PT_ElementConnections(mode=CS)", "Series Capacitance", "QuVault Team", "series,capacitance"));
	}
	
	public boolean setInitialParams(Map<String, String> params) {
		assert(params.containsKey("mode"));
		
		String mode = params.get("mode");
		char cElem = mode.charAt(0);
		char cConn = mode.charAt(1);

		if (cElem == 'R') elementType = ElementType.Resistor;
		else if (cElem == 'C') elementType = ElementType.Capacitor;
		else if (cElem == 'L') elementType = ElementType.Inductor;
		else if (cElem == 'E') elementType = ElementType.DcVoltageSource;
		else if (cElem == 'I') elementType = ElementType.DcCurrentSource;

		if (cConn == 'S') connectionType = ConnectionType.Series;
		else if (cConn == 'P') connectionType = ConnectionType.Parallel;
		
		return true;
	}

	public void setupParamSpecs(ProblemParamSpecs specs) {
		// This is an integer range, from 2 to 5
		specs.addRangeInteger("count", "Element Count", 2, 5);
			//.setDefault(3);
		
		specs.addListFloat("values", "Element Values");
	}
	
	public void loadParamDefaults(ProblemParams params) {
		params.put("count", "3");
		params.put("values", "sym,sym,sym");
	}
	
	public void loadRandomParams(Map<String, String> params) {
		Random rnd = new Random();
		
		if (!params.containsKey("count")) {
			int n = rnd.nextInt(4) + 2;
			params.put("count", Integer.toString(n));
		}
		String sCount = params.get("count");
		int nCount = Integer.parseInt(sCount);
		assert(nCount >= 2);
		
		double[] values;
		if (params.containsKey("values")) {
			String sValues = params.get("values");
			values = parseListFloatString(sValues, nCount);
		}
		else {
			values = new double[nCount];
		}
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
	}

	public void setupProblemData(QuContainer container) {
		Map<String, String> params = container.fillParamMap();
		assert(params.containsKey("count"));
		assert(params.containsKey("values"));
		
		String sCount = params.get("count");
		int nCount = Integer.parseInt(sCount);
		assert(nCount >= 2);

		double[] values = parseListFloatString(params.get("values"), nCount);

		
		String title = null;
		String keywords = null;
		String sElemTag = null;
		String sElemPrefix = null;

		switch (connectionType) {
		case Series:
			title = "Series ";
			keywords = "series,";
			break;
		case Parallel:
			title = "Parallel ";
			keywords = "parallel,";
			break;
		}
		
		switch (elementType) {
		case Resistor:
			title += "Resistance";
			keywords += "resistance";
			sElemTag = "resistor";
			sElemPrefix = "R";
			break;
		case Capacitor:
			title += "Capacitance";
			keywords += "capacitance";
			sElemTag = "capacitor";
			sElemPrefix = "C";
			break;
		case Inductor:
			title += "Inducance";
			keywords += "inductance";
			sElemTag = "inductor";
			sElemPrefix = "L";
			break;
		}

		QuProblem problem = container.createProblem();
		problem.setTitle(title);
		problem.setKeywords(keywords);
		problem.createText().setHtml("<resource name='network'/>");

		/-*
		String sNetworkXml = "<network>" + "<circle label-left='a'/>";
		for (int i = 0; i < nCount; i++) {
			sNetworkXml += "<" + sElemTag + " dx='+1' label-left='" + sElemPrefix + Integer.toString(i + 1) + "'";
			if (values[i] > 0) {
				sNetworkXml += " label-right='" + Double.toString(values[i]) + "'";
			}
			sNetworkXml += "/>";
		}
		sNetworkXml += "<circle label-right='b'/>" + "</network>";
		problem.setResource("network", sNetworkXml);
		*-/
		Document doc = DocumentHelper.createDocument();
		Element network = doc.addElement("network");
		network.addElement("circle").addAttribute("label-left", "a");
		for (int i = 0; i < nCount; i++) {
			Element elem = network.addElement(sElemTag);
			elem.addAttribute("dx", "+1");
			elem.addAttribute("label-left", sElemPrefix + Integer.toString(i + 1));
			if (values[i] > 0) {
				elem.addAttribute("label-right", Double.toString(values[i]));
			}
		}
		network.addElement("circle").addAttribute("label-right", "b");
		problem.setResource("network", "network", network);

		QuQuestionMathlib q = problem.createQuestionMathlib();
		q.setVar("RT", "R<sub>T</sub>", "total resistance");
		q.addFindVar("RT");
		String sSolution = "RT = ";
		for (int i = 0; i < nCount; i++) {
			if (i > 0)
				sSolution += " + ";

			if (values[i] > 0) {
				sSolution += values[i];
			}
			else {
				String s = sElemPrefix + Integer.toString(i + 1);
				q.addGivenVar(s);
	
				sSolution += s;
			}
		}
		q.setSolution(null, sSolution);
	}
	
	private double[] parseListFloatString(String sList, int nCount) {
		double[] values = new double[nCount];
		String[] asValues = sList.split(",");
		for (int i = 0; i < asValues.length && i < nCount; i++) {
			String sValue = asValues[i];
			if (!sValue.isEmpty()) {
				if (sValue.equals("sym")) {
					values[i] = -1;
				}
				else {
					double nValue = Double.parseDouble(sValue);
					values[i] = nValue;
				}
			}
		}
		return values;
	}
	*/
}
