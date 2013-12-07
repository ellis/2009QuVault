package net.ellisw.quvault.vault;

import java.util.Map;

public interface IProblemTempl {
	void setParams(Map<String, String> params);
	boolean setInitialParams(Map<String, String> params);
	boolean setupParamSpecs(int iLevel, ProblemParamSpecs specs);
	boolean loadParamDefaults(int iLevel, Map<String, String> params);
	void loadRandomParams(Map<String, String> params);
	void setupProblemData(QuContainer problem);
}
