package net.ellisw.quvault.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ProblemData {
	private String title;
	private String keywords;
	private String description;
	private Map<String, String> resourceContents = new HashMap<String, String>();
	private Map<String, String> resourceTypes = new HashMap<String, String>();
	private List<QuestionData> questions = new ArrayList<QuestionData>();
	
	
	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }
	
	public String getKeywords() { return keywords; }
	public void setKeywords(String s) { keywords = s; }
	
	public String getDescription() { return description; }
	public void setDescription(String s) { description = s; }
	
	public boolean hasResource(String name) { return resourceContents.containsKey(name); }
	public String getResourceContents(String name) { return resourceContents.get(name); }
	public String getResourceType(String name) { return resourceTypes.get(name); }

	public List<QuestionData> getQuestions() { return questions; }

	public void setResource(String name, String type, String value) {
		resourceContents.put(name, value);
		resourceTypes.put(name, type);
	}
	
	public QuestionMatlibData createQuestionMatlib() {
		QuestionMatlibData q = new QuestionMatlibData();
		questions.add(q);
		return q;
	}
	
	public Element toXml() {
		Element elem = DocumentHelper.createElement("problem");
		elem.addAttribute("ver", "1");

		if (title != null)
			elem.addElement("title").setText(title);
		if (keywords != null)
			elem.addElement("keywords").setText(keywords);
		if (description != null)
			elem.addElement("description").setText(description);
		for (String sResKey : resourceContents.keySet()) {
			String sResType = resourceTypes.get(sResKey);
			String sResValue = resourceContents.get(sResKey);
			elem.addElement("resource")
				.addAttribute("type", sResType)
				.addAttribute("name", sResKey)
				.setText(sResValue);
		}
		for (QuestionData q : questions) {
			elem.add(q.toXml());
		}
		
		return elem;
	}
}
