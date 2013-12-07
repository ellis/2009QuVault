package net.ellisw.quvault.core;

import java.util.Map;

public interface IProblemTempl {
	boolean setInitialParams(Map<String, String> params);
	public void setupParamSpecs(ProblemParamSpecs specs);
	public void loadParamDefaults(ProblemParams params);
	public void loadRandomParams(Map<String, String> params);
	void setupProblemData(QuContainer problem);
}
