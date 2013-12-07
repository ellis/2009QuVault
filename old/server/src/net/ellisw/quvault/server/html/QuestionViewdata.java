package net.ellisw.quvault.server.html;

import java.io.Serializable;

import net.ellisw.quvault.core.QuestionType;

public class QuestionViewdata implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private QuestionType type;
	private String text;
	private String answer;
	private Double answerScore;

	public QuestionType getType() { return type; }
	public void setType(QuestionType type) { this.type = type; }

	public String getText() { return text; }
	public void setText(String s) { text = s; }

	public String getAnswer() { return answer; }
	public void setAnswer(String s) { answer = s; }
	
	public Double getAnswerScore() { return answerScore; }
	public void setAnswerScore(Double n) { answerScore = n; }
}
