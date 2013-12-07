package net.ellisw.quvault.vault;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class QuText extends QuObject {
	private String html;
	

	public QuText(QuObject parent) {
		super(parent);
	}
	
	public String getHtml() { return html; }
	public void setHtml(String s) { html = s; }

	@Override
	public String getXmlTagName() {
		return "text";
	}

	@Override
	public void fillElement(Element elem) {
		super.fillElement(elem);
		
		Document docContent;
		try {
			docContent = DocumentHelper.parseText("<dummy>" + html + "</dummy>");
			elem.setContent(docContent.getRootElement().content());
		} catch (DocumentException e) {
			e.printStackTrace();
			elem.setText("ERROR");
		}
	}
}
