package net.ellisw.quvault.vault;

import org.dom4j.Element;


public class QuProblem extends QuContainerTitled {
	private String instructionKeywords;

	
	public QuProblem(QuObject parent) {
		super(parent);
		
		setEnumerated(true);
	}

	public String getInstructionKeywords() { return instructionKeywords; }
	public void setInstructionKeywords(String s) { instructionKeywords = s; }

	@Override
	public String getXmlTagName() {
		return "questionContainer";
	}

	@Override
	public void fillElement(Element elem) {
		super.fillElement(elem);
		
		if (instructionKeywords != null && !instructionKeywords.isEmpty())
			elem.addElement("instructions").addAttribute("keywords", instructionKeywords);
	}
}
