package net.ellisw.quvault.client;

import java.io.Serializable;

public class ProbsetIndex implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	
	
	public String getId() { return id; }
	public void setId(String s) { id = s; }

	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }
}
