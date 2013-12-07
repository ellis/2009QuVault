package net.ellisw.quvault.vault;

import org.dom4j.Element;


public abstract class QuQuestion extends QuContainerTitled {
	private double points;
	private Level level;
	private QuestionType type;
	private String description;
	private String entryMode;
	//private String solution; 
	private String answer;
	private Double answerScore;
	
	
	public QuQuestion(QuestionType type, QuObject parent) {
		super(parent);
		
		setEnumerated(true);

		this.type = type;
	}

	public double getPoints() { return points; }
	public void setPoints(double n) { points = n; }

	public Level getLevel() { return level; }
	public void setLevel(Level level) { this.level = level; }

	public QuestionType getType() { return type; }

	public String getDescription() { return description; }
	public void setDescription(String s) { description = s; }
	
	public String getEntryMode() { return entryMode; }
	public void setEntryMode(String s) { entryMode = s; }

	//public String getSolution() { return solution; }
	//public void setSolution(String s) { solution = s; }

	/** The user's answer to this question */
	public String getAnswer() { return answer; }
	public void setAnswer(String s) { answer = s; }
	
	public Double getAnswerScore() { return answerScore; }
	public void setAnswerScore(Double n) { answerScore = n; }


	@Override
	public void fillElement(Element elem) {
		super.fillElement(elem);
		
		if (entryMode != null && !entryMode.isEmpty())
			elem.addElement("entryMode").addAttribute("mode", entryMode);
	}
}
