package net.ellisw.quvault.vault;

import org.dom4j.Element;

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

	@Override
	public void fillElement(Element elem) {
		super.fillElement(elem);

		if (title != null && !title.isEmpty())
			elem.addAttribute("title", title);
		if (keywords != null && !keywords.isEmpty())
			elem.addElement("keywords").setText(keywords);
	}
}
