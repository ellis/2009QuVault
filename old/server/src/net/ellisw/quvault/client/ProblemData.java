package net.ellisw.quvault.client;

import java.util.ArrayList;

public class ProblemData {
	private String title;
	private String description;
	private final ArrayList<QuestionData> questions = new ArrayList<QuestionData>();
	
	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }
	
	public String getDescription() { return description; }
	public void setDescription(String s) { description = s; }
	
	public ArrayList<QuestionData> getQuestions() { return questions; }
	
	public void addQuestion(QuestionData question) {
		questions.add(question);
	}
}
