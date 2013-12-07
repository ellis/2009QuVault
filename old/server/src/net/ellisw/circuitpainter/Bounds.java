package net.ellisw.circuitpainter;

/** This class represents the rectangle bounds of a circuit item of an arbitrary angle */
public class Bounds {
	private Point ptCenter;
	private Vector vRight;
	private Vector vUp;
	private double nWidth;
	private double nHeight;

	public Bounds() {
		nWidth = 0;
		nHeight = 0;
	}

	public Bounds(Line line, Point ptCenter, double nWidth, double nHeight)
	{
		init(line, ptCenter, nWidth, nHeight);
	}

	public void init(Line line, Point ptCenter, double nWidth, double nHeight)
	{
		Direction dir = line.primaryDirection();

		// Reorient the line to be pointing towards the right
		switch (dir)
		{
		case Right:
			vRight = line.unitVector();
			this.nWidth = nHeight;
			this.nHeight = nWidth;
			break;
		case Left:
			vRight = line.unitVector().mult(-1);
			this.nWidth = nHeight;
			this.nHeight = nWidth;
			break;
		case Up:
			vRight = line.unitNormalVector();
			this.nWidth = nWidth;
			this.nHeight = nHeight;
			break;
		case Down:
			vRight = line.unitNormalVector().mult(-1);
			this.nWidth = nWidth;
			this.nHeight = nHeight;
			break;
		case Undefined:
			vRight = new Vector(0, 0);
			break;
		}

		vUp = new Vector(-vRight.dy, vRight.dx);

		this.ptCenter = new Point(ptCenter);
	}

	public Point center()
	{
		return ptCenter;
	}

	public Point left()
	{
		Point pt = Vector.sub(ptCenter, vRight.mult(nWidth));
		return pt;
	}

	Point right()
	{
		Point pt = Vector.add(ptCenter, vRight.mult(nWidth));
		return pt;
	}

	Point top(double dx, double dy)
	{
		Point pt = Vector.add(Vector.sub(ptCenter, vUp.mult(nHeight - dy)), vRight.mult(dx));
		return pt;
	}

	Point top()
	{
		Point pt = Vector.sub(ptCenter, vUp.mult(nHeight));
		return pt;
	}

	Point bottom()
	{
		Point pt = Vector.add(ptCenter, vUp.mult(nHeight));
		return pt;
	}

	Point topLeft()
	{
		return Vector.sub(top(), vRight.mult(nWidth));
	}

	Point topRight()
	{
		return Vector.add(top(), vRight.mult(nWidth));
	}

	Point bottomLeft()
	{
		return Vector.sub(bottom(), vRight.mult(nWidth));
	}

	Point bottomRight()
	{
		return Vector.add(bottom(), vRight.mult(nWidth));
	}
}
