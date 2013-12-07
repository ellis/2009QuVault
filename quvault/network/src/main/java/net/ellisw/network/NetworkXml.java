package net.ellisw.network;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class NetworkXml {
	private List<Node> nodes = new ArrayList<Node>();
	private List<Elem> elems = new ArrayList<Elem>();
	private int xMin = Integer.MAX_VALUE;
	private int xMax = Integer.MIN_VALUE;
	private int yMin = Integer.MAX_VALUE;
	private int yMax = Integer.MIN_VALUE;
	
	
	private NetworkXml(NetworkData data) {
		nodes = data.getNodes();
		elems = data.getElems();
		xMin = data.getXMin();
		xMax = data.getXMax();
		yMin = data.getYMin();
		yMax = data.getYMax();
	}
	
	public static Element getDisplayXml(NetworkData data, DisplayParams display) {
		NetworkXml x = new NetworkXml(data);
		return x.getNetworkXml(display);
	}
	
	public Element getNetworkXml(DisplayParams display) {
		Element root = DocumentHelper.createElement("network");
		
		int nCols = xMax - xMin + 1;
		int nRows = yMax - yMin + 1; 
		byte[][] hwires = new byte[nCols][nRows];
		byte[][] vwires = new byte[nCols][nRows];

		for (Elem elem : elems) {
			char c = elem.getName().charAt(0);
			if ("RCLVI".indexOf(c) >= 0) {
				createBiNodalsElemXml(display, elem, root, hwires, vwires);
				markAutomaticWires(elem, hwires, vwires);
			}
		}
		
		for (int iCol = 0; iCol < nCols; iCol++) {
			for (int iRow = 0; iRow < nRows; iRow++) {
				if (hwires[iCol][iRow] > 0) {
					int x = iCol - xMin;
					int y = iRow - yMin;
					root.addElement("line")
						.addAttribute("from", x + "," + y)
						.addAttribute("to", (x + 1) + "," + y);
				}
			}
		}
		
		for (Node node : nodes) {
			int x = node.getX();
			int y = node.getY();
			
			String sLabelSide = "";
			int iCol = x - xMin;
			int iRow = y - yMin;
			if (iRow == 0)
				sLabelSide = "bottom";
			else if (iRow == yMax)
				sLabelSide = "top";
			else if (iCol == 0)
				sLabelSide = "left";
			else if (iCol == xMax)
				sLabelSide = "right";
			else if (vwires[iCol][iRow] == 0)
				sLabelSide = "top";
			else if (hwires[iCol - 1][iRow] == 0)
				sLabelSide = "left";
			else if (hwires[iCol][iRow] == 0)
				sLabelSide = "right";
		
			if (node.getName().equals("0")) {
				root.addElement("ground")
					.addAttribute("from", x + "," + y);
			}
			else {
				boolean bShow = true;
				switch (display.displayNodes) {
				case All: bShow = true; break;
				case None: bShow = false; break;
				case Include: bShow = display.nodes.contains(node.getName()); break;
				case Exclude: bShow = !display.nodes.contains(node.getName()); break;
				}

				if (bShow) {
					root.addElement("circle")
						.addAttribute("from", x + "," + y)
						.addAttribute("label-" + sLabelSide, node.getName());
				}
			}
		}
		
		return root;
	}
	
	public static Element getDisplayXml(NetworkData data) {
		DisplayParams display = new DisplayParams();
		return getDisplayXml(data, display);
	}
	
	private void createBiNodalsElemXml(DisplayParams display, Elem elem, Element root, byte[][] hwires, byte[][] vwires) {
		char c = elem.getName().charAt(0);

		String sTag = null;
		String sStyle = null;
		if (c == 'R') sTag = "resistor";
		else if (c == 'C') sTag = "capacitor";
		else if (c == 'L') sTag = "inductor";
		else if (c == 'V') { sTag = "source"; sStyle="v"; }
		else if (c == 'I') { sTag = "source"; sStyle="i"; }
		
		int x0 = elem.getXYBase().x;
		int y0 = elem.getXYBase().y;
		Element xmlElem = root.addElement(sTag);
		xmlElem.addAttribute("from", x0 + "," + y0);
		
		int[] d = elem.getXYDestDiff();
		int dx = d[0];
		int dy = d[1];
		if ((dx == 0) ^ (dy == 0)) {
			int x1 = x0 + dx;
			int y1 = y0 + dy;
			xmlElem.addAttribute("to", x1 + "," + y1);
			if (dx != 0)
				hwires[x0][y0] = -1;
			else
				vwires[x0][y0] = -1;
		}
		
		xmlElem.addAttribute("label-left", elem.getName());
		if (sStyle != null)
			xmlElem.addAttribute("style", sStyle);
		
		boolean bShow = true;
		switch (display.displayCurrents) {
		case All: bShow = true; break;
		case None: bShow = false; break;
		case Include: bShow = display.currents.contains("i" + elem.getName()); break;
		case Exclude: bShow = !display.currents.contains("i" + elem.getName()); break;
		}
		if (bShow) {
			xmlElem.addAttribute("showCurrentFlow", "1");
		}
	}
	
	private void markAutomaticWires(Elem elem, byte[][] hwires, byte[][] vwires) {
		Node node0 = elem.getNodes().get(0);
		int x0 = elem.getXYBase().x;
		int y0 = elem.getXYBase().y;
		markAutomaticWires(x0, y0, node0, hwires, vwires);

		Node node1 = elem.getNodes().get(1);
		int[] d = elem.getXYDestDiff();
		int x1 = x0 + d[0];
		int y1 = y0 + d[1];
		markAutomaticWires(x1, y1, node1, hwires, vwires);
	}
	
	private void markAutomaticWires(int x, int y, Node node, byte[][] hwires, byte[][] vwires) {
		int dx = node.getX() - x;
		int dy = node.getY() - y;
		
		// If one, but not both, equal zero
		if ((dx == 0) ^ (dy == 0)) {
			// If the base connector of the elem is in a horizontal line with its node: 
			if (dx != 0) {
				int iMin = Math.min(x, x + dx);
				int iMax = Math.max(x, x + dx);
				for (int i = iMin; i < iMax; i++)
					hwires[i - xMin][y - yMin] = 1;
			}
		}
	}
}
