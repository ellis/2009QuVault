package net.ellisw.network;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Network network = new Network();
		String[] asGivens = new String[] { "V", "R1", "R2", "R3" };
		
		Map<String, String> translations = new HashMap<String, String>();

		int i = 5;
		// V: R1 | R2
		if (i == 0) {
			asGivens = new String[] { "V", "R1", "R2" };
			network.addNode("a", 1, 1);
			network.addNode("b", 1, 0);
			network.addElem("V",  "b", "a", 0, 1, Direction.Down);
			network.addElem("R1", "a", "b", 1, 1, Direction.Down);
			network.addElem("R2", "a", "b", 2, 1, Direction.Down);
		}
		// V: R1 + (R2 | R3)
		else if (i == 1) {
			network.addNode("a", 0, 1);
			network.addNode("b", 2, 1);
			network.addNode("0", 1, 0);
			network.addElem("V",  "0", "a", 0, 1, Direction.Down);
			network.addElem("R1", "a", "b", 0, 1, Direction.Right);
			network.addElem("R2", "b", "0", 1, 1, Direction.Down);
			network.addElem("R3", "b", "0", 2, 1, Direction.Down);
		}
		// R1 | (R2 + R3)
		else if (i == 2) {
			network.addNode("a", 0, 1);
			network.addNode("b", 2, 1);
			network.addNode("0", 1, 0);
			network.addElem("V",  "0", "a", 0, 1, Direction.Down);
			network.addElem("R1", "a", "0", 1, 1, Direction.Down);
			network.addElem("R2", "a", "b", 1, 1, Direction.Right);
			network.addElem("R3", "b", "0", 2, 1, Direction.Down);
		}
		// I: R1 + (R2 | R3)
		else if (i == 3) {
			asGivens = new String[] { "I", "R1", "R2", "R3" };
			network.addNode("a", 0, 1);
			network.addNode("b", 2, 1);
			network.addNode("0", 1, 0);
			network.addElem("I",  "0", "a", 0, 1, Direction.Down);
			network.addElem("R1", "a", "b", 0, 1, Direction.Right);
			network.addElem("R2", "b", "0", 1, 1, Direction.Down);
			network.addElem("R3", "b", "0", 2, 1, Direction.Down);
		}
		// V: R + C
		else if (i == 4) {
			asGivens = new String[] { "V", "R", "C", "w" };
			network.addNode("a", 0, 1);
			network.addNode("b", 1, 1);
			network.addNode("0", 0, 0);
			network.addElem("V",  "0", "a", 0, 0, Direction.Up);
			network.addElem("R", "a", "b", 0, 1, Direction.Right);
			network.addElem("C", "b", "0", 1, 1, Direction.Down);
		}
		// V: R + C, { iR -> i, iC -> i }
		else if (i == 5) {
			asGivens = new String[] { "V", "R", "C", "w" };
			network.addNode("a", 0, 1);
			network.addNode("b", 1, 1);
			network.addNode("0", 0, 0);
			network.addElem("V",  "0", "a", 0, 0, Direction.Up);
			network.addElem("R", "a", "b", 0, 1, Direction.Right);
			network.addElem("C", "b", "0", 1, 1, Direction.Down);

			translations.put("iV", "i");
			translations.put("iR", "i");
			translations.put("iC", "i");
		}
		// V: R1 + L2
		else if (i == 6) {
			asGivens = new String[] { "V", "R1", "C2" };
			network.addNode("a", 0, 1);
			network.addNode("b", 1, 1);
			network.addNode("0", 0, 0);
			network.addElem("V",  "0", "a", 0, 0, Direction.Up);
			network.addElem("R1", "a", "b", 0, 1, Direction.Right);
			network.addElem("L2", "b", "0", 1, 1, Direction.Down);
		}
		// V: R1 + L2 + C3
		else if (i == 7) {
			asGivens = new String[] { "V", "R", "L", "C" };
			network.addNode("a", 0, 1);
			network.addNode("b", 1, 1);
			network.addNode("c", 2, 1);
			network.addNode("0", 0, 0);
			network.addElem("V", "0", "a", 0, 0, Direction.Up);
			network.addElem("R", "a", "b", 0, 1, Direction.Right);
			network.addElem("L", "b", "c", 1, 1, Direction.Right);
			network.addElem("C", "c", "0", 2, 1, Direction.Down);
		}

		network.createVars(translations);
		System.out.println();

		Solver solver = null;
		List<String> as = new ArrayList<String>();
		
		if (false) {
			Map<Solver.Item, Solver> mapSolution = network.getDiffEqs();
			for (Solver.Item itemDiffeq : mapSolution.keySet()) {
				Solver vc = mapSolution.get(itemDiffeq);
				String sSolution = vc.getSolution(itemDiffeq.var.getName());
				System.out.println(sSolution);
				System.out.println(vc.getSingleExpressionFor("vc''"));
			}
		}

		if (false) {
			solver = network.getFindables(asGivens);
			as = solver.getListOfVarNames(solver.exprs);
			System.out.println(solver.getSolution(as));
		}
		
		if (false) {
			solver = network.getFindables(asGivens);
			System.out.println(solver.getSingleExpressionFor("iC"));
		}
		
		/*
		LinkedHashMap<String, Expression> mapSolutions = network.getFindablesForGivens(asGivens);
		System.out.println("SOLUTION FOR vb, pR2:");
		//System.out.println(network.getSolution(mapSolutions, "vb", "pR2"));
		System.out.println(network.getDiffEq());
		//for (String s : asFindables)
			//System.out.println(s);
		*/
		if (true) {
			DisplayParams display = new DisplayParams();
			display.displayNodes = DisplayNodes.Include;
			display.nodes.add("a");
			Element xmlNetwork = network.getDisplayXml(display);
			System.out.println(toString(xmlNetwork));
		}
	}

	private static String toString(Element elem) {
		try {
			OutputFormat fmt = new OutputFormat();
			fmt.setNewlines(true);
			fmt.setIndent("\t");
			
			StringWriter sw = new StringWriter();
			XMLWriter w = new XMLWriter(sw, fmt);
			w.write(elem);
			return sw.toString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return "<error>" + "</error>";
		}
	}
}
