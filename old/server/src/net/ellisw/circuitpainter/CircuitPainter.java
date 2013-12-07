package net.ellisw.circuitpainter;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


public abstract class CircuitPainter {
	private double nGridSize;
	protected Point from;
	private Point min = new Point();
	private Point max = new Point();
	protected  Direction prevDir;

	/** Padding required around most recently drawn element for labels */
	protected Bounds rcPadding;
	
	protected double getGridSize() { return nGridSize; }
	
	protected Rect processCircuitXml(String sXml)
	{
		Rect rect = null;
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(sXml)));

			nGridSize = 90;
			from = new Point();
			prevDir = Direction.Up;

			Element root = doc.getDocumentElement();
			Node node = root.getFirstChild();
			while (node != null)
			{
				if (node instanceof Element)
				{
					Element elem = (Element) node;
					String sFrom = elem.getAttribute("from");
					if (sFrom != null && !sFrom.isEmpty())
					{
						String[] asCoords = sFrom.split(",");
						if (asCoords.length != 2)
							return null;

						double x = Double.parseDouble(asCoords[0]);
						double y = -Double.parseDouble(asCoords[1]);

						from.x = x * nGridSize;
						from.y = y * nGridSize;
					}

					if (!drawCircle(elem))
						drawTwoNodeItem(elem);
				}
				node = node.getNextSibling();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		rect = new Rect(min, max);
		return rect;
	}
	
	/*
	private void save(String sSvg, double nWidth, double nHeight, String sFilename) {
		try {
			// Create a JPEG transcoder
			JPEGTranscoder t = new JPEGTranscoder();

			// Set the transcoding hints.
			t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
					new Float(.8));

			// Create the transcoder input.
			TranscoderInput input = new TranscoderInput(new StringReader(sSvg));

			// Create the transcoder output.
			OutputStream ostream = new FileOutputStream(sFilename);
			TranscoderOutput output = new TranscoderOutput(ostream);

			// Save the image.
			t.transcode(input, output);

			// Flush and close the stream.
			ostream.flush();
			ostream.close();
			System.exit(0);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	*/

	protected void expandViewBox(Point pt)
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

	private void drawLabels(Element elem, Line line)
	{
		// Is the line connecting the nodes more left, right, up, or down?
		Direction dir = line.primaryDirection();
		if (dir == Direction.Undefined)
			dir = prevDir;

		drawLabels(elem, dir);
	}

	protected void drawLabels(Element elem, Direction dir)
	{
		String sLabel = elem.getAttribute("label-left");
		if (!sLabel.isEmpty())
		{
			Direction side;
			if (dir == Direction.Up)
				side = Direction.Left;
			else if (dir == Direction.Down)
				side = Direction.Right;
			else if (dir == Direction.Right)
				side = Direction.Up;
			else
				side = Direction.Down;

			drawLabel(sLabel, side);
		}

		sLabel = elem.getAttribute("label-right");
		if (!sLabel.isEmpty())
		{
			Direction side;
			if (dir == Direction.Up)
				side = Direction.Right;
			else if (dir == Direction.Down)
				side = Direction.Left;
			else if (dir == Direction.Right)
				side = Direction.Down;
			else
				side = Direction.Up;

			drawLabel(sLabel, side);
		}

		sLabel = elem.getAttribute("label-top");
		if (!sLabel.isEmpty())
		{
			Direction side;
			if (dir == Direction.Up)
				side = Direction.Up;
			else if (dir == Direction.Down)
				side = Direction.Down;
			else if (dir == Direction.Right)
				side = Direction.Right;
			else
				side = Direction.Left;

			drawLabel(sLabel, side);
		}

	}

	private boolean drawTwoNodeItem(Element elem)
	{
		double x2 = from.x / nGridSize;
		double y2 = from.y / nGridSize;

		try {
			String sTo = elem.getAttribute("to");
			if (!sTo.isEmpty())
			{
				String[] asCoords = sTo.split(",");
				if (asCoords.length != 2)
					return false;

				x2 = Double.parseDouble(asCoords[0]);
				y2 = -Double.parseDouble(asCoords[1]);
			}
			else
			{
				String sDx = elem.getAttribute("dx");
				String sDy = elem.getAttribute("dy");
				if ((sDx == null || sDx.isEmpty()) && (sDy == null || sDy.isEmpty()))
					return false;

				if (sDx != null && !sDx.isEmpty())
				{
					if (sDx.startsWith("+"))
						sDx = sDx.substring(1);
					double dx = Double.parseDouble(sDx);
					x2 += dx;
				}
				if (sDy != null && !sDy.isEmpty())
				{
					if (sDy.startsWith("+"))
						sDy = sDy.substring(1);
					double dy = -Double.parseDouble(sDy);
					y2 += dy;
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		x2 *= nGridSize;
		y2 *= nGridSize;

		expandViewBox(from);
		expandViewBox(new Point(x2, y2));

		Line line = new Line(from, new Point(x2, y2));

		boolean bFound = true;
		String sTag = elem.getTagName();
		if (sTag.equals("source"))
			rcPadding = drawSource(line);
		else if (sTag.equals("resistor"))
			rcPadding = drawResistor(line);
		else if (sTag.equals("capacitor"))
			rcPadding = drawCapacitor(line);
		else if (sTag.equals("inductor"))
			rcPadding = drawInductor(line);
		else if (sTag.equals("line"))
			rcPadding = drawLine(line);
		else
			bFound = false;

		expandViewBox(rcPadding.topLeft());
		expandViewBox(rcPadding.topRight());
		expandViewBox(rcPadding.bottomLeft());
		expandViewBox(rcPadding.bottomRight());

		/*str.print("<polyline stroke='black' stroke-width='1' style='fill-opacity:0' points='";
		str.print(rcPadding.topLeft().x + "," + rcPadding.topLeft().y + " "
			+ rcPadding.topRight().x + "," + rcPadding.topRight().y + " "
			+ rcPadding.bottomRight().x + "," + rcPadding.bottomRight().y + " "
			+ rcPadding.bottomLeft().x + "," + rcPadding.bottomLeft().y + "'/>" + endl;*/

		drawLabels(elem, line);

		from.x = x2;
		from.y = y2;

		return bFound;
	}

	protected abstract void drawLabel(String sLabel, Direction side);
	protected abstract boolean drawCircle(Element elem);
	protected abstract Bounds drawLine(Line line);
	protected abstract Bounds drawSource(Line line);
	protected abstract Bounds drawResistor(Line line);
	protected abstract Bounds drawCapacitor(Line line);
	protected abstract Bounds drawInductor(Line line);
}
