package net.ellisw.quvault.client;

import java.io.Serializable;

public class QuestionScope implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private QuestionType type;
	private String text;
	private String answer; 

	public QuestionType getType() { return type; }
	public void setType(QuestionType type) { this.type = type; }

	public String getText() { return text; }
	public void setText(String s) { text = s; }

	public String getAnswer() { return answer; }
	public void setAnswer(String s) { answer = s; }
}
