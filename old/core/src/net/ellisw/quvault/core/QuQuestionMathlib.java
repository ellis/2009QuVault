package net.ellisw.quvault.core;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class QuQuestionMathlib extends QuQuestion {
	public static enum AnswerMode {
		SingleLineRhsPerVar,
		EquationBlock,
		FunctionBlock
	}
	
	private final List<String> findVars = new ArrayList<String>();
	private final List<String> givenVars = new ArrayList<String>();
	

	public QuQuestionMathlib(QuObject parent) {
		super(QuestionType.Matlib, parent);
	}
	
	public List<String> getFindVars() { return findVars; }
	public List<String> getGivenVars() { return givenVars; }
	
	public void addFindVar(String name) {
		findVars.add(name);
	}
	
	public void addGivenVar(String name) {
		givenVars.add(name);
	}
	
	public Element toXml() {
		Element elem = DocumentHelper.createElement("question");
		elem.addAttribute("type", "matlib");
		
		if (getTitle() != null)
			elem.addElement("title").setText(getTitle());
		if (getKeywords() != null)
			elem.addElement("keywords").setText(getKeywords());
		if (getDescription() != null)
			elem.addElement("description").setText(getDescription());
		/* FIXME:
		for (String name : findVars) {
			QuVar var = getVar(name);
			if (var != null) {
				Element elemVar = elem.addElement("find");
				elemVar.addAttribute("name", var.getName());
				if (var.getNameHtml() != null)
					elemVar.addAttribute("html", var.getNameHtml());
				if (var.getDescription() != null)
					elemVar.addAttribute("description", var.getDescription());
			}
		}
		for (QuVar var : givenVars) {
			Element elemVar = elem.addElement("given");
			elemVar.addAttribute("name", var.getName());
			if (var.getNameHtml() != null)
				elemVar.addAttribute("html", var.getNameHtml());
			if (var.getDescription() != null)
				elemVar.addAttribute("description", var.getDescription());
		}
		*/
		if (getSolution() != null)
			elem.addElement("solution").setText(getSolution());
		
		return elem;
	}
}
