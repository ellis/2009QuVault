package net.ellisw.quvault.server.html;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProblemViewdata implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String text;
	private List<QuestionViewdata> questions = new ArrayList<QuestionViewdata>();

		
	public String getText() { return text; }
	public void setText(String s) { text = s; }
	
	public List<QuestionViewdata> getQuestions() { return questions; }
		
	public void addQuestion(QuestionViewdata question) {
		questions.add(question);
	}
}
