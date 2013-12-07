package net.ellisw.network;

import java.util.ArrayList;
import java.util.List;

public class Elem {
	private String sName;
	private List<Node> nodes = new ArrayList<Node>();
	private XY xyBase;
	private Direction dir;
	
	
	public Elem(String sName, List<Node> nodes, XY xyBase, Direction dir) {
		this.sName = sName;
		this.nodes.addAll(nodes);
		this.xyBase = (xyBase == null) ? null : new XY(xyBase);
		this.dir = dir;
	}
	
	public String getName() { return sName; }
	public List<Node> getNodes() { return nodes; }
	public XY getXYBase() { return xyBase; }
	public Direction getDirection() { return dir; }

	/**
	 * Return two integers in an array: first integer is change in x, second is change in y
	 */
	public int[] getXYDestDiff() {
		int[] d = new int[2];
		switch (getDirection()) {
		case Right: d[0] = 1; break;
		case Left:  d[0] = -1; break;
		case Up:    d[1] = 1; break;
		case Down:  d[1] = -1; break;
		}
		return d;
	}
}
