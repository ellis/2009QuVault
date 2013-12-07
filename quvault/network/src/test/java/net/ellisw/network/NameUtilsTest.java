package net.ellisw.network;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NameUtilsTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public NameUtilsTest(String testName)
	{
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(NameUtilsTest.class);
	}

	private Elem createElem(String sName, Node nodePos, Node nodeNeg) {
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(nodePos);
		nodes.add(nodeNeg);
		Elem elem = new Elem(sName, nodes, new XY(0, 0), Direction.Down);
		return elem;
	}

	public void testNamesIPZ() {
		NameUtils u = new NameUtils();
		Node nodeA = new Node("a", new XY(0, 0));
		Node nodeB = new Node("b", new XY(0, 0));
		//Node node0 = new Node("0", new XY(0, 0));
		Elem elemR1 = createElem("R1", nodeA, nodeB);
		Elem elemC2 = createElem("C2", nodeA, nodeB);
		Elem elemL3 = createElem("L3", nodeA, nodeB);
		Elem elemV1 = createElem("V1", nodeA, nodeB);
		Elem elemI1 = createElem("I1", nodeA, nodeB);

		assertEquals("iR1", u.getVarNameI(elemR1));
		assertEquals("iC2", u.getVarNameI(elemC2));
		assertEquals("iL3", u.getVarNameI(elemL3));
		assertEquals("iV1", u.getVarNameI(elemV1));
		assertEquals("I1", u.getVarNameI(elemI1));

		assertEquals("pR1", u.getVarNameP(elemR1));
		assertEquals("pC2", u.getVarNameP(elemC2));
		assertEquals("pL3", u.getVarNameP(elemL3));
		assertEquals("pV1", u.getVarNameP(elemV1));
		assertEquals("pI1", u.getVarNameP(elemI1));

		assertEquals("R1", u.getVarNameZ(elemR1));
		assertEquals("ZC2", u.getVarNameZ(elemC2));
		assertEquals("ZL3", u.getVarNameZ(elemL3));
	}

	public void testVarDataI() {
		NameUtils u = new NameUtils();
		Node nodeA = new Node("a", new XY(0, 0));
		Node nodeB = new Node("b", new XY(0, 0));
		//Node node0 = new Node("0", new XY(0, 0));
		Elem elemR1 = createElem("R1", nodeA, nodeB);
		Elem elemC2 = createElem("C2", nodeA, nodeB);
		Elem elemL3 = createElem("L3", nodeA, nodeB);
		Elem elemV1 = createElem("V1", nodeA, nodeB);
		Elem elemI1 = createElem("I1", nodeA, nodeB);

		NameUtils.VarData d = null;
		
		d = u.getVarDataI(elemR1);
		assertEquals("iR1", d.sName);
		assertEquals("i<sub>R1</sub>", d.sNameXhtml);

		d = u.getVarDataI(elemC2);
		assertEquals("iC2", d.sName);
		assertEquals("i<sub>C2</sub>", d.sNameXhtml);

		d = u.getVarDataI(elemL3);
		assertEquals("iL3", d.sName);
		assertEquals("i<sub>L3</sub>", d.sNameXhtml);

		d = u.getVarDataI(elemV1);
		assertEquals("iV1", d.sName);
		assertEquals("i<sub>V1</sub>", d.sNameXhtml);

		d = u.getVarDataI(elemI1);
		assertEquals("I1", d.sName);
		assertEquals("I1", d.sNameXhtml);
	}

	public void testVarDataP() {
		NameUtils u = new NameUtils();
		Node nodeA = new Node("a", new XY(0, 0));
		Node nodeB = new Node("b", new XY(0, 0));
		//Node node0 = new Node("0", new XY(0, 0));
		Elem elemR1 = createElem("R1", nodeA, nodeB);
		Elem elemC2 = createElem("C2", nodeA, nodeB);
		Elem elemL3 = createElem("L3", nodeA, nodeB);
		Elem elemV1 = createElem("V1", nodeA, nodeB);
		Elem elemI1 = createElem("I1", nodeA, nodeB);

		NameUtils.VarData d = null;
		
		d = u.getVarDataP(elemR1);
		assertEquals("pR1", d.sName);
		assertEquals("p<sub>R1</sub>", d.sNameXhtml);

		d = u.getVarDataP(elemC2);
		assertEquals("pC2", d.sName);
		assertEquals("p<sub>C2</sub>", d.sNameXhtml);

		d = u.getVarDataP(elemL3);
		assertEquals("pL3", d.sName);
		assertEquals("p<sub>L3</sub>", d.sNameXhtml);

		d = u.getVarDataP(elemV1);
		assertEquals("pV1", d.sName);
		assertEquals("p<sub>V1</sub>", d.sNameXhtml);

		d = u.getVarDataP(elemI1);
		assertEquals("pI1", d.sName);
		assertEquals("p<sub>I1</sub>", d.sNameXhtml);
	}
	
	public void testVarDataV() {
		NameUtils u = new NameUtils();
		Node nodeA = new Node("a", new XY(0, 0));
		Node nodeB = new Node("b", new XY(0, 0));
		Node node0 = new Node("0", new XY(0, 0));
		Elem elemR1 = createElem("R1", nodeA, nodeB);
		Elem elemR2 = createElem("R2", nodeB, nodeA);
		Elem elemR3 = createElem("R3", nodeA, node0);
		Elem elemR4 = createElem("R4", node0, nodeA);

		NameUtils.VarData d = null;
		
		d = u.getVarDataV(elemR1);
		assertEquals("vab", d.sName);
		assertEquals("v<sub>ab</sub>", d.sNameXhtml);
		assertEquals(true, d.nodes.bPos);
		
		d = u.getVarDataV(elemR2);
		assertEquals("vab", d.sName);
		assertEquals("v<sub>ab</sub>", d.sNameXhtml);
		assertEquals(false, d.nodes.bPos);
		
		d = u.getVarDataV(elemR3);
		assertEquals("va", d.sName);
		assertEquals("v<sub>a</sub>", d.sNameXhtml);
		assertEquals(true, d.nodes.bPos);
		
		d = u.getVarDataV(elemR4);
		assertEquals("va", d.sName);
		assertEquals("v<sub>a</sub>", d.sNameXhtml);
		assertEquals(false, d.nodes.bPos);
	}

	public void testVarDataZ() {
		NameUtils u = new NameUtils();
		Node nodeA = new Node("a", new XY(0, 0));
		Node nodeB = new Node("b", new XY(0, 0));
		//Node node0 = new Node("0", new XY(0, 0));
		Elem elemR1 = createElem("R1", nodeA, nodeB);
		Elem elemC2 = createElem("C2", nodeA, nodeB);
		Elem elemL3 = createElem("L3", nodeA, nodeB);

		NameUtils.VarData d = null;
		
		d = u.getVarDataZ(elemR1);
		assertEquals("R1", d.sName);
		assertEquals("R1", d.sNameXhtml);

		d = u.getVarDataZ(elemC2);
		assertEquals("ZC2", d.sName);
		assertEquals("Z<sub>C2</sub>", d.sNameXhtml);

		d = u.getVarDataZ(elemL3);
		assertEquals("ZL3", d.sName);
		assertEquals("Z<sub>L3</sub>", d.sNameXhtml);
		
		List<Elem> l = new ArrayList<Elem>();
		l.add(elemR1);
		l.add(elemC2);
		d = u.getVarDataZ(l);
		assertEquals("ZC2R1", d.sName);
		assertEquals("Z<sub>C2,R1</sub>", d.sNameXhtml);
		Elem elemZC2R1 = createElem(d.sName, nodeA, nodeB);

		l.clear();
		l.add(elemZC2R1);
		l.add(elemL3);
		d = u.getVarDataZ(l);
		assertEquals("ZC2L3R1", d.sName);
		assertEquals("Z<sub>C2,L3,R1</sub>", d.sNameXhtml);

		l.clear();
		l.add(elemL3);
		l.add(elemZC2R1);
		d = u.getVarDataZ(l);
		assertEquals("ZC2L3R1", d.sName);
		assertEquals("Z<sub>C2,L3,R1</sub>", d.sNameXhtml);
	}
}
