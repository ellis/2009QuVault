package net.ellisw.quvault.vault;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class ProblemParamSpecs {
	public static class Var {
		private String sType;
		private String sName;
		private String sDesc;
		private boolean bUserEditable = false;
		
		
		public Var(String sName, String sDesc) {
			this("text", sName, sDesc);
		}
		
		protected Var(String sType, String sName, String sDesc) {
			this.sType = sType;
			this.sName = sName;
			this.sDesc = sDesc;
		}
		
		public String getType() { return sType; }
		
		public String getName() { return sName; }
		public void setName(String s) { sName = s; }
		
		public String getDesc() { return sDesc; }
		public void setDesc(String s) { sDesc = s; }
		
		public boolean getUserEditable() { return bUserEditable; }
		public Var setUserEditable(boolean b) { bUserEditable = b; return this; }
		
		public Element getXml() {
			Element elem = DocumentHelper.createElement("param-spec");
			fillElement(elem);
			return elem;
		}
		
		protected void fillElement(Element elem) {
			elem.addAttribute("type", sType);
			elem.addAttribute("name", sName);
			elem.addAttribute("label", sDesc);
		}
	}
	
	
	public static class VarListFloat extends Var {
		private int itemCount = 0;
		private double nRandomRangeFactor = 10;

		
		public VarListFloat(String sName, String sDesc) {
			super(sName, sDesc);
		}
		
		public int getItemCount() { return itemCount; }
		public VarListFloat setItemCount(int n) { itemCount = n; return this; }

		public double getRandomRangeFactor() { return nRandomRangeFactor; }
		public VarListFloat setRandomRangeFactor(double n) { nRandomRangeFactor = n; return this; }
	}
	
	
	protected static class VarNamedList extends Var {
		private List<String> names = new ArrayList<String>();
		private List<String> descs = new ArrayList<String>();
		
		
		public VarNamedList(String sType, String sName, String sDesc) {
			super(sType, sName, sDesc);
		}

		public VarNamedList addValue(String sName, String sDesc) {
			names.add(sName);
			descs.add(sDesc);
			return this;
		}
		
		public List<String> getNames() { return names; }
		public List<String> getDescs() { return descs; }

		protected void fillElement(Element elem) {
			super.fillElement(elem);
			for (int i = 0; i < names.size(); i++) {
				elem.addElement("item").addAttribute("name", names.get(i)).addAttribute("label", descs.get(i));
			}
		}
	}
	
	
	public static class VarOption extends VarNamedList {
		public VarOption(String sName, String sDesc) {
			super("option", sName, sDesc);
		}
	}
	
	
	public static class VarRangeInteger extends Var {
		private int min;
		private int max;
		
		
		public VarRangeInteger(String sName, String sDesc, int min, int max) {
			super(sName, sDesc);
			this.min = min;
			this.max = max;
		}
		
		public int getMin() { return min; }
		public void setMin(int n) { min = n; }
		
		public int getMax() { return max; }
		public void setMax(int n) { max = n; }
	}
	
	
	public static class VarChecklist extends VarNamedList {
		public VarChecklist(String sName, String sDesc) {
			super("checklist", sName, sDesc);
		}
	}
	
	
	private List<Var> vars = new ArrayList<Var>();
	
	
	public List<Var> getVars() { return vars; }

	public VarOption addOption(String sName, String sDesc) {
		VarOption v = new VarOption(sName, sDesc);
		vars.add(v);
		return v;
	}
	
	public VarChecklist addChecklist(String sName, String sDesc) {
		VarChecklist v = new VarChecklist(sName, sDesc);
		vars.add(v);
		return v;
	}
	
	public Var addVar(String sName, String sDesc) {
		Var v = new Var(sName, sDesc);
		vars.add(v);
		return v;
	}
	
	public VarRangeInteger addRangeInteger(String sName, String sDesc, int min, int max) {
		VarRangeInteger v = new VarRangeInteger(sName, sDesc, min, max);
		vars.add(v);
		return v;
	}

	public VarListFloat addListFloat(String sName, String sDesc) {
		VarListFloat v = new VarListFloat(sName, sDesc);
		vars.add(v);
		return v;
	}
}
