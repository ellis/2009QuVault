package net.ellisw.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NetworkData {
	private List<Node> nodes = new ArrayList<Node>();
	private List<Elem> elems = new ArrayList<Elem>();
	private int xMin = Integer.MAX_VALUE;
	private int xMax = Integer.MIN_VALUE;
	private int yMin = Integer.MAX_VALUE;
	private int yMax = Integer.MIN_VALUE;
	
	private String[] asElemNamesSorted = null;
	
	
	public int getXMin() { return xMin; }
	public int getXMax() { return xMax; }
	public int getYMin() { return yMin; }
	public int getYMax() { return yMax; }
	
	public List<Node> getNodes() { return nodes; }
	public List<Elem> getElems() { return elems; }
	//public Set<String> getVarNames() { return vars.keySet(); }
	
	public String[] getElemNamesSorted() {
		if (asElemNamesSorted == null)
			asElemNamesSorted = getElemNamesSorted(elems);
		return asElemNamesSorted;
	}
	
	private String[] getElemNamesSorted(List<Elem> elems) {
		String[] asNames = new String[elems.size()];
		for (int i = 0; i < asNames.length; i++) {
			asNames[i] = elems.get(i).getName();
		}
		Arrays.sort(asNames, String.CASE_INSENSITIVE_ORDER);
		return asNames;
	}
	
	public void addNode(String sName, int x, int y) {
		Node node = new Node(sName, x, y);
		nodes.add(node);
		updateMinMax(x, y);
	}
	
	public void addElem(String sName, String sNodeName1, String sNodeName2, int x0, int y0, Direction dir) {
		assert(sName != null);
		assert(sNodeName1 != null);
		assert(sNodeName2 != null);
		assert(dir != null);
		
		Node node1 = findNode(sNodeName1);
		Node node2 = findNode(sNodeName2);
		assert(node1 != null);
		assert(node2 != null);

		List<Node> nodes = new ArrayList<Node>(2);
		nodes.add(node1);
		nodes.add(node2);
		
		Elem elem = new Elem(sName, nodes, new XY(x0, y0), dir);
		elems.add(elem);
		
		node1.addElem(elem);
		node2.addElem(elem);
		
		int[] d = elem.getXYDestDiff();
		updateMinMax(x0 + d[0], y0 + d[1]);
	}
	
	private void updateMinMax(int x, int y) {
		if (x < xMin)
			xMin = x;
		else if (x > xMax)
			xMax = x;
		if (y < yMin)
			yMin = y;
		else if (y > yMax)
			yMax = y;
	}
	
	/**
	 * Return a node with the given name, or null if none found
	 * @param sName node name
	 */
	public Node findNode(String sName) {
		for (Node node : nodes) {
			if (node.getName().equals(sName))
				return node;
		}
		return null;
	}
}
