package net.ellisw.circuitpainter;

public class Line {
	public final Point from;
	public final Point to;

	public Line() {
		from = new Point();
		to = new Point();
	}
	
	public Line(Point from, Point to) {
		this.from = new Point(from);
		this.to = new Point(to);
	}
	
	public Line(Point from, Vector v) {
		this.from = new Point(from);
		this.to = new Point(from.x + v.dx, from.y + v.dy);
	}
	
	/** Return the point that is 'nFraction' of the way to 'to' from 'from' */
	public Point at(double nFraction) {
		return new Point(
			from.x + (to.x - from.x) * nFraction,
			from.y + (to.y - from.y) * nFraction);
	}
	
	/** Swap from/to points */
	void swap() {
		Point pt = new Point(to);
		to.load(from);
		from.load(pt);
	}

	/** Is the line connecting the nodes more left, right, up, or down? */
	public Direction primaryDirection() 
	{
		double dx = to.x - from.x;
		double dy = to.y - from.y;

		if (dx == 0 && dy == 0)
			return Direction.Undefined;
		else if (Math.abs(dx) >= Math.abs(dy))
		{
			if (dx >= 0)
				return Direction.Right;
			else
				return Direction.Left;
		}
		else
		{
			if (dy <= 0)
				return Direction.Up;
			else
				return Direction.Down;
		}
	}

	/** Get the unit vector parallel to this line */
	public Vector unitVector() 
	{
		// Find the unit vector components
		double dx = to.x - from.x;
		double dy = to.y - from.y;
		double r = Math.sqrt(dx * dx + dy * dy);
		if (r != 0)
		{
			dx /= r;
			dy /= r;
		}

		return new Vector(dx, dy);
	}

	/** Get the unit vector parallel to this line */
	public Vector unitNormalVector() 
	{
		double dx = to.x - from.x;
		double dy = to.y - from.y;
		// Find the perpendicular unit vector components
		double r = Math.sqrt(dx * dx + dy * dy);
		if (r != 0)
		{
			dx = -dy / r;
			dy = dx / r;
		}

		return new Vector(dx, dy);
	}

	/** Get the angle in radians of the vector from 'from' to 'to' */
	public double angle() 
	{
		double dx = to.x - from.x;
		double dy = to.y - from.y;
		if (dx != 0)
		{
			double a = Math.atan(dy / dx);
			if (dx < 0)
				a += Math.PI;
			return a;
		}
		else
			return Math.PI / 2;
	}

	/** Return the point that is at(nFraction) and off on the perpendicular vector by nPixelsOff */
	public Point atAndOff(double nFraction, double nPixelsOff) 
	{
		double dx = to.x - from.x;
		double dy = to.y - from.y;

		Point pt = new Point(
				from.x + dx * nFraction,
				from.y + dy * nFraction);

		// Find the normalized perpendicular vector components
		double r = Math.sqrt(dx * dx + dy * dy);
		if (r != 0)
		{
			double nx = -dy / r;
			double ny = dx / r;

			pt.x += nx * nPixelsOff;
			pt.y += ny * nPixelsOff;
		}

		return pt;
	}

	/** Return the point that is at(nFraction) and then backwards or fordwards by nPixelsOn */
	public Point atAndOn(double nFraction, double nPixelsOn) 
	{
		double dx = to.x - from.x;
		double dy = to.y - from.y;

		Point pt = new Point(
				from.x + dx * nFraction,
				from.y + dy * nFraction);

		// Find the normalized perpendicular vector components
		double r = Math.sqrt(dx * dx + dy * dy);
		if (r != 0)
		{
			dy /= r;
			dx /= r;

			pt.x += dx * nPixelsOn;
			pt.y += dy * nPixelsOn;
		}

		return pt;
	}
}
