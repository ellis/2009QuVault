package net.ellisw.quvault.client;

import java.io.Serializable;

public class ProbsetViewdata implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String title;
	private int problemCount;
	
	public String getId() { return id; }
	public void setId(String s) { id = s; }

	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }
	
	public int getProblemCount() { return problemCount; }
	public void setProblemCount(int n) { problemCount = n; }
}
