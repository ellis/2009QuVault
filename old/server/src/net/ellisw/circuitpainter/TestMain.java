package net.ellisw.circuitpainter;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String sXml = 
			"<circuit>" +
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
			"</circuit>";

		String s = new CircuitPainterSvg().convertToSvg(sXml, true);
		System.out.println(s);
	}
}
