package net.ellisw.quvault.server.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import jmathlib.core.interpreter.ErrorLogger;
import jmathlib.core.interpreter.Interpreter;

import net.ellisw.quvault.core.CoreUtils;
import net.ellisw.quvault.core.ProblemData;
import net.ellisw.quvault.core.ProblemDataAndParams;
import net.ellisw.quvault.core.ProblemParamSpecs;
import net.ellisw.quvault.core.QuContainer;
import net.ellisw.quvault.core.QuContainerTitled;
import net.ellisw.quvault.core.QuObject;
import net.ellisw.quvault.core.QuProblem;
import net.ellisw.quvault.core.QuText;
import net.ellisw.quvault.core.QuestionData;
import net.ellisw.quvault.core.QuestionMatlibData;
import net.ellisw.quvault.core.QuestionType;
import net.ellisw.quvault.core.QuestionMatlibData.Var;
import net.ellisw.quvault.vault.ProbServer;


public class ProblemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html");
		PrintWriter w = resp.getWriter();

		w.println("<html><body>");

		String sPath = req.getPathInfo();
		String[] asParts = sPath.split("/");
		String sSource = asParts[1];
		String sSourceUri = sPath.substring(sSource.length() + 2);
		Map<String, String[]> vars = null;
		if (req.getQueryString() != null) {
			vars = new HashMap<String, String[]>();
			vars.putAll(req.getParameterMap());
			sSourceUri += "?" + req.getQueryString();
		}

		URI uri = null;
		try {
			uri = new URI(sSourceUri);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		ProblemDataAndParams stuff = getStuff(sSource, uri);
		if (vars != null) {
			loadAnswers(req, stuff);
			if (vars.containsKey("check"))
				checkAnswers(stuff);
		}
		printProblem(w, sSource, sPath, stuff);
		printParams(w, sSource, sPath, uri, stuff);

		w.println("</body></html>");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}

	private ProblemDataAndParams getStuff(String sSource, URI uri) {
		ProblemDataAndParams stuff = null;

		if (sSource.equals("local")) {
			ProbServer srv = new ProbServer();
			stuff = srv.getProblemDataAndParams(uri);
		}

		return stuff;
	}

	private ProblemDataAndParams printProblem(PrintWriter w, String sSource, String sPath, ProblemDataAndParams stuff) {
		w.println("<form action='/html/problem" + sPath + "' method='get'>");
		w.println("<table cellpadding='0' cellspacing='0'>");
		if (sSource.equals("local")) {
			printObject(stuff.problem);
			QuContainer problem = stuff.problem;
			ProblemViewdata problemV = (new ViewdataCreator()).createProblemScope(problem);

			w.println("<tr><td valign='top'></td><td valign='top'><b>1.</b>&nbsp;&nbsp;</td>");
			w.println("<td colspan='2'>" + problemV.getText() + "</td>");
			w.println("</tr>");

			int nQuestions = problemV.getQuestions().size();
			for (int iQuestion = 0; iQuestion < nQuestions; iQuestion++) {
				QuestionViewdata questionV = problemV.getQuestions().get(iQuestion);
				if (questionV.getType() == QuestionType.Matlib) {
					QuestionMatlibViewdata qMatlib = (QuestionMatlibViewdata) questionV;

					w.print("<tr><td>");
					if (qMatlib.getAnswerScore() != null) {
						if (qMatlib.getAnswerScore() > 0)
							w.print("+");
						else
							w.print("-");
					}
					w.print("</td><td></td>");
					if (nQuestions > 1)
						w.print("<td valign='top'>" + (char)('a' + iQuestion) + ")&nbsp;&nbsp;</td>");
					w.println();

					w.println("<td>");
					w.println(qMatlib.getText());

					// Function header
					String s = qMatlib.getAnswerHeader();
					if (s != null && !s.isEmpty()) {
						//layout.add(new HTML(s));
						// TODO: add edit box
						//layout.add(new HTML(question.getAnswerFooter()));
					}
					else {
						w.print("<p>");
						w.print(qMatlib.getAnswerPrefix());
						w.print("<input name='ans_1_" + (iQuestion + 1) + "' type='text' size='40' maxlength='80'");
						String sAnswer = qMatlib.getAnswer();
						if (sAnswer != null) {
							w.print(" value=\"" + StringEscapeUtils.escapeHtml(qMatlib.getAnswer()) + "\"/>");
						}
						w.println("</p>");
					}

					w.println("</td>");
					w.println("</tr>");
				}
			}
		}
		w.println("</table>");
		w.println("<br/><input type='submit' value='Check Answers'/>");
		w.println("</form>");

		return stuff;
	}
	
	private void printObject(QuObject o) {
		if (o instanceof QuText) {
			
		}
		else if (o instanceof QuContainerTitled) {
			if (o instanceof QuQuestio)
					
		}
		else if (o instanceof QuContainer) {
			
		}
	}

	private void printParams(PrintWriter w, String sSource, String sPath, URI uri, ProblemDataAndParams stuff) {
		if (sSource.equals("local")) {
			ProbServer srv = new ProbServer();
			ProblemParamSpecs specs = srv.getProblemParamSpecs(uri);
			if (specs.getVars().size() > 0) {
				w.println("<br/>");
				w.println("<form action='/html/problem" + sPath + "' method='get'>");
				w.println("<table>");
				for (ProblemParamSpecs.Var var : specs.getVars()) {
					String sName = var.getName();
					String sValue = StringEscapeUtils.escapeHtml(stuff.params.get(sName));
					w.print("<tr><td>");
					w.print(var.getDesc() + ":</td><td>");
					w.print("<input name='" + sName + "' type='text' size='20' maxlength='80'");
					if (sValue != null)
						w.print(" value=\"" + sValue + "\"");
					w.println("/></td></tr>");
				}
				w.println("<tr><td colspan='2'><input type='submit' value='Change Parameters'/></tr>");
				w.println("</table>");
				w.println("</form>");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadAnswers(HttpServletRequest req, ProblemDataAndParams stuff) {
		for (Enumeration e = req.getParameterNames(); e.hasMoreElements() ;) {
			String sKey = (String) e.nextElement();
			if (sKey.startsWith("ans_")) {
				try {
					String sAnswer = req.getParameter(sKey);
					//String[] asAnswers = entry.getValue();
					//if (asAnswers.length == 1) {
						//String sAnswer = asAnswers[0];
						String[] asParts = sKey.split("_");
						String siQuestion = asParts[2];
						int iQuestion = Integer.parseInt(siQuestion) - 1;
						QuestionData question = stuff.problem.getQuestions().get(iQuestion);
						question.setAnswer(sAnswer);
					//}
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void checkAnswers(ProblemDataAndParams stuff) {
		for (QuestionData question : stuff.problem.getQuestions()) {
			QuestionMatlibData qMatlib = (QuestionMatlibData) question;
			
			Interpreter jmlAnswer = new Interpreter(false);
			Interpreter jmlSolution = new Interpreter(false);
			//jml.setOutputPanel(this);
			ErrorLogger.setDebug(true);
	
			ArrayList<Var> givens = qMatlib.getGivenVars();
			ArrayList<Var> finds = qMatlib.getFindVars();
			for (int iPower = 0; iPower < 2; iPower++) {
				String sInput = "";
				for (int iVar = 0; iVar < givens.size(); iVar++) {
					Var var = givens.get(iVar);
					double nValue = Math.pow((iVar + 1), (iPower + 1));
					sInput += var.getNamePlain() + " = " + nValue + ";\n"; 
				}
				
				String sAnswerCode = sInput + question.getAnswer();
				String sSolutionCode = sInput + qMatlib.getSolution();
				
				jmlAnswer.executeExpression(sAnswerCode);
				jmlSolution.executeExpression(sSolutionCode);
				
				boolean bOk = true;
				for (int iVar = 0; iVar < finds.size(); iVar++) {
					Var var = finds.get(iVar);
					if (!compareValues(jmlSolution, jmlAnswer, var.getNamePlain())) {
						bOk = false;
						break;
					}
				}
				qMatlib.setAnswerScore(bOk ? 1.0 : 0.0);
			}
		}
	}
	
	private boolean compareValues(Interpreter jmlSolution, Interpreter jmlAnswer, String name) {
		double[][] solutionRe = jmlSolution.getArrayValueRe(name);
		double[][] solutionIm = jmlSolution.getArrayValueRe(name);
		double[][] answerRe = jmlAnswer.getArrayValueRe(name);
		double[][] answerIm = jmlAnswer.getArrayValueRe(name);
		
		if (!compareValues(solutionRe, answerRe, 0.01))
			return false;
		if (!compareValues(solutionIm, answerIm, 0.01))
			return false;
		return true;
	}
	
	private boolean compareValues(double[][] a, double[][] b, double precision) {
		if (a.length != b.length)
			return false;
		
		for (int i = 0; i < a.length; i++) {
			if (a[i].length != b[i].length)
				return false;
			
			for (int j = 0; j < a[i].length; j++) {
				double nA = a[i][j];
				double nB = b[i][j];
				double nDiff = nA - nB;
				double nLeeway = nA * precision;
				if (nDiff > nLeeway)
					return false;
			}
		}
		return true;
	}
}
