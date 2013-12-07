package net.ellisw.quvault.core;

public class QuContainerTitled extends QuContainer {
	private String title;
	private String keywords;

	
	public QuContainerTitled(QuObject parent) {
		super(parent);
		
		setEnumerated(true);
	}
	
	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }
	
	public String getKeywords() { return keywords; }
	public void setKeywords(String s) { keywords = s; }
}
