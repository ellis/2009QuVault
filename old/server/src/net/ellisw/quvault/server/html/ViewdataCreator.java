package net.ellisw.quvault.server.html;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ellisw.circuitpainter.CircuitPainterSvg;
import net.ellisw.quvault.core.Level;
import net.ellisw.quvault.core.ProblemData;
import net.ellisw.quvault.core.QuestionData;
import net.ellisw.quvault.core.QuestionMatlibData;


public class ViewdataCreator {
	public ProblemViewdata createProblemScope(ProblemData problemM) {
		ProblemViewdata problemS = new ProblemViewdata();

		String sHeader = "";

		// Problem title
		String sTitle = problemM.getTitle();
		if (sTitle != null && !sTitle.isEmpty())
			sHeader = "<b>" + sTitle + "</b> ";

		// Problem points
		double nPoints = 0;
		for (QuestionData question : problemM.getQuestions()) {
			nPoints += question.getPoints();
		}
		if (nPoints > 0) {
			String sPoints = "(Points: " + new DecimalFormat("#0.##").format(nPoints) + ")";
			sHeader += sPoints;
		}

		// Replace resource references
		String sDesc = problemM.getDescription();
		Pattern rexRes = Pattern.compile("<\\s*resource\\s+name\\s*=\\s*['\"]([^'\"]+)['\"]\\s*/?>");
		while (sDesc != null) {
			Matcher match = rexRes.matcher(sDesc);
			if (!match.find())
				break;

			String sKey = match.group(1).toLowerCase();
			String sValue = "";
			if (problemM.hasResource(sKey)) {
				String sType = problemM.getResourceType(sKey).toLowerCase();
				sValue = problemM.getResourceContents(sKey);
				if (sValue == null || sType == null) {
					sValue = " [RESOURCE ERROR] ";
				}
				else {
					if (sType.equals("network")) {
						sValue = new CircuitPainterSvg().convertToSvgObject(sValue);
					}
				}
			}
			else {
				sValue = " [RESOURCE KEY MISSING] ";
			}

			sDesc =
				sDesc.substring(0, match.start()) +
				sValue +
				sDesc.substring(match.end());
		}

		String sHtml = "";
		if (!sHeader.isEmpty())
			sHtml += "<div style='padding-bottom: .7em'>" + sHeader + "</div>";
		if (sDesc != null && !sDesc.isEmpty())
			sHtml += "<div style='padding-bottom: .7em'>" + sDesc + "</div>";

		problemS.setText(sHtml);

		for (int iQuestion = 0; iQuestion < problemM.getQuestions().size(); iQuestion++) {
			QuestionData questionM = problemM.getQuestions().get(iQuestion);
			switch (questionM.getType()) {
			case Matlib:
				QuestionMatlibViewdata qMatlibS = createQuestionMatlibScope(problemM, iQuestion);
				problemS.addQuestion(qMatlibS);
			}
		}

		return problemS;
	}

	public QuestionMatlibViewdata createQuestionMatlibScope(ProblemData problem, int iQuestion) {
		QuestionMatlibViewdata qMatlibS = new QuestionMatlibViewdata();

		QuestionMatlibData questionM = (QuestionMatlibData) problem.getQuestions().get(iQuestion);
		qMatlibS.setType(questionM.getType());
		qMatlibS.setAnswer(questionM.getAnswer());
		qMatlibS.setAnswerScore(questionM.getAnswerScore());

		ArrayList<String> lines = new ArrayList<String>();

		String s = "";
		String sTitle = questionM.getTitle();
		if (sTitle != null && !sTitle.isEmpty())
			s += "<b>" + sTitle + "</b>&nbsp;";
		int nQuestions = problem.getQuestions().size();
		if (nQuestions > 1) {
			String sInParen = "";
			if (questionM.getPoints() > 0)
				sInParen += String.format("Points: %.2f", questionM.getPoints());
			if (questionM.getLevel() != null && questionM.getLevel() != Level.NONE) {
				if (!sInParen.isEmpty())
					sInParen += "; ";
				sInParen += questionM.getLevel().toString();
			}
			if (!sInParen.isEmpty())
				s += " (" + sInParen + ")";
		}
		if (!s.isEmpty()) {
			lines.add(s);
		}

		if (questionM.getDescription() != null)
			lines.add(questionM.getDescription());

		int nFindVars = questionM.getFindVars().size();  
		if (nFindVars > 0) {
			s = "Find: ";
			for (int i = 0; i < questionM.getFindVars().size(); i++) {
				QuestionMatlibData.Var var = questionM.getFindVars().get(i);
				if (i > 0)
					s += ", ";
				String sDesc = var.getDescription();
				if (sDesc != null && !sDesc.isEmpty()) {
					s += sDesc + " (<i style='color: blue'>" + var.getNameHtml() + "</i>)";
				}
				else {
					s += "<i style='color: blue'>" + var.getNameHtml() + "</i>";
				}
			}
			lines.add(s);
		}

		if (questionM.getGivenVars().size() > 0) {
			s = "Given: ";
			for (int i = 0; i < questionM.getGivenVars().size(); i++) {
				QuestionMatlibData.Var var = questionM.getGivenVars().get(i);
				if (i > 0)
					s += ", ";
				s += "<i style='color: green'>" + var.getNameHtml() + "</i>";
			}
			lines.add(s);
		}

		/*
		// TODO: Add in function header for appropriate questions
		// Function header
		s = questionM.getAnswerHeader();
		if (s != null && !s.isEmpty())
			lines.add("<code>" + s + "</code>");
		 */

		String sHtml = "";
		for (int i = 0; i < lines.size(); i++) {
			if (i > 0)
				sHtml += "<br>";
			sHtml += lines.get(i);
		}
		qMatlibS.setText(sHtml);

		if (nFindVars == 1) {
			QuestionMatlibData.Var var = questionM.getFindVars().get(0);
			s = "<i style='color: blue'>" + var.getNameHtml() + "</i> =&nbsp;";
			qMatlibS.setAnswerPrefix(s);
		}
		else {
			// TODO: construct function header for answer prefix
			qMatlibS.setAnswerFooter("<code>end</code>");
		}

		return qMatlibS;
	}
}
