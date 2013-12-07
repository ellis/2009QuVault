package net.ellisw.quvault.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProblemViewdata implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String text;
	private List<QuestionScope> questions = new ArrayList<QuestionScope>();

		
	public String getText() { return text; }
	public void setText(String s) { text = s; }
	
	public List<QuestionScope> getQuestions() { return questions; }
		
	public void addQuestion(QuestionScope question) {
		questions.add(question);
	}
}
