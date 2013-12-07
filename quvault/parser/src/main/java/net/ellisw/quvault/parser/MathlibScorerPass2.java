package net.ellisw.quvault.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeNodeStream;


public class MathlibScorerPass2 extends MathlibScorerPass1 {
	private MathlibScorerPass1 pass1;
	private Map<String, MathlibValue> correctValues = null;
	private Map<String, MathlibValue> correctedValues = new HashMap<String, MathlibValue>();

	
	public MathlibScorerPass2(MathlibScorerPass1 pass1, TreeNodeStream input) {
		super(input);
		
		this.pass1 = pass1;
	}
	
	public void runPass2(Map<String, MathlibValue> givens, List<String> asFinds, Map<String, MathlibValue> correctValues, int[] anFindMarks) throws RecognitionException {
		assert(correctValues != null);
		
		this.correctValues = correctValues;
		
		loadGivens(givens);
		
		// Load the find variables which are either
		// - not defined in the user's answer
		// - correct in the user's answer, in case the user set variables in the wrong order
		for (int iFind = 0; iFind < anFindMarks.length; iFind++) {
			if (anFindMarks[iFind] == MISSING || anFindMarks[iFind] == CORRECT_COMPLETE) {
				String sVarName = asFinds.get(iFind);
				MathlibValue value = correctValues.get(sVarName);
				super.setVar(sVarName, value);
			}
		}

		// Run the AST evaluator
		if (input.getTreeSource() != null)
			input();
		
		for (int iFind = 0; iFind < anFindMarks.length; iFind++) {
			if (anFindMarks[iFind] == WRONG) {
				String sVarName = asFinds.get(iFind);
				
				MathlibValue value = correctedValues.get(sVarName);
				MathlibValue correct = correctValues.get(sVarName);
				assert(correct != null);
				
				if (value != null && value.equals(correct))
					anFindMarks[iFind] = CORRECT_INCOMPLETE;
			}
		}
	}
	
	@Override
	public void setVar(String sVarName, MathlibValue value) {
		// Keep track of how many times this variable gets set (second pass)
		Integer n2 = varSetCounts.get(sVarName);
		if (n2 == null)
			n2 = 0;
		n2 += 1;
		varSetCounts.put(sVarName, n2);

		// Check how many times this variable got set during the first pass
		Integer n1 = pass1.varSetCounts.get(sVarName);

		// If this is the last assignment of this variable, and the value was wrong in pass 1:
		if ((int) n2 == (int) n1) {
			MathlibValue correct = correctValues.get(sVarName);
			if (correct != null) {
				System.out.println("CHANGE " + sVarName + ": " + value.toString() + " => " + correct.toString());
				correctedValues.put(sVarName, value);
				value = correct.getValueForAssignment();
			}
		}

		super.setVar(sVarName, value);
	}
}
