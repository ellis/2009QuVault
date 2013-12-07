package net.ellisw.quvault.core;

import java.util.ArrayList;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import net.ellisw.quvault.core.QuestionData;


public class QuestionMatlibData extends QuestionData {
	public static enum AnswerMode {
		SingleLineRhsPerVar,
		EquationBlock,
		FunctionBlock
	}
	
	public static class Var {
		private String namePlain;
		private String nameHtml;
		private String description;
		
		public Var(String namePlain, String nameHtml, String description) {
			this.namePlain = namePlain;
			this.nameHtml = nameHtml;
			this.description = description;
		}
		
		public String getNamePlain() { return namePlain; }
		public void setNamePlain(String s) { namePlain = s; }
		
		public String getNameHtml() { return nameHtml; }
		public void setNameHtml(String s) { nameHtml = s; }
		
		public String getDescription() { return description; }
		public void setDescription(String s) { description = s; }
	}
	
	private final ArrayList<Var> findVars = new ArrayList<Var>();
	private final ArrayList<Var> givenVars = new ArrayList<Var>();
	

	public QuestionMatlibData() {
		super(QuestionType.Matlib);
	}
	
	public ArrayList<Var> getFindVars() { return findVars; }
	public ArrayList<Var> getGivenVars() { return givenVars; }
	
	public void addFindVar(String namePlain, String nameHtml, String description) {
		Var var = new Var(namePlain, nameHtml, description);
		findVars.add(var);
	}
	
	public void addGivenVar(String namePlain, String nameHtml, String description) {
		Var var = new Var(namePlain, nameHtml, description);
		givenVars.add(var);
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
		for (Var var : findVars) {
			Element elemVar = elem.addElement("find");
			elemVar.addAttribute("name", var.namePlain);
			if (var.nameHtml != null)
				elemVar.addAttribute("html", var.nameHtml);
			if (var.description != null)
				elemVar.addAttribute("description", var.description);
		}
		for (Var var : givenVars) {
			Element elemVar = elem.addElement("given");
			elemVar.addAttribute("name", var.namePlain);
			if (var.nameHtml != null)
				elemVar.addAttribute("html", var.nameHtml);
			if (var.description != null)
				elemVar.addAttribute("description", var.description);
		}
		if (getSolution() != null)
			elem.addElement("solution").setText(getSolution());
		
		return elem;
	}
}
