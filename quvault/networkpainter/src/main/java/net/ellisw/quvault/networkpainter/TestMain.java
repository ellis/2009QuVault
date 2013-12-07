package net.ellisw.quvault.networkpainter;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String sXml = "";
		
		if (args.length == 0) {
			int i = 1;
			if (i == 0) {
				sXml =
				"<network>" +
					"<circle label-left='a'/>" +
					"<resistor dy='+1' label-left='C1'/>" +
					"<capacitor dx='+1' label-left='C2'/>" +
					"<inductor dx='+1' dy='-1' label-left='C3'/>" +
					"<circle label-right='b'/>" +
				
					"<resistor from='-5,-5' dy='+1' label-left='R1'/>" +
					"<resistor dx='+1' label-left='R1'/>" +
					"<resistor dy='-1' label-left='R1'/>" +
					"<resistor dx='-1' label-left='R1'/>" +
					"<resistor dx='+1' dy='+1' label-left='R1'/>" +
					"<resistor dx='+1' dy='-1' label-left='R1'/>" +
				
					"<capacitor from='-2,-5' dy='+1' label-left='C1'/>" +
					"<capacitor dx='+1' label-left='C1'/>" +
					"<capacitor dy='-1' label-left='C1'/>" +
					"<capacitor dx='-1' label-left='C1'/>" +
					"<capacitor dx='+1' dy='+1' label-left='C1'/>" +
					"<capacitor dx='+1' dy='-1' label-left='C1'/>" +
				
					"<inductor from='1,-5' dy='+1' label-left='L1'/>" +
					"<inductor dx='+1' label-left='L1'/>" +
					"<inductor dy='-1' label-left='L1'/>" +
					"<inductor dx='-1' label-left='L1'/>" +
					"<inductor dx='+.5' dy='+.5' label-left='L1'/>" +
					"<inductor dx='+.5' dy='-.5' label-left='L1'/>" +
				"</network>";
			}
			else if (i == 1) {
				sXml =
				"<network>" +
				"<ground/>" +
				"<source dy='+1'/>" +
				"<source style='i' dy='+1'/>" +
				"</network>";
			}
		}
		else if (args[0].equals("-")) {
			try {
				java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
				while (true) {
					String line = stdin.readLine();
					if (line == null)
						break;
					sXml += line;
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		String s = new CircuitPainterSvg().convertToSvg(sXml, true);
		System.out.println(s);
	}
}
