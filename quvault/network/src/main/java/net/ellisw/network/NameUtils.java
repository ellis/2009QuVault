package net.ellisw.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NameUtils {
	public static class NodesData {
		public boolean bSwap;
		public boolean bPos;
		public String sNodes; // REFACTOR: we can remove this and use VarData.sIndex instead
		public String sNodePos;
		public String sNodeNeg;
		/** Equals either sNodePos or "ground" */
		public String sNodePosLabel;
		/** Equals either sNodeNeg or "ground" */
		public String sNodeNegLabel;
	}
	
	public static class VarData {
		public String sName;
		public String sNameXhtml;
		public String sIndex;
		public String sIndexXhtml;
		public String sDescription;
		public NodesData nodes;
	}
	
	public String getVarNameI(String s) {
		char c = s.charAt(0);
		String sName = (c == 'I') ? s : "i" + s;
		return sName;
	}

	public String getVarNameI(Elem elem) { return getVarNameI(elem.getName()); }

	public String getVarNameP(String s) {
		String sName = "p" + s;
		return sName;
	}

	public String getVarNameP(Elem elem) { return getVarNameP(elem.getName()); }

	public String getVarNameZ(String s) {
		char c = s.charAt(0);
		String sName = (c == 'R' || c == 'Z') ? s : "Z" + s;
		return sName;
	}
	
	public String getVarNameZ(Elem elem) { return getVarNameZ(elem.getName()); }

	public VarData getVarDataI(Elem elem) {
		VarData d = new VarData();
		
		String s = elem.getName();
		if (s.charAt(0) == 'I') {
			d.sName = s;
			d.sNameXhtml = s;
		}
		else {
			d.sIndex = elem.getName();
			d.sIndexXhtml = d.sIndex;
			d.sName = "i" + d.sIndex;
			d.sNameXhtml = "i<sub>" + d.sIndexXhtml + "</sub>";
		}
		d.sDescription = String.format("Current through <i>%s</i>", d.sNameXhtml);
		return d;
	}

	public VarData getVarDataP(Elem elem) {
		VarData d = new VarData();
		d.sIndex = elem.getName();
		d.sIndexXhtml = d.sIndex;
		d.sName = "p" + d.sIndex;
		d.sNameXhtml = "p<sub>" + d.sIndexXhtml + "</sub>";
		d.sDescription = String.format("Power dissipated by <i>%s</i>", d.sNameXhtml);
		return d;
	}

	public VarData getVarDataV(Node nodePlus, Node nodeMinus) {
		VarData d = new VarData();
		d.nodes = getNodesData(nodePlus, nodeMinus);
		d.sIndex = d.nodes.sNodes;
		d.sIndexXhtml = d.sIndex;
		d.sName = "v" + d.sIndex;
		d.sNameXhtml = "v<sub>" + d.sIndexXhtml + "</sub>";
		d.sDescription = String.format("Voltage drop from <i>%s</i> to <i>%s</i>", d.nodes.sNodePosLabel, d.nodes.sNodeNegLabel);
		return d;
	}
	
	public VarData getVarDataV(Elem elem) {
		return getVarDataV(elem.getNodes().get(0), elem.getNodes().get(1));
	}

	public VarData getVarDataZ(Elem elem) {
		VarData d = new VarData();
		
		String s = elem.getName();
		char c = s.charAt(0);
		
		if (c == 'R') {
			d.sName = s;
			d.sNameXhtml = s;
		}
		else {
			List<Elem> elems = new ArrayList<Elem>(1);
			elems.add(elem);
			getVarDataZ_indexStrings(elems, d);
			
			d.sName = "Z" + d.sIndex;
			d.sNameXhtml = "Z<sub>" + d.sIndexXhtml + "</sub>";
		}
		
		return d;
	}
	
	public VarData getVarDataZ(List<Elem> elems) {
		VarData d = new VarData();
		
		getVarDataZ_indexStrings(elems, d);
		d.sName = "Z" + d.sIndex;
		d.sNameXhtml = "Z<sub>" + d.sIndexXhtml + "</sub>";
		
		return d;
	}
	
	private void getVarDataZ_indexStrings(List<Elem> elems, VarData d) {
		List<String> list = new ArrayList<String>();
		for (Elem elem : elems) {
			if (elem.getName().charAt(0) == 'Z')
				list.addAll(getVarDataZ_indexList(elem));
			else
				list.add(elem.getName());
		}
		Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
		
		String s = "";
		String sXhtml = "";
		for (String sName : list) {
			s += sName;
			
			if (!sXhtml.isEmpty())
				sXhtml += ",";
			sXhtml += sName;
		}
		
		d.sIndex = s;
		d.sIndexXhtml = sXhtml;
		if (list.size() == 1)
			d.sDescription = String.format("Impedance of <i>%s</i>", list.get(0));
		else
			d.sDescription = String.format("Combined impedance of the given elements");
	}
	
	private List<String> getVarDataZ_indexList(Elem elemZ) {
		assert(elemZ.getName().charAt(0) == 'Z');
		
		String s = elemZ.getName();
		List<String> list = new ArrayList<String>();
		int iStart = 1;
		for (int i = 2; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 'A' && c <= 'Z') {
				String sElemName = s.substring(iStart, i);
				list.add(sElemName);
				iStart = i;
			}
		}
		list.add(s.substring(iStart));
		
		return list;
	}
	
	public VarData getVarDataD(String sName, String sNameXhtml) {
		VarData d = new VarData();
		d.sName = sName + "'";
		d.sNameXhtml = sNameXhtml + "'";
		d.sDescription = String.format("Derivative of <i>%s</i>", d.sNameXhtml);
		return d;
	}
	
	private NodesData getNodesData(Node nodePos, Node nodeNeg) {
		NodesData d = new NodesData();
		
		String s0 = nodePos.getName();
		String s1 = nodeNeg.getName();
		String s0Label = s0;
		String s1Label = s1;
		
		// Calculate bSwap
		boolean bGround0 = false;
		boolean bGround1 = false;
		if (s0.equals("0")) {
			bGround0 = true;
			d.bSwap = true;
			s0 = "";
			s0Label = "ground";
		}
		if (s1.equals("0")) {
			bGround1 = true;
			s1 = "";
			s1Label = "ground";
		}
		if (!bGround0 && !bGround1 && s0.compareTo(s1) > 0)
			d.bSwap = true;
		
		d.bPos = !d.bSwap;
		
		if (d.bPos) {
			d.sNodePos = s0;
			d.sNodeNeg = s1;
			d.sNodes = s0 + s1;
			d.sNodePosLabel = s0Label;
			d.sNodeNegLabel = s1Label;
		}
		else {
			d.sNodePos = s1;
			d.sNodeNeg = s0;
			d.sNodes = s1 + s0;
			d.sNodePosLabel = s1Label;
			d.sNodeNegLabel = s0Label;
		}
		
		return d;
	}
}
