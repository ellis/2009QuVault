package net.ellisw.quvault.parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;


public class MathlibTest {

	public static void main(String[] args) throws IOException, RecognitionException {
		CharStream str = null;
		if (args.length == 1 && args[0].equals("-"))
			str = new ANTLRInputStream(System.in);
		else {
			String[] as = new String[] {
				"a = 1\nb= 4&4\nc = 3\nd = 4"
			};
			String s = as[0];
			str = new ANTLRStringStream(s);
		}
		
		MathlibLexer lexer = new MathlibLexer(str);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MathlibParser parser = new MathlibParser(tokens);
		MathlibParser.prog_return r = parser.prog();
		System.out.println("Parser errors: " + parser.getErrors());
		
		/*CommonTree t = (CommonTree) r.getTree();
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
		MathlibEvaluator walker = new MathlibEvaluator(nodes);
		walker.input();*/
		
		//System.out.println("---------");
	
		//x();
	}
	
	private static void x() throws RecognitionException {
		List<String> asGivens = Arrays.asList(new String[] { "R1", "R2", "R3", "V" });
		List<String> asFinds = Arrays.asList(new String[] { "ZR2R3", "ZR1R2R3", "va", "iR2", "pR2" });
    	String sSolution =
    		"ZR2R3 = 1/(1/R2 + 1/R3)\n" +
			"va = V\n" +
			"ZR1R2R3 = R1 + ZR2R3\n" +
			"vb = va * ZR2R3 / ZR1R2R3\n" +
			"iR2 = vb / R2\n" +
			"pR2 = vb * iR2";
    	String sAnswer =
    		"ZR2R3 = 1/(1/R2 + 1/R3) + 1\n" +
			"va = V\n" +
			"ZR1R2R3 = R1 + ZR2R3\n" +
			"vb = va * ZR2R3 / ZR1R2R3\n" +
			"iR2 = vb / R2\n" +
			"pR2 = vb * iR2";
    	sAnswer = "va = V\niR2 = iR2";
    	sAnswer = "a = 1\nb= 4&4\nc = 3";
    	sAnswer = "";

    	MathlibScorer scorer = new MathlibScorer();
    	scorer.score(asGivens, asFinds, sSolution, sAnswer);
	}
}
