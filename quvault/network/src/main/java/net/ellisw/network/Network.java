package net.ellisw.network;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;


public class Network {
	private NetworkData data;
	private NetworkVars vars;
	private boolean bDataChanged;

	
	public Network() {
		data = new NetworkData();
		bDataChanged = true;
	}
	
	public List<Node> getNodes() { return data.getNodes(); }
	public List<Elem> getElems() { return data.getElems(); }
	public Map<String, Var> getVars() { return (vars != null) ? vars.getVars() : null; }
	
	public String[] getElemNamesSorted() {
		return data.getElemNamesSorted();
	}
	
	public String[] getVarNamesSorted() {
		return vars.getVarNamesSorted();
	}
	
	public void addNode(String sName, int x, int y) {
		data.addNode(sName, x, y);
		bDataChanged = true;
	}
	
	public void addElem(String sName, String sNodeName1, String sNodeName2, int x0, int y0, Direction dir) {
		data.addElem(sName, sNodeName1, sNodeName2, x0, y0, dir);
		bDataChanged = true;
	}
	
	public Map<String, Var> createVars(Map<String, String> translations) {
		createNetworkVars(translations);
		return vars.getVars();
	}
	
	public Map<String, Var> createVars() {
		createNetworkVars(null);
		return vars.getVars();
	}
	
	public Element getDisplayXml(DisplayParams display) {
		return NetworkXml.getDisplayXml(data, display);
	}
	
	public Element getDisplayXml() {
		return NetworkXml.getDisplayXml(data);
	}

	private void createNetworkVars(Map<String, String> translations) {
		if (bDataChanged) {
			vars = new NetworkVars(data, translations);
			bDataChanged = false;
		}
	}

	public Solver createSolver() {
		createNetworkVars(null);
		Solver solver = new Solver();
		for (Var var : vars.getVars().values()) {
			Solver.Item item = new Solver.Item();
			item.var = var;
			solver.mapNameToItem.put(var.getName(), item);
		}
		return solver;
	}
	
	public Solver getFindables(List<String> asGivens) {
		Solver solver = createSolver();
		solver.addGivenNames(asGivens);
		solver.find(null);
		return solver;
	}

	public Solver getFindables(String... asGivens) {
		return getFindables(Arrays.asList(asGivens));
	}
	
	public Map<Solver.Item, Solver> getDiffEqs() {
		String[] asGivens = data.getElemNamesSorted();
		Solver solver = getFindables(asGivens);
		Map<Solver.Item, Solver> mapSolution = solver.findDiffEqs();
		return mapSolution;
	}
}
