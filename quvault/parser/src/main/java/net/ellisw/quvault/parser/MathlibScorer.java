package net.ellisw.quvault.parser;

import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

public class MathlibScorer {
	private String sErrorsSolutionParse;
	private String sErrorsAnswerParse;
	private String sErrorsAnswer;
	
	public MathlibScorer() {
	}
	
	public String getSolutionErrors() { return sErrorsSolutionParse; }
	public String getErrors() { return sErrorsAnswer; }
	
	public int[] score(List<String> asGivens, List<String> asFinds, String sSolution, String sAnswer) throws RecognitionException {
		CommonTree treeSolution = getAST(sSolution, false);
		CommonTree treeAnswer = getAST(sAnswer, true);
		
		int[] anFindMarksTotal = new int[asFinds.size()];

		sErrorsAnswerParse = "";
		String sErrorsAnswerEvaluate = "";
		for (int iPower = 1; iPower <= 2; iPower++) {
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(treeSolution);
			MathlibEvaluator walker = new MathlibEvaluator(nodes);

			HashMap<String, MathlibValue> givens = new HashMap<String, MathlibValue>();
			for (int iGiven = 0; iGiven < asGivens.size(); iGiven++) {
				String sVarName = asGivens.get(iGiven);
				// Pick a value for this given variable
				double n = Math.pow(iGiven + 2, iPower);
				MathlibValue value = MathlibValue.createScalar(n, 0);
				
				givens.put(sVarName, value);
				walker.setVar(sVarName, value);
			}
			
			// Run the evaluator on sSolution
			walker.input();
	    	System.out.println("WALKER ERRORS: " + walker.getErrors());
			
	
			// Get the correct values of the variables that the user should find
			HashMap<String, MathlibValue> correctVars = new HashMap<String, MathlibValue>();
			for (String sVarName : asFinds)
				correctVars.put(sVarName, walker.getVar(sVarName));

			nodes = new CommonTreeNodeStream(treeAnswer);
			MathlibScorerPass1 scorer1 = new MathlibScorerPass1(nodes);
			int[] anFindMarks = scorer1.runPass1(givens, asFinds, correctVars);
	    	System.out.println("SCORER1 ERRORS: " + scorer1.getErrors());
	
			nodes = new CommonTreeNodeStream(treeAnswer);
			MathlibScorerPass2 scorer2 = new MathlibScorerPass2(scorer1, nodes);
			scorer2.runPass2(givens, asFinds, correctVars, anFindMarks);
	    	System.out.println("SCORER2 ERRORS: " + scorer2.getErrors());
	    	sErrorsAnswerEvaluate = scorer2.getErrors();
			
			for (int iFind = 0; iFind < asFinds.size(); iFind++) {
				if (anFindMarks[iFind] > anFindMarksTotal[iFind])
					anFindMarksTotal[iFind] = anFindMarks[iFind];
			}
		}
		
		sErrorsAnswer = sErrorsAnswerParse + sErrorsAnswerEvaluate;
		
		System.out.println("MARKS:");
		for (int iFind = 0; iFind < asFinds.size(); iFind++)
			System.out.println(asFinds.get(iFind) + ": " + anFindMarksTotal[iFind]);

		return anFindMarksTotal;
	}

	/**
	 * Construct the AST for the given input
	 * @param sCode matlab-like code to analyze
	 * @throws RecognitionException
	 */
	private CommonTree getAST(String sCode, boolean bAnswer) throws RecognitionException {
    	ANTLRStringStream str = new ANTLRStringStream(sCode);

		MathlibLexer lexer = new MathlibLexer(str);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MathlibParser parser = new MathlibParser(tokens);
		MathlibParser.prog_return r = parser.prog();
		
		if (bAnswer)
			sErrorsAnswerParse = parser.getErrors();
		else
			sErrorsSolutionParse = parser.getErrors();
		
		CommonTree t = (CommonTree) r.getTree();
		return t;
	}
}
