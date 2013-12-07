package net.ellisw.quvault.client;

import java.util.ArrayList;

public class ProbsetData {
	private String title;
	private int problemCount;
	private final ArrayList<ProblemData> problems = new ArrayList<ProblemData>();
	
	public ProbsetData() {
		title = "Title";
	}
	
	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }
	
	public int getProblemCount() { return problemCount; }
	public void setProblemCount(int n) { problemCount = n; }
	
	public ArrayList<ProblemData> getProblems() { return problems; }
	
	public ProblemData getProblem(int i) {
		if (i >= 0 && i < problems.size())
			return problems.get(i);
		return null;
	}
	
	public void addProblem(ProblemData problem) {
		problems.add(problem);
	}
}
