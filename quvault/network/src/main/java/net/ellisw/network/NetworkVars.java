package net.ellisw.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkVars {
	private class ElemByNameComparator implements Comparator<Elem> {
		@Override
		public int compare(Elem arg0, Elem arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}
	}
	
	//private NetworkData data;
	private List<Node> nodes;
	private List<Elem> elems;
	
	private NameUtils utils = new NameUtils();
	
	private Map<String, Var> vars = new HashMap<String, Var>();
	private String[] asVarNamesSorted = null;
	private Map<String, String> translations = null;
	
	public Map<String, Var> getVars() { return vars; }
	
	
	public NetworkVars(NetworkData data, Map<String, String> translations) {
		nodes = data.getNodes();
		elems = data.getElems();
		createVars(translations);
	}
	
	public String[] getVarNamesSorted() {
		if (asVarNamesSorted == null) {
			asVarNamesSorted = new String[vars.size()];
			int i = 0;
			for (String s : vars.keySet()) {
				asVarNamesSorted[i] = s;
				i++;
			}
			Arrays.sort(asVarNamesSorted, String.CASE_INSENSITIVE_ORDER);
		}
		return asVarNamesSorted;
	}
	
	private void createVars(Map<String, String> translations) {
		this.translations = translations;
		
		for (Elem elem : elems) {
			char c = elem.getName().charAt(0);
			if (c == 'V') {
				String s = elem.getName();
				NameUtils.VarData dataV = utils.getVarDataV(elem);
				
				Var var = createVar(s, s, null);
				Var varV = createVar(dataV);
				
				varV.addExpression(!dataV.nodes.bPos, "%s", var.getName());
				var .addExpression(!dataV.nodes.bPos, "%s", varV.getName());
				
				createAdditionalVoltageVars(varV, dataV.nodes);
				// TODO: v__ = pV/iV ???
			}
			else if (c == 'I') {
			}
			else if (c == 'R' || c == 'C' || c == 'L') {
				createPassiveVars(elem);
			}
		}
		
		// Construct the KCL equations for each node
		for (Node node : nodes) {
			List<Elem> elemsIn = new ArrayList<Elem>();
			List<Elem> elemsOut = new ArrayList<Elem>();
			
			for (Elem elem : node.getElems()) {
				List<Node> elemNodes = elem.getNodes();
				if (elemNodes.size() == 2) {
					if (elemNodes.get(0) == node)
						elemsOut.add(elem);
					else
						elemsIn.add(elem);
				}
			}
			
			// Construct one KCL equation for each element attached to this node
			for (Elem elem : node.getElems()) {
				Var varI = createVarI(elem);
				
				List<Elem> elemsPlus = null;
				List<Elem> elemsMinus = null;
				if (elemsIn.contains(elem)) {
					elemsPlus = elemsOut;
					elemsMinus = elemsIn;
				}
				else {
					elemsPlus = elemsIn;
					elemsMinus = elemsOut;
				}
				
				String sExpression = "";
				List<String> names = new ArrayList<String>();
				for (Elem plus : elemsPlus) {
					if (plus != elem) {
						Var varPlus = createVarI(plus);
						names.add(varPlus.getName());
						if (!sExpression.isEmpty())
							sExpression += " + ";
						sExpression += "%s";
					}
				}
				for (Elem minus : elemsMinus) {
					if (minus != elem) {
						Var varMinus = createVarI(minus);
						names.add(varMinus.getName());
						if (!sExpression.isEmpty())
							sExpression += " - ";
						else
							sExpression += "-";
						sExpression += "%s";
					}
				}

				String[] asVars = new String[names.size()];
				names.toArray(asVars);
				varI.addExpressionCheck(true, sExpression, asVars);
			}
		}
		
		reduceNetwork();
	}

	private void createPassiveVars(Elem elem) {
		NameUtils.VarData dataV = utils.getVarDataV(elem);

		Var var = createVar(elem);
		Var varZ = createVarZ(elem);
		Var varV = createVar(dataV);
		Var varI = createVarI(elem);
		Var varP = createVarP(elem);

		String s = var.getName();
		String sZ = varZ.getName();
		String sI = varI.getName();
		String sP = varP.getName();
		String sV = varV.getName();
		boolean bPos = dataV.nodes.bPos;
		
		if (s.charAt(0) == 'C') {
			createVar("w", "w", null);
			varZ.addExpression("1/(j*%s*%s)", "w", s);
			Var varDV = createVarD(varV);
			varDV.addExpression("%s / %s", sI, s);
			varI.addExpression("%s * %s", varDV.getName(), s);
		}
		else if (s.charAt(0) == 'L') {
			createVar("w", "w", null);
			varZ.addExpression("j*%s * %s", "w", s);
			Var varDI = createVarD(varI);
			varDI.addExpression("%s / %s", sV, s);
			varV.addExpression("%s * %s", varDI.getName(), s);
		}

		// [E = IR]
		// vab = iR * R
		// iR = vab / R
		// R = vab / iR
		varV.addExpression(bPos, "%s * %s", sI, sZ);
		varI.addExpression(bPos, "%s / %s", sV, sZ);
		var .addExpression(bPos, "%s / %s", sV, sI);
		
		// [P = IE]
		// pR = vab * iR
		// vab = pR / iR
		// iR = pR / vab
		varP.addExpression(bPos, "%s * %s", sV, sI);
		varV.addExpression(bPos, "%s / %s", sP, sI);
		varI.addExpression(bPos, "%s / %s", sP, sV);

		createAdditionalVoltageVars(varV, dataV.nodes);
	}
	
	private void createAdditionalVoltageVars(Var varV, NameUtils.NodesData d) {
		if (d.sNodes.length() == 2) {
			String sV0 = "v" + d.sNodePos;
			String sV0Xhtml = "v<sub>" + d.sNodePos + "</sub>";
			String sV1 = "v" + d.sNodeNeg;
			String sV1Xhtml = "v<sub>" + d.sNodeNeg + "</sub>";
			
			Var varV0 = createVar(sV0, sV0Xhtml, null);
			Var varV1 = createVar(sV1, sV1Xhtml, null);
			
			varV .addExpressionCheck(true, "%s - %s", sV0, sV1);
			varV0.addExpressionCheck(true, "%s + %s", varV.getName(), sV1);
			varV1.addExpressionCheck(true, "%s - %s", sV0, varV.getName());
		}
	}
	
	private void reduceNetwork() {
		List<Elem> elems = new ArrayList<Elem>();
		elems.addAll(this.elems);

		int nElemsToCheck = elems.size() - 1;
		reduce_debug(elems);
		while (true) {
			List<Elem> elemsNew = new ArrayList<Elem>();
			// Check for combinable elements
			for (int iElem1 = 0; iElem1 < nElemsToCheck && iElem1 < elems.size() - 1; iElem1++) {
				Elem elem1 = elems.get(iElem1);
				
				if (!isElemType(elem1, "VIRLCZ"))
					continue;
					
				List<Elem> series = new ArrayList<Elem>();
				List<Elem> parallel= new ArrayList<Elem>();
				List<Elem> nonparallelNodePos = new ArrayList<Elem>();
				List<Elem> nonparallelNodeNeg = new ArrayList<Elem>();
				reduce_getCombinableElems(elems, iElem1, series, parallel, nonparallelNodePos, nonparallelNodeNeg);
	
				// Check for parallel elements
				if (parallel.size() > 0) {
					Elem elemZ = reduce_combineParallel(parallel, nonparallelNodePos, nonparallelNodeNeg);
					elemsNew.add(elemZ);
					elems.removeAll(parallel);
					iElem1--;
					reduce_debug(elems);
					continue;
				}
				
				if (series.size() > 0) {
					List<Elem> elemsZ = reduce_combineSeries(elem1, series);
					elemsNew.addAll(elemsZ);
					elems.removeAll(series);
					elems.remove(elem1);
					iElem1--;
					reduce_debug(elems);
					continue;
				}
			}
			
			if (elemsNew.size() == 0)
				break;
			
			elems.addAll(0, elemsNew);
			nElemsToCheck = elemsNew.size();
			reduce_debug(elems);
		}
	}
	
	private void reduce_debug(List<Elem> elems) {
		System.out.print("debug:");
		for (Elem elem : elems) {
			System.out.print(" " + elem.getName());
		}
		System.out.println();
	}
	
	private void reduce_getCombinableElems(List<Elem> elems, int iElem, List<Elem> series, List<Elem> parallel, List<Elem> nonparallelNodePos, List<Elem> nonparallelNodeNeg) {
		Elem elem = elems.get(iElem); 
		
		Node node0 = elem.getNodes().get(0);
		Node node1 = elem.getNodes().get(1);
		boolean bAllowSeries0 = !node0.getName().equals("0");
		boolean bAllowSeries1 = !node1.getName().equals("0");
		// These lists are for elements which might be in series with 'elem'
		List<Elem> elemsAtNode0 = new ArrayList<Elem>();
		List<Elem> elemsAtNode1 = new ArrayList<Elem>();
		
		for (int iOther = 0; iOther < iElem; iOther++) {
			Elem other = elems.get(iOther);
			boolean bNode0 = other.getNodes().contains(node0);
			boolean bNode1 = other.getNodes().contains(node1);
			if (bNode0) nonparallelNodePos.add(other);
			if (bNode1) nonparallelNodeNeg.add(other);
		}
		
		for (int iOther = iElem + 1; iOther < elems.size(); iOther++) {
			Elem other = elems.get(iOther);
			boolean bNode0 = other.getNodes().contains(node0);
			boolean bNode1 = other.getNodes().contains(node1);
			
			// Parallel element/source
			if (bNode0 && bNode1) {
				if (typesMatchForParallelCombination(elem, other))
					parallel.add(other);
				else {
					nonparallelNodePos.add(other);
					nonparallelNodeNeg.add(other);
				}
			}
			// Series element/source
			else if (bNode0) {
				if (bAllowSeries0 && typesMatchForSeriesCombination(elem, other))
					elemsAtNode0.add(other);
				nonparallelNodePos.add(other);
			}
			else if (bNode1) {
				if (bAllowSeries1 && typesMatchForSeriesCombination(elem, other))
					elemsAtNode1.add(other);
				nonparallelNodeNeg.add(other);
			}
		}

		if (parallel.size() > 0)
			parallel.add(0, elem);
		else {
			if (nonparallelNodePos.size() == 1 && elemsAtNode0.size() == 1)
				series.add(elemsAtNode0.get(0));
			if (nonparallelNodeNeg.size() == 1 && elemsAtNode1.size() == 1)
				series.add(elemsAtNode1.get(0));
		}
	}
	
	private List<Elem> reduce_combineSeries(Elem elem, List<Elem> list) {
		List<Elem> elemsNew = new ArrayList<Elem>(list.size() + 1);
		for (Elem other : list) {
			// Sort the element names into alphabetical order
			List<Elem> elems = new ArrayList<Elem>(2);
			elems.add(elem);
			elems.add(other);
			sortElemList(elems);

			// Combined impedance
			Var varZ = createVarZ(elems);
			String sZ = varZ.getName();
			String sFirst = createVarZ(elems.get(0)).getName();
			String sSecond = createVarZ(elems.get(1)).getName();
			varZ.addExpressionCheck(true, "%s + %s", sFirst, sSecond);
		
			// TODO: Voltage divider equations 
		
			// TODO: Handle voltage sources

			// Figure out which nodes (and in which order) the new combined impedance should be connected to
			Node node0 = null;
			Node node1 = null;
			Node nodeM = null;
			boolean bOtherHasPosCurrent = true;
			if (elem.getNodes().get(1) == other.getNodes().get(0)) {
				node0 = elem.getNodes().get(0);
				node1 = other.getNodes().get(1);
				nodeM = elem.getNodes().get(1);
				bOtherHasPosCurrent = true;
			}
			else if (other.getNodes().get(1) == elem.getNodes().get(0)) {
				node0 = other.getNodes().get(0);
				node1 = elem.getNodes().get(1);
				nodeM = elem.getNodes().get(0);
				bOtherHasPosCurrent = true;
			}
			else if (elem.getNodes().get(0) == other.getNodes().get(0)) {
				node0 = elem.getNodes().get(1);
				node1 = other.getNodes().get(1);
				nodeM = elem.getNodes().get(0);
				bOtherHasPosCurrent = false;
			}
			else {
				node0 = elem.getNodes().get(0);
				node1 = other.getNodes().get(0);
				nodeM = elem.getNodes().get(1);
				bOtherHasPosCurrent = false;
			}
			List<Node> nodes = new ArrayList<Node>(2);
			nodes.add(node0);
			nodes.add(node1);
			
			// TODO_MAYBE: find better base and direction values, so that this element could actually be displayed
			Elem elemNew = new Elem(sZ, nodes, elem.getXYBase(), elem.getDirection());
			elemsNew.add(elemNew);
			createPassiveVars(elemNew);
			
			Var varINew = createVarI(elemNew);
			Var varIElem = createVarI(elem); 
			Var varIOther = createVarI(other);
			varINew.addExpressionCheck(true, "%s", varIElem.getName());
			varINew.addExpressionCheck(bOtherHasPosCurrent, "%s", varIOther.getName());
			varIElem.addExpressionCheck(true, "%s", varINew.getName());
			varIOther.addExpressionCheck(bOtherHasPosCurrent, "%s", varINew.getName());

			// Voltage divider
			String sVM = "v" + nodeM.getName();
			Var varVM = createVar(sVM, sVM, null);
			for (Elem e : elems) {
				Node nodeElemEnd = (e.getNodes().get(0) != nodeM) ? e.getNodes().get(0) : e.getNodes().get(1);
				Node nodeChainEnd = (nodeElemEnd != node0) ? node0 : node1;
				String sVElemEnd = "v" + nodeElemEnd.getName();
				String sVChainEnd = "v" + nodeChainEnd.getName();
				
				List<String> asDependencies = new ArrayList<String>();
				String sExpression = "";
				boolean bOk = true;
				if (sVElemEnd.equals("v0")) {
					sExpression += "%s";
					asDependencies.add(sVChainEnd);
				}
				else if (sVChainEnd.equals("v0")) {
					bOk = false; // I think it might be better to leave this equation out -- ellis, 20090604
				}
				else {
					sExpression += "%s + (%s-" + sVElemEnd + ")";
					asDependencies.add(sVElemEnd);
					asDependencies.add(sVChainEnd);
				}
				Var varEZ = createVarZ(e);
				sExpression += " * %s / %s";
				asDependencies.add(varEZ.getName());
				asDependencies.add(sZ);

				if (bOk) {
					varVM.addExpression(true, sExpression, asDependencies);
				}
			}
		}
		return elemsNew;
	}
	
	private Elem reduce_combineParallel(List<Elem> parallel, List<Elem> nonparallelNodePos, List<Elem> nonparallelNodeNeg) {
		// Sort the element names into alphabetical order
		sortElemList(parallel);
		sortElemList(nonparallelNodeNeg);
		sortElemList(nonparallelNodePos);

		// These are the variable names which are needed to calculate the current divider equations
		// It contains: all impedances of the parallel network + the current through the combined impedance
		List<String> asElemIDependencies = new ArrayList<String>();
		
		// Get the denominator by summing over the inverse of the impedance of all elements in 'list'
		String sDenom = "";
		String[] asZs = new String[parallel.size()];
		for (int iElem = 0; iElem < asZs.length; iElem++) {
			String sZ = utils.getVarNameZ(parallel.get(iElem));
			asZs[iElem] = sZ;
			if (!sDenom.isEmpty())
				sDenom += " + ";
			sDenom += "1/" + sZ;
			asElemIDependencies.add(sZ);
		}

		// Combined impedance
		Var varZ = createVarZ(parallel);
		varZ.addExpressionCheck(true, "1/(" + sDenom + ")", asZs);
		
		// Create element to represent the combined impedance
		Elem elemRef = parallel.get(0);
		Elem elemZ = new Elem(varZ.getName(), elemRef.getNodes(), elemRef.getXYBase(), elemRef.getDirection());

		// Create I, V, and P variables for this pseudo-element
		createPassiveVars(elemZ);
		
		// Get reference to current through Z
		Var varIZ = createVarI(elemZ);
		
		// Equations for current through Z
		reduce_combineParallel_currentThruZ(varIZ, elemRef, 0, nonparallelNodePos);
		reduce_combineParallel_currentThruZ(varIZ, elemRef, 1, nonparallelNodeNeg);
		
		// Current divider equations
		asElemIDependencies.add(varIZ.getName());
		for (Elem elem : parallel) {
			Var varElemI = createVarI(elem);
			String sElemZ = utils.getVarNameZ(elem);
			varElemI.addExpression(varIZ.getName() + " * (1/" + sElemZ + ") / (" + sDenom + ")", asElemIDependencies);
		}
		
		// TODO: Handle parallel current sources
		
		return elemZ;
	}
	
	private void reduce_combineParallel_currentThruZ(Var varIZ, Elem elemRef, int iNode, List<Elem> nonparallel) {
		// Equations for current through Z
		List<String> asVarIZDependencies = new ArrayList<String>(nonparallel.size());
		String sITotal = "";
		for (int iElem = 0; iElem < nonparallel.size(); iElem++) {
			Elem other = nonparallel.get(iElem);
			String sI = utils.getVarNameI(other);
			
			int iNodeOtherPos = (iNode == 0) ? 1 : 0;
			boolean bPos = (other.getNodes().get(iNodeOtherPos) == elemRef.getNodes().get(iNode));
			if (iElem > 0)
				sITotal += (bPos) ? " + " : " - ";
			else if (!bPos)
				sITotal += "-";
			sITotal += "%s"; 
			asVarIZDependencies.add(sI);
		}
		varIZ.addExpressionCheck(sITotal, asVarIZDependencies);
	}

	private Var createVar(String sName, String sNameXhtml, String sDescription) {
		if (translations != null && translations.containsKey(sName)) {
			sName = translations.get(sName);
			sNameXhtml = sName;
		}
		
		if (vars.containsKey(sName))
			return vars.get(sName);
		else {
			Var var = new Var(sName, sNameXhtml, sDescription);
			vars.put(sName, var);
			return var;
		}
	}
	
	private Var createVar(NameUtils.VarData d) {
		if (vars.containsKey(d.sName))
			return vars.get(d.sName);
		else {
			return createVar(d.sName, d.sNameXhtml, d.sDescription);
		}
	}
	
	private Var createVar(Elem elem) {
		return createVar(elem.getName(), elem.getName(), null);
	}
	
	private Var createVarI(Elem elem) {
		Var var = null;
		String sName = utils.getVarNameI(elem);
		if (vars.containsKey(sName))
			var = vars.get(sName);
		else
			var = createVar(utils.getVarDataI(elem));
		return var;
	}
	
	private Var createVarP(Elem elem) {
		Var var = null;
		String sName = utils.getVarNameP(elem);
		if (vars.containsKey(sName))
			var = vars.get(sName);
		else
			var = createVar(utils.getVarDataP(elem));
		return var;
	}
	
	private Var createVarZ(Elem elem) {
		Var var = null;
		NameUtils.VarData d = utils.getVarDataZ(elem);
		if (vars.containsKey(d.sName))
			var = vars.get(d.sName);
		else
			var = createVar(d);
		return var;
	}
	
	private Var createVarZ(List<Elem> elems) {
		Var var = null;
		NameUtils.VarData d = utils.getVarDataZ(elems);
		if (vars.containsKey(d.sName))
			var = vars.get(d.sName);
		else
			var = createVar(d);
		return var;
	}
	
	private Var createVarD(Var orig) {
		Var var = null;
		NameUtils.VarData d = utils.getVarDataD(orig.getName(), orig.getNameXhtml());
		if (vars.containsKey(d.sName))
			var = vars.get(d.sName);
		else
			var = createVar(d);
		return var;
	}
	
	private void sortElemList(List<Elem> elems) {
		Collections.sort(elems, new ElemByNameComparator());
	}
	
	// REFACTOR: This should really be done with enums instead of an array of characters
	private boolean isElemType(Elem elem, String acTypes) {
		char cElem = elem.getName().charAt(0);
		for (int i = 0; i < acTypes.length(); i++) {
			if (cElem == acTypes.charAt(i))
				return true;
		}
		return false;
	}
	
	private boolean typesMatchForParallelCombination(Elem elem1, Elem elem2) {
		if (elem1 == elem2)
			return false;
		if (isElemType(elem1, "RLCZ") && isElemType(elem2, "RLCZ"))
			return true;
		if (isElemType(elem1, "I") && isElemType(elem2, "I"))
			return true;
		return false;
	}
	
	private boolean typesMatchForSeriesCombination(Elem elem1, Elem elem2) {
		if (elem1 == elem2)
			return false;
		if (isElemType(elem1, "RLCZ") && isElemType(elem2, "RLCZ"))
			return true;
		if (isElemType(elem1, "E") && isElemType(elem2, "E"))
			return true;
		return false;
	}
	
	/**
	 * Get the node to be considered as the reference node.  This will either be the named "0"
	 * or the node which is last when all node names are place in alphabetic order.
	 * @return null if there are no nodes, otherwise the node which we'll take as reference.
	 */
	/*
	private Node getRefNode() {
		if (nodes.size() == 0)
			return null;
		
		Node ref = nodes.get(0);
		if (ref.getName().equals("0"))
			return ref;
		
		for (int i = 1; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			if (node.getName().equals("0"))
				return node;
			if (node.getName().compareTo(ref.getName()) > 0)
				ref = node;
		}
		
		return ref;
	}
	*/
}
