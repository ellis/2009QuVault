package net.ellisw.network;

import java.util.ArrayList;
import java.util.List;

public class Var {
	private String sName;
	private String sNameXhtml;
	private String sDescription;
	private List<Expression> expressions = new ArrayList<Expression>();
	
	
	public Var(String sName, String sNameXhtml, String sDescription) {
		this.sName = sName;
		this.sNameXhtml = sNameXhtml;
		this.sDescription = sDescription;
	}
	
	public String getName() { return sName; }
	public String getNameXhtml() { return sNameXhtml; }
	public String getDescription() { return sDescription; }
	
	public List<Expression> getExpressions() { return expressions; }
	
	public void addExpression(Expression expression) {
		expressions.add(expression);
	}
	
	private void addExpression(boolean bCheck, boolean bPositive, String sExpressionFormat, String[] vars) {
		String sExpression = String.format(sExpressionFormat, (Object[]) vars);

		if (bCheck) {
			for (Expression expr : expressions) {
				if (expr.getExpression().equals(sExpression))
					return;
			}
		}
		
		expressions.add(new Expression(bPositive, sExpressionFormat, vars));
		System.out.println("EXPR: " + sName + " = " + sExpression);
	}

	private void addExpression(boolean bCheck, boolean bPositive, String sExpressionFormat, List<String> vars) {
		String[] asVars = new String[vars.size()];
		vars.toArray(asVars);
		addExpression(bCheck, bPositive, sExpressionFormat, asVars);
	}

	public void addExpression(boolean bPositive, String sExpressionFormat, String... vars) {
		addExpression(false, bPositive, sExpressionFormat, vars);
	}
	
	public void addExpression(boolean bPositive, String sExpressionFormat, List<String> vars) {
		addExpression(false, bPositive, sExpressionFormat, vars);
	}
	
	public void addExpression(String sExpressionFormat, String... vars) {
		addExpression(false, true, sExpressionFormat, vars);
	}
	
	public void addExpression(String sExpressionFormat, List<String> vars) {
		addExpression(false, true, sExpressionFormat, vars);
	}
	
	public void addExpressionCheck(boolean bPositive, String sExpressionFormat, String... vars) {
		addExpression(true, bPositive, sExpressionFormat, vars);
	}
	
	public void addExpressionCheck(boolean bPositive, String sExpressionFormat, List<String> vars) {
		addExpression(true, bPositive, sExpressionFormat, vars);
	}
	
	public void addExpressionCheck(String sExpressionFormat, String... vars) {
		addExpression(true, true, sExpressionFormat, vars);
	}
	
	public void addExpressionCheck(String sExpressionFormat, List<String> vars) {
		addExpression(true, true, sExpressionFormat, vars);
	}
}
