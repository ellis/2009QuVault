package net.ellisw.network;

public class XY {
	public int x;
	public int y;
	
	public XY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public XY(XY that) {
		load(that);
	}
	
	public XY() {
		x = 0;
		y = 0;
	}
	
	public void load(XY that) {
		x = that.x;
		y = that.y;
	}
}
