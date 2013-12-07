package net.ellisw.circuitpainter;

public class Rect {
	public Point min;
	public Point max;
		
	//public Rect() {
	//}
	
	public Rect(Point pt1, Point pt2) {
		min = new Point(pt1);
		max = new Point(min);
		expand(pt2);
	}
		
	public Rect(Line line, double nFraction, double nPadding)
	{
		setup(line, nFraction, nPadding, nPadding, nPadding, nPadding);
	}

	public Rect(Line line, double nFraction, double nLeftRightPadding, double nTopBottomPadding)
	{
		setup(line, nFraction, nLeftRightPadding, nTopBottomPadding, nLeftRightPadding, nTopBottomPadding);
	}

	public Rect(Line line, double nFraction, double nLeftPadding, double nTopPadding, double nRightPadding, double nBottomPadding)
	{
		setup(line, nFraction, nLeftPadding, nTopPadding, nRightPadding, nBottomPadding);
	}
		
	private void setup(Line line, double nFraction, double nLeftPadding, double nTopPadding, double nRightPadding, double nBottomPadding)
	{
		// Initial values
		min = max = line.atAndOff(nFraction, nLeftPadding);

		Point pt;
		pt = line.atAndOff(nFraction, -nRightPadding);
		expand(pt);
		pt = line.atAndOn(nFraction, nTopPadding);
		expand(pt);
		pt = line.atAndOn(nFraction, -nBottomPadding);
		expand(pt);
	}

	public double width() { return max.x - min.x; }
	public double height() { return max.y - min.y; }

	public void expand(Point pt)
	{
		if (pt.x < min.x)
			min.x = pt.x;
		if (pt.y < min.y)
			min.y = pt.y;
		if (pt.x > max.x)
			max.x = pt.x;
		if (pt.y > max.y)
			max.y = pt.y;
	}
}
