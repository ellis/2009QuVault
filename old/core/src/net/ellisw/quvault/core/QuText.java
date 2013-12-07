package net.ellisw.quvault.core;

public class QuText extends QuObject {
	private String html;
	

	public QuText(QuObject parent) {
		super(parent);
	}
	
	public String getHtml() { return html; }
	public void setHtml(String s) { html = s; }
}
