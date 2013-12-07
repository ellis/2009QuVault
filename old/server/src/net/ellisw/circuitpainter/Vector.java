package net.ellisw.circuitpainter;

public class Vector {
	public double dx;
	public double dy;

	public Vector() {
	}

	public Vector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public Vector mult(double n)
	{
		return new Vector(dx * n, dy * n);
	}

	public static Point add(Point pt, Vector v)
	{
		return new Point(pt.x + v.dx, pt.y + v.dy);
	}

	public static Point add(Vector v, Point pt)
	{
		return new Point(pt.x + v.dx, pt.y + v.dy);
	}

	public static Point sub(Point pt, Vector v)
	{
		return new Point(pt.x - v.dx, pt.y - v.dy);
	}

	public static Point sub(Vector v, Point pt)
	{
		return new Point(pt.x - v.dx, pt.y - v.dy);
	}

}
