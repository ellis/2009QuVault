package net.ellisw.quvault.core;

import org.dom4j.Element;

import net.ellisw.quvault.core.Level;
import net.ellisw.quvault.core.QuestionType;

public abstract class QuestionData {
	private double points;
	private Level level;
	private QuestionType type;
	private String title; 
	private String keywords;
	private String description;
	private String solution; 
	private String answer;
	private Double answerScore;
	
	
	protected QuestionData(QuestionType type) {
		this.type = type;
	}
	
	public double getPoints() { return points; }
	public void setPoints(double n) { points = n; }

	public Level getLevel() { return level; }
	public void setLevel(Level level) { this.level = level; }

	public QuestionType getType() { return type; }
	//public void setType(QuestionType type) { this.type = type; }

	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }

	public String getKeywords() { return keywords; }
	public void setKeywords(String s) { keywords = s; }

	public String getDescription() { return description; }
	public void setDescription(String s) { description = s; }

	public String getSolution() { return solution; }
	public void setSolution(String s) { solution = s; }

	/** The user's answer to this question */
	public String getAnswer() { return answer; }
	public void setAnswer(String s) { answer = s; }
	
	public Double getAnswerScore() { return answerScore; }
	public void setAnswerScore(Double n) { answerScore = n; }
	
	public abstract Element toXml();
}
