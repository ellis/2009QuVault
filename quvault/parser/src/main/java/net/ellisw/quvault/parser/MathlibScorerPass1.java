package net.ellisw.quvault.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeNodeStream;


public class MathlibScorerPass1 extends MathlibEvaluator {
	public final static int CORRECT_COMPLETE = 0;
	public final static int CORRECT_INCOMPLETE = 1;
	public final static int MISSING = 2;
	public final static int WRONG = 3;
	
	protected final Map<String, Integer> varSetCounts = new HashMap<String, Integer>();

	
	public MathlibScorerPass1(TreeNodeStream input) {
		super(input);
	}
	
	public int[] runPass1(Map<String, MathlibValue> givens, List<String> asFinds, Map<String, MathlibValue> correctValues) throws RecognitionException {
		assert(givens != null);
	
		loadGivens(givens);

		// Run the AST evaluator
		if (input.getTreeSource() != null)
			input();
		
		int[] an = new int[asFinds.size()];
		for (int iFind = 0; iFind < an.length; iFind++) {
			String sVarName = asFinds.get(iFind);

			MathlibValue value = getVar(sVarName);
			MathlibValue correct = correctValues.get(sVarName);
			assert(correct != null);
			
			if (value == null)
				an[iFind] = MISSING;
			else if (!value.equals(correct))
				an[iFind] = WRONG;
		}
		
		return an;
	}

	@Override
	public void setVar(String sVarName, MathlibValue value) {
		// Keep track of how many times this variable gets set (first pass)
		Integer n1 = varSetCounts.get(sVarName);
		if (n1 == null)
			n1 = 0;
		varSetCounts.put(sVarName, n1 + 1);

		super.setVar(sVarName, value);
	}

	protected void loadGivens(Map<String, MathlibValue> givens) {
		for (String sVarName : givens.keySet())
			super.setVar(sVarName, givens.get(sVarName));
	}
}
