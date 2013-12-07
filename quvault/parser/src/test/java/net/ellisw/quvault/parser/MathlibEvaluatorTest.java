package net.ellisw.quvault.parser;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for MathlibEvaluator.
 */
public class MathlibEvaluatorTest extends TestCase
{
	private static class EvalData {
		public MathlibParser parser;
		public MathlibParser.prog_return parserReturn;
		public MathlibEvaluator evaluator;
		
		public static EvalData evaluate(String sCode) throws RecognitionException {
			EvalData d = new EvalData();
			d.parse(sCode);
			d.evaluate();
			return d;
		}
		
	    public void parse(String sCode) throws RecognitionException {
	    	ANTLRStringStream str = new ANTLRStringStream(sCode);

			MathlibLexer lexer = new MathlibLexer(str);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			
			parser = new MathlibParser(tokens);
			parserReturn = parser.prog();
	    }

	    public void createEvaluator() {
			CommonTree t = (CommonTree) parserReturn.getTree();
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
			evaluator = new MathlibEvaluator(nodes);
	    }

	    public void evaluate() throws RecognitionException {
	    	createEvaluator();
			evaluator.input();
	    }
	}
	
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MathlibEvaluatorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MathlibEvaluatorTest.class );
    }

    private boolean testVar(String sCode, String sVarName, String sValueExpected) throws RecognitionException {
    	EvalData d = EvalData.evaluate(sCode);
    	String sValue = d.evaluator.getVar(sVarName).toString().trim();
        return sValue.equals(sValueExpected.trim());
    }
    
    public void testInteger() throws RecognitionException {
        assertTrue(testVar("1", "ans", "1"));
        assertTrue(testVar("-1", "ans", "-1"));
    }
    
    public void testDouble() throws RecognitionException {
        assertTrue(testVar("1.1", "ans", "1.1000"));
    }
    
    public void testImaginary() throws RecognitionException {
        assertTrue(testVar("i", "ans", "0 + 1i"));
        assertTrue(testVar("I", "ans", "0 + 1i"));
        assertTrue(testVar("j", "ans", "0 + 1i"));
        assertTrue(testVar("J", "ans", "0 + 1i"));
        assertTrue(testVar("2i", "ans", "0 + 2i"));
    }
    
    public void testComplexInteger() throws RecognitionException {
    	assertTrue(testVar("1 + 2i", "ans", "1 + 2i"));
    }
    
    public void testComplexDouble() throws RecognitionException {
    	assertTrue(testVar("1.1 + 2i", "ans", "1.1000 + 2.0000i"));
    }
    
    public void testAssignment() throws RecognitionException {
    	assertTrue(testVar("a = 1.1 + 2i", "a", "1.1000 + 2.0000i"));
    	
    	EvalData d = EvalData.evaluate("a = 1 + i\nb = 2 + 5i");
    	assertTrue(d.evaluator.getVar("a").toString().trim().equals("1 + 1i"));
    	assertTrue(d.evaluator.getVar("b").toString().trim().equals("2 + 5i"));
    }
    
    public void testMult() throws RecognitionException {
        assertTrue(testVar("i*i", "ans", "-1"));
        assertTrue(testVar("2*3*4", "ans", "24"));

    	EvalData d = EvalData.evaluate("a = 1 + i; a2 = a * 2\nb = 2 + 5i; ab = a * b");
    	assertTrue(d.evaluator.getVar("a2").toString().trim().equals("2 + 2i"));
    	assertTrue(d.evaluator.getVar("ab").toString().trim().equals("-3 + 7i"));
    }
    
    public void testDiv() throws RecognitionException {
        assertTrue(testVar("1/1", "ans", "1"));
        assertTrue(testVar("10/1", "ans", "10"));
        assertTrue(testVar("10/2", "ans", "5"));
        assertTrue(testVar("10/10", "ans", "1"));
        assertTrue(testVar("i/i", "ans", "1"));
        assertTrue(testVar("a = -3 + 7i; b = 1 + i; a / b;", "ans", "2 + 5i"));
        assertTrue(testVar("1/(1/2 + 1/3)", "ans", "1.2000"));
    }
    
    public void testSign() throws RecognitionException {
        assertTrue(testVar("-1", "ans", "-1"));
        assertTrue(testVar("-i", "ans", "-0 - 1i"));
        assertTrue(testVar("-(-1)", "ans", "1"));
        assertTrue(testVar("-0", "ans", "-0"));
    }
    
    public void testNot() throws RecognitionException {
        assertTrue(testVar("~1", "ans", "0"));
        assertTrue(testVar("!1", "ans", "0"));
        assertTrue(testVar("~i", "ans", "0"));
        assertTrue(testVar("~0", "ans", "1"));
        assertTrue(testVar("~~2", "ans", "1"));
    }
    
    public void testEndOfStatements() throws RecognitionException {
    	EvalData d = null;

    	d = EvalData.evaluate("a = 1\nb = 2\nc = 3\n");
    	assertTrue(d.parser.getErrors().isEmpty());
    	assertTrue(d.evaluator.getVar("a").equals(MathlibValue.createScalar(1, 0)));
    	assertTrue(d.evaluator.getVar("b").equals(MathlibValue.createScalar(2, 0)));
    	assertTrue(d.evaluator.getVar("c").equals(MathlibValue.createScalar(3, 0)));

    	d = EvalData.evaluate("a = 1\nb = 2\nc = 3");
    	assertTrue(d.parser.getErrors().isEmpty());
    	assertTrue(d.evaluator.getVar("a").equals(MathlibValue.createScalar(1, 0)));
    	assertTrue(d.evaluator.getVar("b").equals(MathlibValue.createScalar(2, 0)));
    	assertTrue(d.evaluator.getVar("c").equals(MathlibValue.createScalar(3, 0)));

    	d = EvalData.evaluate("a = 1; b = 2; c = 3;");
    	assertTrue(d.parser.getErrors().isEmpty());
    	assertTrue(d.evaluator.getVar("a").equals(MathlibValue.createScalar(1, 0)));
    	assertTrue(d.evaluator.getVar("b").equals(MathlibValue.createScalar(2, 0)));
    	assertTrue(d.evaluator.getVar("c").equals(MathlibValue.createScalar(3, 0)));

    	d = EvalData.evaluate("a = 1;\nb = 2;\nc = 3;\n");
    	assertTrue(d.parser.getErrors().isEmpty());
    	assertTrue(d.evaluator.getVar("a").equals(MathlibValue.createScalar(1, 0)));
    	assertTrue(d.evaluator.getVar("b").equals(MathlibValue.createScalar(2, 0)));
    	assertTrue(d.evaluator.getVar("c").equals(MathlibValue.createScalar(3, 0)));

    	d = EvalData.evaluate("a = 1\nb = 2;c = 3\n");
    	assertTrue(d.parser.getErrors().isEmpty());
    	assertTrue(d.evaluator.getVar("a").equals(MathlibValue.createScalar(1, 0)));
    	assertTrue(d.evaluator.getVar("b").equals(MathlibValue.createScalar(2, 0)));
    	assertTrue(d.evaluator.getVar("c").equals(MathlibValue.createScalar(3, 0)));
    }

    public void testParserErrors() throws RecognitionException {
    	EvalData d = new EvalData();

    	d.parse("a = 1\nb = 2\nc = 3\nd = 4\n");
    	assertTrue(d.parser.getErrors().isEmpty());

    	d.parse("a = 1\nb = 2&\nc = 3\nd = 4\n");
    	assertTrue(d.parser.getErrors().trim().equals("Line 2:6: invalid character: '&'"));
    }
    
    public void testEvaluatorErrors() throws RecognitionException {
    	EvalData d = null;

    	d = EvalData.evaluate("a = 1\nb = 2\nc = 3\nd = 4\n");
    	assertTrue(d.evaluator.getErrors().isEmpty());
    	assertTrue(d.evaluator.getVar("a") != null);
    	assertTrue(d.evaluator.getVar("b") != null);
    	assertTrue(d.evaluator.getVar("c") != null);
    	assertTrue(d.evaluator.getVar("d") != null);

    	d = EvalData.evaluate("a = 1\nb = abcd\nc = 3\nd = 4\n");
    	assertEquals("Line 2:5: Undefined variable or function: abcd", d.evaluator.getErrors().trim());
    	assertTrue(d.evaluator.getVar("a") != null);
    	assertTrue(d.evaluator.getVar("b").isNull());
    	assertTrue(d.evaluator.getVar("c") != null);
    	assertTrue(d.evaluator.getVar("d") != null);

    	d = EvalData.evaluate("a = 1\nb = 2&\nc = 3\nd = 4\n");
    	assertTrue(d.evaluator.getVar("a") != null);
    	assertTrue(d.evaluator.getVar("b") == null);
    	assertTrue(d.evaluator.getVar("c") != null);
    	assertTrue(d.evaluator.getVar("d") != null);


    	d = EvalData.evaluate("a = 1; b = 2&; c = 3; d = 4\n");
    	System.out.println(d.evaluator.getErrors());
    	assertTrue(d.evaluator.getVar("a") != null);
    	assertTrue(d.evaluator.getVar("b") == null);
    	assertTrue(d.evaluator.getVar("c").equals(MathlibValue.createScalar(3, 0)));
    	assertTrue(d.evaluator.getVar("d") != null);
    }
}
