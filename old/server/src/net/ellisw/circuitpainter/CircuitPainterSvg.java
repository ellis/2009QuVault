package net.ellisw.circuitpainter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;

import org.w3c.dom.Element;


public class CircuitPainterSvg extends CircuitPainter {
	private PrintWriter str;
	private DecimalFormat df;

	public String convertToSvg(String sXml, boolean bIncludeXmlHeader)
	{
		StringWriter buffer = new StringWriter();
		str = new PrintWriter(buffer);
		df = new DecimalFormat("#.###");

		Rect rect = processCircuitXml(sXml);
		if (rect != null) {
			str.println("</svg>");
	
			StringWriter bufferHeader = new StringWriter();
			PrintWriter header = new PrintWriter(bufferHeader);
			if (bIncludeXmlHeader) {
				header.println("<?xml version='1.0' standalone='no'?>");
				header.println("<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' 'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'>");
			}
			header.println(getSvgTag(rect, true));
	
			str.close();
			String s = buffer.toString();
			header.close();
			String sHeader = bufferHeader.toString();
	
			return sHeader + s;
		}
		else {
			return null;
		}
	}

	public String convertToSvgObject(String sXml)
	{
		StringWriter buffer = new StringWriter();
		str = new PrintWriter(buffer);
		df = new DecimalFormat("#.###");

		Rect rect = processCircuitXml(sXml);
		if (rect != null) {
			str.println("</svg>");
			str.close();
			String sSvg = getSvgTag(rect, false) + buffer.toString();
			
			String sObject = String.format("<object width='%d' height='%d' type='image/svg+xml' data=\"data:image/svg+xml,%s\"></object>",
					(int) Math.ceil(rect.width()), (int) Math.ceil(rect.height()), sSvg);
			
			return sObject;
		}
		else {
			return null;
		}
	}
	
	private String getSvgTag(Rect rect, boolean bIncludeSize) {
		String s = "<svg viewBox='" + (rect.min.x - 1) + " " + (rect.min.y - 1) + " "
			+ (rect.max.x - rect.min.x + 2) + " " + (rect.max.y - rect.min.y + 2)
			+ "' version='1.1' xmlns='http://www.w3.org/2000/svg'>";
		return s;
	}

	protected void drawLabel(String sLabel, Direction side)
	{
		double nSpacing = 4;
		double nLabelWidth = sLabel.length() * 10 + nSpacing;
		double nFontSize = 10;
		String sTextAnchor = null;
		Point ptTextMin = null;
		Point ptTextMax = null;
		Point pt = null;

		switch (side)
		{
		case Left:
			sTextAnchor = "end";
			pt = rcPadding.left().plus(-nSpacing, nFontSize / 2);
			ptTextMin = new Point(pt.x - nLabelWidth, pt.y - nFontSize);
			ptTextMax = new Point(pt.x, pt.y);
			break;
		case Right:
			sTextAnchor = "start";
			pt = rcPadding.right().plus(nSpacing, nFontSize / 2);
			ptTextMin = new Point(pt.x, pt.y - nFontSize);
			ptTextMax = new Point(pt.x + nLabelWidth, pt.y);
			break;
		case Up:
			sTextAnchor = "middle";
			pt = rcPadding.top(0, -nSpacing);
			ptTextMin = new Point(pt.x - nLabelWidth / 2, pt.y - nFontSize);
			ptTextMax = new Point(pt.x + nLabelWidth / 2, pt.y);
			break;
		case Down:
			sTextAnchor = "middle";
			pt = rcPadding.bottom().plus(0, nSpacing + nFontSize);
			ptTextMin = new Point(pt.x - nLabelWidth / 2, pt.y - nFontSize);
			ptTextMax = new Point(pt.x + nLabelWidth / 2, pt.y);
			break;
		case Undefined:
			return;
		}

		// NOTE: in the firefox SVG renderer, a font-size of 10 looks good, but
		//  ImageMagick (and rsvg) need font-size=15 for the same effect...
		str.print("<text style='font-size: 15' x='" + df.format(pt.x) + "' y='" + df.format(pt.y) + "'");

		str.print(" text-anchor='" + sTextAnchor + "'");
		// NOTE: this doesn't work in ImageMagick, so I had to adjust pt.y manually. -- ellis, 2008-08-31
		//str.print(" dominant-baseline='" + sDominantBaseline + "'");

		str.println(">" + sLabel + "</text>");

		expandViewBox(ptTextMin);
		expandViewBox(ptTextMax);
	}

	protected boolean drawCircle(Element elem)
	{
		String sTag = elem.getTagName();
		boolean bFound = true;
		boolean bFilled = false;
		if (sTag.equals("circle"))
			bFilled = false;
		else if (sTag.equals("dot"))
			bFilled = true;
		else
			bFound = false;

		if (!bFound)
			return false;

		double r = 2;
		str.print(String.format("<circle cx='%s' cy='%s' r='%s' stroke='black' stroke-width='1' ",
				df.format(from.x), df.format(from.y), r));

		str.println("fill='" + ((bFilled) ? "black" : "white") + "'/>");

		rcPadding = new Bounds(new Line(from, new Vector(0, -1)), from, r, r);
		drawLabels(elem, Direction.Up);

		expandViewBox(new Point(from.x - r, from.y - r));
		expandViewBox(new Point(from.x + r, from.y + r));

		return true;
	}

	protected Bounds drawLine(Line line)
	{
		str.println(String.format("<line x1='%s' y1='%s' x2='%s' y2='%s' stroke='black' stroke-width='1'/>", 
				df.format(line.from.x), df.format(line.from.y), df.format(line.to.x), df.format(line.to.y)));

		return new Bounds(line, line.at(0.5), 1, 0);
	}

	protected Bounds drawSource(Line line)
	{
		Point center = line.at(0.5);

		// Radius
		double r = getGridSize() / 6;

		str.println(String.format("<line x1='%s' y1='%s' x2='%s' y2='%s' stroke='black' stroke-width='1'/>",
				df.format(line.from.x), df.format(line.from.y), df.format(line.to.x), df.format(line.to.y)));
		str.println(String.format("<circle cx='%s' cy='%s' r='%s' stroke='black' stroke-width='1' fill='white'/>", 
				df.format(center.x), df.format(center.y), df.format(r)));

		return new Bounds(line, line.at(0.5), r, r);
	}

	protected Bounds drawResistor(Line line)
	{
		Point ptA = line.atAndOn(0.5, -getGridSize() / 6);
		Point ptB = line.atAndOn(0.5, getGridSize() / 6);

		Line l = new Line(ptA, ptB);
		double nPixelsOff = -getGridSize() / 18;

		str.print("<polyline stroke='black' stroke-width='1' style='fill-opacity:0' points='");
		str.print(df.format(line.from.x) + "," + df.format(line.from.y) + " " + df.format(ptA.x) + "," + df.format(ptA.y) + " ");

		// Try to keep the zig-zag resistor form consistent regardless of
		// which direction it's drawn in.
		Direction dir = line.primaryDirection();
		if (dir == Direction.Left || dir == Direction.Down)
			nPixelsOff *= 1;

		Point pt;
		pt = l.atAndOff(1.0/12, nPixelsOff);
		str.print(df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = l.atAndOff(3.0/12, -nPixelsOff);
		str.print(df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = l.atAndOff(5.0/12, nPixelsOff);
		str.print(df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = l.atAndOff(7.0/12, -nPixelsOff);
		str.print(df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = l.atAndOff(9.0/12, nPixelsOff);
		str.print(df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = l.atAndOff(11.0/12, -nPixelsOff);
		str.print(df.format(pt.x) + "," + df.format(pt.y) + " ");

		str.println(
				df.format(ptB.x) + "," + df.format(ptB.y) + " "
				+ df.format(line.to.x) + "," + df.format(line.to.y)
				+ "'/>");

		return new Bounds(line, line.at(0.5), Math.abs(nPixelsOff), getGridSize() / 6);
	}

	protected Bounds drawCapacitor(Line line)
	{
		Point ptA = line.atAndOn(0.5, -getGridSize() / 20);
		Point ptB = line.atAndOn(0.5, getGridSize() / 20);

		Line l = new Line(ptA, ptB);
		double nPixelsOff = getGridSize() / 8;
		Point pt;

		str.print("<path stroke='black' stroke-width='1' style='fill-opacity:0' d='");
		// Top connector
		str.print('M' + df.format(line.from.x) + "," + df.format(line.from.y) + " "
				+ 'L' + df.format(ptA.x) + "," + df.format(ptA.y) + " ");
		// Top capacitor line
		pt = l.atAndOff(0, nPixelsOff);
		str.print('M' + df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = l.atAndOff(0, -nPixelsOff);
		str.print('L' + df.format(pt.x) + "," + df.format(pt.y) + " ");
		// Bottom capacitor line
		pt = l.atAndOff(1, nPixelsOff);
		str.print('M' + df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = l.atAndOff(1, -nPixelsOff);
		str.print('L' + df.format(pt.x) + "," + df.format(pt.y) + " ");
		// Bottom connector
		str.println('M' + df.format(ptB.x) + "," + df.format(ptB.y) + " "
				+ 'L' + df.format(line.to.x) + "," + df.format(line.to.y)
				+ "'/>");

		return new Bounds(line, line.at(0.5), Math.abs(nPixelsOff), getGridSize() / 20);
	}

	protected Bounds drawInductor(Line line)
	{
		Point ptA = line.atAndOn(0.5, -getGridSize() / 6);
		Point ptB = line.atAndOn(0.5, getGridSize() / 6);

		Line l = new Line(ptA, ptB);
		double nXRadius = getGridSize() / 24;
		double nYRadius = nXRadius * 2;
		double nDegrees = line.angle() * 180 / Math.PI;
		Point pt;

		str.print("<path stroke='black' stroke-width='1' style='fill-opacity:0' d='");
		// Top connector
		str.print('M' + df.format(line.from.x) + "," + df.format(line.from.y) + " "
				+ 'L' + df.format(ptA.x) + "," + df.format(ptA.y) + " ");

		// First bump
		pt = l.at(0.25);
		String sXRadius = df.format(nXRadius);
		String sYRadius = df.format(nYRadius);
		String sDegrees = df.format(nDegrees);
		str.print('A' + sXRadius + "," + sYRadius + " " + sDegrees + " 0,1 " + df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = l.at(0.50);
		str.print('A' + sXRadius + "," + sYRadius + " " + sDegrees + " 0,1 " + df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = l.at(0.75);
		str.print('A' + sXRadius + "," + sYRadius + " " + sDegrees + " 0,1 " + df.format(pt.x) + "," + df.format(pt.y) + " ");
		pt = ptB;
		str.print('A' + sXRadius + "," + sYRadius + " " + sDegrees + " 0,1 " + df.format(pt.x) + "," + df.format(pt.y) + " ");

		// Bottom connector
		str.println('L' + df.format(line.to.x) + "," + df.format(line.to.y) + "'/>");

		return new Bounds(line, line.at(0.5), Math.abs(nYRadius), getGridSize() / 3);
	}
}
