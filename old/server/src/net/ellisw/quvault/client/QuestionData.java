package net.ellisw.quvault.client;


public class QuestionData {
	private double points;
	private Level level;
	private QuestionType type;
	private String description;
	private String answerHeader; 
	private String answerFooter; 
	private String answerPrefix; 
	private String answerSuffix; 
	private String answer; 

	public double getPoints() { return points; }
	public void setPoints(double n) { points = n; }

	public Level getLevel() { return level; }
	public void setLevel(Level level) { this.level = level; }

	public QuestionType getType() { return type; }
	public void setType(QuestionType type) { this.type = type; }

	public String getDescription() { return description; }
	public void setDescription(String s) { description = s; }

	public String getAnswerHeader() { return answerHeader; }
	public void setAnswerHeader(String s) { answerHeader = s; }

	public String getAnswerFooter() { return answerFooter; }
	public void setAnswerFooter(String s) { answerFooter = s; }

	public String getAnswerPrefix() { return answerPrefix; }
	public void setAnswerPrefix(String s) { answerPrefix = s; }

	public String getAnswerSuffix() { return answerSuffix; }
	public void setAnswerSuffix(String s) { answerSuffix = s; }

	public String getAnswer() { return answer; }
	public void setAnswer(String s) { answer = s; }
}
