package net.ellisw.network;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Expression {
	private String sExpressionFormat;
	private boolean bPositive;
	private String[] args;
	private Set<String> vars = new HashSet<String>();
	
	/**
	 * Create an expression
	 * @param bPositive false if getExpression() should return "-(sExpression)"
	 * @param sExpressionFormat The expression
	 * @param vars A list of variable name in the expression 
	 */
	public Expression(boolean bPositive, String sExpressionFormat, String... vars) {
		this.sExpressionFormat = sExpressionFormat;
		this.args = vars;
		this.bPositive = bPositive;
		for (String sVarName : vars) {
			this.vars.add(sVarName);
		}
	}
	
	/**
	 * Create an expression
	 * @param sExpressionFormat The expression
	 * @param vars A list of variable name in the expression 
	 */
	public Expression(String sExpressionFormat, String... vars) {
		this(true, sExpressionFormat, vars);
	}
	
	public String getExpressionFormat() { return sExpressionFormat; }
	
	public String[] getExpressionArgs() { return args; }
	
	public String getExpression() {
		String sExpression = String.format(sExpressionFormat, (Object[]) args);
		return sExpression;
	}

	public String getExpression(Map<String, String> map) {
		String[] newargs = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			String sArg = args[i];
			if (map.containsKey(sArg))
				newargs[i] = map.get(sArg);
			else
				newargs[i] = sArg;
		}
		String sExpression = String.format(sExpressionFormat, (Object[]) newargs);
		return sExpression;
	}

	public boolean isPositive() { return bPositive; }
	
	public boolean isPositive(boolean bPositive) {
		return (this.bPositive == bPositive);
	}
	
	/**
	 * Get a list of variable names used in the expression
	 * @return
	 */
	public Set<String> getVars() { return vars; }
}
