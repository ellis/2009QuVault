package net.ellisw.quvault.server.html;

public class QuestionMatlibViewdata extends QuestionViewdata {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String answerHeader; 
	private String answerFooter; 
	private String answerPrefix; 
	private String answerSuffix; 

	
	public String getAnswerHeader() { return answerHeader; }
	public void setAnswerHeader(String s) { answerHeader = s; }

	public String getAnswerFooter() { return answerFooter; }
	public void setAnswerFooter(String s) { answerFooter = s; }

	public String getAnswerPrefix() { return answerPrefix; }
	public void setAnswerPrefix(String s) { answerPrefix = s; }

	public String getAnswerSuffix() { return answerSuffix; }
	public void setAnswerSuffix(String s) { answerSuffix = s; }
}
