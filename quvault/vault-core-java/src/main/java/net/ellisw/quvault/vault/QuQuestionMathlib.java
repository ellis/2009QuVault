package net.ellisw.quvault.vault;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

public class QuQuestionMathlib extends QuQuestion {
	public static enum AnswerMode {
		SingleLineRhsPerVar,
		EquationBlock,
		FunctionBlock
	}
	
	private final List<String> findVars = new ArrayList<String>();
	private final List<String> givenVars = new ArrayList<String>();
	private boolean bShowFind = true;
	private boolean bShowGiven = true;
	private AnswerMode mode = AnswerMode.SingleLineRhsPerVar;
	private final Map<String, String> solutions = new LinkedHashMap<String, String>();
	

	public QuQuestionMathlib(QuObject parent) {
		super(QuestionType.Matlib, parent);
		setEntryMode("mathlib");
	}
	
	public List<String> getFindVars() { return findVars; }
	public List<String> getGivenVars() { return givenVars; }
	
	public void addGivenVar(String name) {
		givenVars.add(name);
	}
	
	public void setGivens(List<String> asGivens) {
		givenVars.clear();
		givenVars.addAll(asGivens);
	}
	
	public void addFindVar(String name) {
		findVars.add(name);
	}
	
	public void setFinds(List<String> asFinds) {
		findVars.clear();
		findVars.addAll(asFinds);
	}
	
	public void setShowFind(boolean b) {
		bShowFind = b;
	}
	
	public void setShowGiven(boolean b) {
		bShowGiven = b;
	}
	
	public void setAnswerMode(AnswerMode mode) {
		this.mode = mode;
	}
	
	public void setSolution(String varName, String solution) {
		solutions.put(varName, solution);
	}
	
	@Override
	public String getXmlTagName() {
		return "questionMathlib";
	}

	@Override
	public void fillElement(Element elem) {
		super.fillElement(elem);
		
		if (findVars.size() > 0)
			elem.addElement("find").setText(CoreUtils.join(findVars, ","));
		if (givenVars.size() > 0)
			elem.addElement("given").setText(CoreUtils.join(givenVars, ","));
		if (!bShowFind)
			elem.addAttribute("showFind", "0");
		if (!bShowGiven)
			elem.addAttribute("showGiven", "0");
		
		String sAnswerMode = null;
		switch (mode) {
		case SingleLineRhsPerVar:
			sAnswerMode = "rhs";
			break;
		case EquationBlock:
			sAnswerMode = "equation";
			break;
		case FunctionBlock:
			sAnswerMode = "function";
			break;
		}
		elem.addAttribute("answerMode", sAnswerMode);
		
		for (String name : solutions.keySet()) {
			elem.addElement("solution").addAttribute("name", name).setText(solutions.get(name));
		}
	}
}
