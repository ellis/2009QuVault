function getPrimaryVariables(vars) {
	// This is an integer range, from 2 to 5
	vars.addRangeInteger("elementCount", "Element Count", 2, 3);

	// This is an enum with the given values
	vars.addOption("valueType", "Values")
		.addValue("S", "Symbolic")
		.addValue("N", "Numeric");

	vars.addProblemType("Series Resistance", "orientation=S; elementType=R");
	vars.addProblemType("Series Capacitance", "orientation=S; elementType=C");
	vars.addProblemType("Series Inductance", "orientation=S; elementType=L");
	vars.addProblemType("Series Voltage", "orientation=S; elementType=V");
	vars.addProblemType("Parallel Resistance", "orientation=P; elementType=R");
	vars.addProblemType("Parallel Capacitance", "orientation=P; elementType=C");
	vars.addProblemType("Parallel Inductance", "orientation=P; elementType=L");
	vars.addProblemType("Parallel Current", "orientation=P; elementType=I");
}

function getSecondaryVariables(vars, params) {
	if (params["valueType"] == "N") {
		// Its length should be equal to "elementCount"
		// The input should be checked for validity
		// It can be random or user-specified
		// The random values should be generated from a system-wide function for resistor values
		vars.createListFloat("elementValues", "Element Values")
			.setItemCount(params["elementCount"])
			.setCanUserEdit(true)
			.setRandomRangeFactor(50);
	}
}

function setup(problem, params) {
	problem.setTitle('Series Resistance');
	problem.setDescription("<res name='circuit'/>");
	
	var sCircuitXml =
		"<circle label-left='a'/>" +
		"<resistor dx='+1' label-left='R1'/>" +
		"<resistor dx='+1' label-left='R2'/>" +
		"<resistor dx='+1' label-left='R3'/>" +
		"<circle label-right='b'/>";
	problem.setResource('circuit', 'circuit', sCircuitXml);
	
	q = problem.createMatlibQuestion();
	q.addFindVar("RT", "R<sub>T</sub>", "total resistance");
	q.addGivenVar("R1", "R1", null);
	q.addGivenVar("R2", "R2", null);
	q.addGivenVar("R3", "R3", null);
	q.setSolution("RT = R1 + R2 + R3");
}
