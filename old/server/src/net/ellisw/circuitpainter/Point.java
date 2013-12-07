package net.ellisw.circuitpainter;

public class Point {
	public double x;
	public double y;
	
	public Point() {
	}
	
	public Point(Point other) {
		x = other.x;
		y = other.y;
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void load(Point other) {
		x = other.x;
		y = other.y;
	}

	public Point plus(double dx, double dy) {
		return new Point(x + dx, y + dy);
	}

	public Point minus(double dx, double dy) {
		return new Point(x - dx, y - dy);
	}
}
