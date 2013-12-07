package net.ellisw.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solver {
	public static final int FIND_PHASE_SINGLE = 0;
	public static final int FIND_PHASE_Z = 1;
	public static final int FIND_PHASE_OTHER = 2;
	
	public static class Item {
		public boolean bEnabled = true;
		public boolean bGiven;
		public boolean bHave;
		public Var var;
		public Expression expr;
		/**
		 * Number of variables involved in calculating this variable (recursively calculated).
		 */
		public int nCost;
		
		public Item() {
		}
		
		public Item(Var var) {
			this.var = var;
		}
		
		public Item(Item that) {
			bEnabled = that.bEnabled;
			bGiven = that.bGiven;
			bHave = that.bHave;
			var = that.var;
			expr = that.expr;
		}
	}
	
	public Map<String, Item> mapNameToItem = new HashMap<String, Item>();
	public List<Item> exprs = new ArrayList<Item>();
	
	
	public Solver() {
	}
	
	public Solver(Solver orig) {
		for (Item origItem : orig.mapNameToItem.values()) {
			Item item = new Item(origItem);
			mapNameToItem.put(item.var.getName(), item);
		}
		
		for (Item origItem : orig.exprs) {
			String sVarName = origItem.var.getName();
			Item item = mapNameToItem.get(sVarName);
			exprs.add(item);
		}
	}
	
	//public Item addVar(Var var) {
	//}
	
	public void addGivenItems(List<Item> itemsGiven) {
		for (Item item : itemsGiven) {
			item.bGiven = true;
			item.bHave = true;
		}
	}

	public void addGivenNames(List<String> asGivens) {
		for (String sGiven : asGivens) {
			Item item = mapNameToItem.get(sGiven);
			item.bGiven = true;
			item.bHave = true;
		}
	}

	public void addGivenNames(String... asGivens) {
		addGivenNames(Arrays.asList(asGivens));
	}

	public boolean find(List<String> asNewDerivatives) {
		boolean bFound = false;
		bFound |= find(FIND_PHASE_SINGLE, asNewDerivatives);
		bFound |= find(FIND_PHASE_Z, asNewDerivatives);
		while (true) {
			boolean bFoundPhase = false;
			bFoundPhase |= find(FIND_PHASE_SINGLE, asNewDerivatives);
			bFoundPhase |= findStep(FIND_PHASE_OTHER, asNewDerivatives);
			if (!bFoundPhase)
				break;
			bFound = true;
		}
		
		return bFound;
	}
	
	public boolean find(int iPhase, List<String> asNewDerivatives) {
		boolean bFound = false;
		while (true) {
			boolean bFoundStep = findStep(iPhase, asNewDerivatives);
			if (!bFoundStep)
				break;
			bFound = true;
		}
		
		return bFound;
	}
	
	public boolean findStep(int iPhase, List<String> asNewDerivatives) {
		boolean bFound = false;
		
		List<Expression> exprsFound = new ArrayList<Expression>();

		//System.out.println("LOOP");
		List<String> asFounds = new ArrayList<String>();
		for (Item item : mapNameToItem.values()) {
			if (!item.bEnabled || item.bHave)
				continue;
			
			String sVarName = item.var.getName();
			
			// Only look at Z* variables?
			if (iPhase == FIND_PHASE_Z && sVarName.charAt(0) != 'Z')
				continue;
			
			System.out.println("inspecting Var: " + sVarName);
			// for debug only
			//if (sVarName.equals("iC"))
			//	sVarName.length();
			// ENDFIX

			exprsFound.clear();
			for (Expression expr : item.var.getExpressions()) {
				if (iPhase == FIND_PHASE_SINGLE && expr.getExpressionArgs().length != 1)
					continue;
				
				boolean bHaveAll = true;
				
				System.out.println("inspecting Expr: " + expr.getExpression());
				for (String sNeed : expr.getVars()) {
					Item itemNeed = mapNameToItem.get(sNeed);
					if (itemNeed == null || !itemNeed.bHave) {
						bHaveAll = false;
						break;
					}
				}
				
				if (bHaveAll)
					exprsFound.add(expr);
			}
			
			if (exprsFound.size() > 0) {
				Expression exprFound = null;
				int nCostFound = 0;
				
				if (exprsFound.size() == 1) {
					exprFound = exprsFound.get(0);
					nCostFound = calcCost(exprFound);
				}
				else  {
					int nCostMin = Integer.MAX_VALUE;
					for (Expression expr : exprsFound) {
						int nCost = calcCost(expr);
						System.out.println("? " + sVarName + " = " + expr.getExpression() + "      (cost=" + nCost + ")");
						if (nCost < nCostMin) {
							exprFound = expr;
							nCostMin = nCost;
						}
					}
					nCostFound = nCostMin; 
				}
				
				asFounds.add(sVarName);
				item.expr = exprFound;
				item.nCost = nCostFound;
				exprs.add(item);
				System.out.println(sVarName + " = " + exprFound.getExpression() + "      (cost=" + nCostFound + ")");
			}
		}
		System.out.println();

		if (asFounds.size() > 0) {
			bFound = true;
			for (String sVarName : asFounds) {
				Item item = mapNameToItem.get(sVarName);
				item.bHave = true;
				if (asNewDerivatives != null && sVarName.endsWith("'"))
					asNewDerivatives.add(sVarName);
			}
		}
		
		return bFound;
	}
	
	private int calcCost(Expression expr) {
		int nCost = 0;
		for (String sNeed : expr.getVars()) {
			Item itemNeed = mapNameToItem.get(sNeed);
			if (itemNeed.nCost > 0)
				nCost += itemNeed.nCost;
			else
				nCost++;
		}
		return nCost;
	}
	
	public String getSolution(List<String> asFinds) {
		String sSolution = "";
		
		// Get the union of the set of variables which the variables in asFinds depend upon
		Set<String> asUsed = new HashSet<String>();
		for (String sVarName : asFinds) {
			Item item = mapNameToItem.get(sVarName);
			getDependencies(item, asUsed);
		}
		
		for (Item item : exprs) {
			String sVarName = item.var.getName();
			if (asUsed.contains(sVarName)) {
				sSolution += sVarName + " = " + item.expr.getExpression() + "\n";
			}
		}
		
		return sSolution.trim();
	}

	public String getSolution(String... asFinds) {
		return getSolution(Arrays.asList(asFinds));
	}
	
	public String getSingleExpressionFor(String sFind) {
		Item itemFind = mapNameToItem.get(sFind);
		assert(itemFind != null);

		// Get the union of the set of variables which the variables in asFinds depend upon
		Set<String> asUsed = new HashSet<String>();
		getDependencies(itemFind, asUsed);
		
		Map<String, String> map = new HashMap<String, String>();
		for (Item item : exprs) {
			String sVarName = item.var.getName();
			if (asUsed.contains(sVarName)) {
				String sExpression = "(" + item.expr.getExpression(map) + ")";
				map.put(sVarName, sExpression);
			}
		}
		
		String sSolution = sFind + " = " + itemFind.expr.getExpression(map);
		return sSolution.trim();
	}

	/**
	 * For the given "find" variable, mark each of the variables it depends on as "used" in the array abUsed.
	 * This is done recursively and ultimately produces a list of all variables required to calculate the initial
	 * "find" variable. 
	 * @param item
	 * @param asUsed
	 */
	private void getDependencies(Item item, Set<String> asUsed) {
		if (item.bEnabled && item.bHave) {
			asUsed.add(item.var.getName());

			// Recursively check dependencies of variables which this expression depends upon 
			Expression expr = item.expr;
			if (expr != null) {
				for (String sVarName : expr.getVars()) {
					if (!asUsed.contains(sVarName)) {
						Item child = mapNameToItem.get(sVarName);
						if (child != null)
							getDependencies(child, asUsed);
						else
							asUsed.add(sVarName);
					}
				}
			}
		}
	}

	/**
	 * Get the variables which are not calculated from other variables in the solution set
	 * @param mapSolutions
	 * @param sFind
	 * @param asVarNames
	 * @param asGivens
	 */
	private void getGivens(Item item, Set<Item> asGivens) {
		// Recursively check dependencies of variables which this expression depends upon 
		Expression expr = item.expr;
		if (expr != null) {
			for (String sVarName : expr.getVars()) {
				Item child = mapNameToItem.get(sVarName);
				if (child == null)
					continue;
				
				if  (!child.bEnabled || !child.bHave)
					asGivens.add(child);
				else
					getGivens(child, asGivens);
			}
		}
	}
	
	/**
	 * You must call addGiven*() and find() before calling this method.
	 * @return
	 */
	public List<Item> findFirstOrderSystem() {
		// Temporarily treat the variables with derivatives as given
		List<Item> itemsGiven = getListOfItemsWithDerivatives();
		for (Item item : itemsGiven)
			item.bHave = true;
		
		// Find again: this should provide us with a system of first order diffeqs
		List<String> asNewDerivatives = new ArrayList<String>();
		find(asNewDerivatives);
		
		// Remove 'bHave = true' from items in itemsGiven if they don't have associated expressions  
		for (Item item : itemsGiven)
			item.bHave = (item.expr != null);
		
		List<Item> system = getListOfItems(asNewDerivatives);
		return system;
	}
	
	public Map<Item, Solver> findDiffEqs() {
		Map<Item, Solver> mapSolutions = new HashMap<Item, Solver>();

		// Temporarily treat the variables with derivatives as given
		List<Item> itemsSystem = getListOfItemsWithDerivatives();
		int nOrder = itemsSystem.size();
		int nTries = nOrder - 1;
		
		differentiate(nTries);
		
		for (Item item : itemsSystem) {
			List<String> asGivens = new ArrayList<String>(nOrder);
			String sGiven = item.var.getName().replace("'", "");
			for (int i = 0; i < nOrder; i++) {
				asGivens.add(sGiven);
				sGiven += "'";
			}

			Solver sub = new Solver(this);
			sub.addGivenNames(asGivens);

			System.out.println();
			System.out.println("SYSTEM FOR " + sGiven);
			
			List<String> asNewDerivatives = new ArrayList<String>();
			sub.find(asNewDerivatives);
			for (String sNewDerivative : asNewDerivatives) {
				String sBaseName = sNewDerivative.replace("'", "");
				if (sBaseName.equals(item.var.getName())) {
					Item itemDeriv = sub.mapNameToItem.get(sNewDerivative);
					if (sub.findDiffEqs_isComplete(itemDeriv))
						mapSolutions.put(itemDeriv, sub);
				}
			}
		}

		return mapSolutions;
	}
	
	private boolean findDiffEqs_isComplete(Item item) {
		Set<Item> itemsGiven = new HashSet<Item>();
		getGivens(item, itemsGiven);
		for (Item child : itemsGiven) {
			if (!child.bGiven)
				return false;
		}
		return true;
	}
	
	
	public List<Item> getListOfItems(List<String> asVarNames) {
		List<Item> items = new ArrayList<Item>();
		for (String sVarName : asVarNames) {
			Item item = mapNameToItem.get(sVarName);
			if (item != null)
				items.add(item);
		}
		return items;
	}
	
	public List<Item> getListOfItemsWithDerivatives() {
		// Get names all of variables with derivatives
		List<Item> items = new ArrayList<Item>();
		for (Item item : mapNameToItem.values()) {
			String sVarName = item.var.getName();
			if (sVarName.endsWith("'")) {
				sVarName = sVarName.substring(0, sVarName.length() - 1);
				Item orig = mapNameToItem.get(sVarName);
				items.add(orig);
			}
		}
		return items;
	}
	
	public List<String> getListOfVarNames(Collection<Item> items) {
		List<String> as = new ArrayList<String>(items.size());
		for (Item item : items) {
			as.add(item.var.getName());
		}
		return as;
	}
	
	public Item differentiate(Item orig) {
		if (!orig.bEnabled)
			return null;
		
		Var origVar = orig.var;
		String sOrigVarName = origVar.getName();
		
		char c = sOrigVarName.charAt(0);
		if (c != 'i' && c != 'v')
			return null;
		
		String sDerivName = sOrigVarName + "'";
		if (mapNameToItem.containsKey(sDerivName))
			return null;
		
		NameUtils.VarData d = (new NameUtils()).getVarDataD(origVar.getName(), origVar.getNameXhtml());
		Var v = new Var(d.sName, d.sNameXhtml, d.sDescription);
		for (Expression exprOrig : origVar.getExpressions()) {
			Expression expr = differentiate(exprOrig);
			if (expr != null)
				v.addExpression(expr);
		}
		
		Item item = null;
		if (v.getExpressions().size() > 0)
			item = new Item(v);
		
		return item;
	}
	
	public Expression differentiate(Expression exprOrig) {
		String[] asVars = new String[exprOrig.getVars().size()];
		String[] asOrigVars = exprOrig.getExpressionArgs();
		boolean bAddExpr = false;
		for (int iDep = 0; iDep < asOrigVars.length; iDep++) {
			String sOrigVar = asOrigVars[iDep];
			char c = sOrigVar.charAt(0);
			if (c == 'i' || c == 'v') {
				asVars[iDep] = sOrigVar + "'";
				bAddExpr = true;
			}
			else if (c == 'p') {
				bAddExpr = false;
				break;
			}
			else
				asVars[iDep] = sOrigVar;
		}
		
		Expression expr = null;
		if (bAddExpr)
			expr = new Expression(exprOrig.isPositive(), exprOrig.getExpressionFormat(), asVars);
		return expr;
	}

	/**
	 * This will differentiate all current and voltage drop variables once.
	 */
	public void differentiate(int n) {
		List<Item> itemsNewDeriv = new ArrayList<Item>();
		
		// Differentiate all variables n times
		while (n > 0) {
			// Differentiate all existing variables once
			for (Item orig : mapNameToItem.values()) {
				Item deriv = differentiate(orig);
				if (deriv != null)
					itemsNewDeriv.add(deriv);
			}
			
			// Add any new variables to our variable map
			for (Item item : itemsNewDeriv)
				mapNameToItem.put(item.var.getName(), item);
			
			n--;
		}
	}
}
