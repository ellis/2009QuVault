package net.ellisw.network;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private String sName;
	private XY xy;
	private List<Elem> elems = new ArrayList<Elem>();
	
	
	public Node(String sName, XY xy) {
		assert(xy != null);
		
		this.sName = sName;
		this.xy = new XY(xy);
	}
	
	public Node(String sName, int x, int y) {
		this(sName, new XY(x, y));
	}
	
	public String getName() { return sName; }
	public void setName(String s) { sName = s; }
	
	public XY getXY() { return xy; }
	public int getX() { return xy.x; }
	public int getY() { return xy.y; }
	
	public List<Elem> getElems() { return elems; }
	
	public void addElem(Elem elem) {
		elems.add(elem);
	}
}
