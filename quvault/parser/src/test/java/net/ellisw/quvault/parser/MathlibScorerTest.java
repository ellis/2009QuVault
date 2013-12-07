package net.ellisw.quvault.parser;

import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.RecognitionException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MathlibScorerTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MathlibScorerTest(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(MathlibScorerTest.class);
    }

    private int[] testAnswer(String sAnswer) throws RecognitionException {
		List<String> asGivens = Arrays.asList(new String[] { "R1", "R2", "R3", "V" });
		List<String> asFinds = Arrays.asList(new String[] { "ZR2R3", "ZR1R2R3", "va", "iR2", "pR2" });
    	String sSolution =
    		"ZR2R3 = 1/(1/R2 + 1/R3)\n" +
			"va = V\n" +
			"ZR1R2R3 = R1 + ZR2R3\n" +
			"vb = va * ZR2R3 / ZR1R2R3\n" +
			"iR2 = vb / R2\n" +
			"pR2 = vb * iR2";

    	MathlibScorer scorer = new MathlibScorer();
    	int[] anFindMarks = scorer.score(asGivens, asFinds, sSolution, sAnswer);
    	return anFindMarks;
    }

    public void testAllCorrect() throws RecognitionException {
    	String sAnswer =
    		"ZR2R3 = 1/(1/R2 + 1/R3)\n" +
			"va = V\n" +
			"ZR1R2R3 = R1 + ZR2R3\n" +
			"vb = va * ZR2R3 / ZR1R2R3\n" +
			"iR2 = vb / R2\n" +
			"pR2 = vb * iR2";
    	int[] anFindMarks = testAnswer(sAnswer);
    	assertTrue(anFindMarks[0] == 0);
    	assertTrue(anFindMarks[1] == 0);
    	assertTrue(anFindMarks[2] == 0);
    	assertTrue(anFindMarks[3] == 0);
    	assertTrue(anFindMarks[4] == 0);
    }

    public void testLastIncorrect() throws RecognitionException {
    	String sAnswer =
    		"ZR2R3 = 1/(1/R2 + 1/R3)\n" +
			"va = V\n" +
			"ZR1R2R3 = R1 + ZR2R3\n" +
			"vb = va * ZR2R3 / ZR1R2R3\n" +
			"iR2 = vb / R2\n" +
			"pR2 = vb * iR2 + 1";
    	int[] anFindMarks = testAnswer(sAnswer);
    	assertTrue(anFindMarks[0] == 0);
    	assertTrue(anFindMarks[1] == 0);
    	assertTrue(anFindMarks[2] == 0);
    	assertTrue(anFindMarks[3] == 0);
    	assertTrue(anFindMarks[4] == 3);
    }

    public void testFirstIncorrect() throws RecognitionException {
    	String sAnswer =
    		"ZR2R3 = 1/(1/R2 + 1/R3) + 1\n" +
			"va = V\n" +
			"ZR1R2R3 = R1 + ZR2R3\n" +
			"vb = va * ZR2R3 / ZR1R2R3\n" +
			"iR2 = vb / R2\n" +
			"pR2 = vb * iR2";
    	int[] anFindMarks = testAnswer(sAnswer);
    	assertTrue(anFindMarks[0] == 3);
    	assertTrue(anFindMarks[1] == 1);
    	assertTrue(anFindMarks[2] == 0);
    	assertTrue(anFindMarks[3] == 1);
    	assertTrue(anFindMarks[4] == 1);
    }

    public void testPass1Incorrect() throws RecognitionException {
    	String sAnswer =
    		"ZR2R3 = 1/(1/3 + 1/4) + 1\n" +
			"va = 5\n" +
			"ZR1R2R3 = 2 + ZR2R3\n" +
			"vb = va * ZR2R3 / ZR1R2R3\n" +
			"iR2 = vb / 3\n" +
			"pR2 = vb * iR2";
    	int[] anFindMarks = testAnswer(sAnswer);
    	assertTrue(anFindMarks[0] == 3);
    	assertTrue(anFindMarks[1] == 3);
    	assertTrue(anFindMarks[2] == 3);
    	assertTrue(anFindMarks[3] == 3);
    	assertTrue(anFindMarks[4] == 1);
    }
}
