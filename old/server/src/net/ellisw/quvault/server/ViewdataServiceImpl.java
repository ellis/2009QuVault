package net.ellisw.quvault.server;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;

import net.ellisw.circuitpainter.CircuitPainterSvg;
import net.ellisw.quvault.client.Level;
import net.ellisw.quvault.client.ProblemHeaderViewdata;
import net.ellisw.quvault.client.ProblemParams;
import net.ellisw.quvault.client.ProblemViewdata;
import net.ellisw.quvault.client.ProblemTemplViewdata;
import net.ellisw.quvault.client.ProblemTemplIndex;
import net.ellisw.quvault.client.ProbsetIndex;
import net.ellisw.quvault.client.ProbsetViewdata;
import net.ellisw.quvault.client.ViewdataService;
import net.ellisw.quvault.client.QuestionMatlibScope;
import net.ellisw.quvault.core.ProblemHeader;
import net.ellisw.quvault.server.database.PMF;
import net.ellisw.quvault.server.database.ProblemHeaderRow;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ViewdataServiceImpl extends RemoteServiceServlet implements ViewdataService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public List<ProbsetIndex> getProbsetIndexes() {
		List<ProbsetIndex> list = new ArrayList<ProbsetIndex>();

		File dir = new File("probsets/");
		String[] files = dir.list();
		for (String sFilename : files) {
			String probsetId = sFilename;
			ProbsetModel probsetM = new ProbsetModel();
			if (probsetM.load(probsetId)) {
				ProbsetIndex index = new ProbsetIndex();
				index.setId(probsetId);
				index.setTitle(probsetM.getTitle());
				list.add(index);
			}
		}

		return list;
	}

	@Override
	public ProbsetViewdata getProbsetViewdata(String probsetId) {
		ProbsetViewdata probsetVD = null;

		ProbsetModel probsetM = new ProbsetModel();
		probsetM.load(probsetId);
		probsetVD = createProbsetViewdata(probsetM);

		return probsetVD;
	}

	@Override
	public ProblemViewdata getProblemViewdata(String problemId, int[] args) {
		ProblemViewdata problemS = null;

		ProblemModel problemM = new ProblemModel();

		if (problemId != null)
			problemM.load(problemId);
		problemS = createProblemScope(problemM);

		return problemS;
	}

	@Override
	public ProblemViewdata getProblemViewdata(String probsetId, int iProblem) {
		ProblemViewdata problemS = null;

		ProbsetModel probsetM = new ProbsetModel();
		if (probsetM.load(probsetId)) {
			if (iProblem >= 0 && iProblem < probsetM.getProblemSpecs().size()) {
				ProbsetModel.ProblemSpec spec = probsetM.getProblemSpecs().get(iProblem);
				ProblemModel problemM = new ProblemModel();
				problemM.load(spec.problemId);
				problemS = createProblemScope(problemM);
			}
		}

		return problemS;
	}
	
	@Override
	public ProblemViewdata getProblemViewdataFromScript(String language, String script, int[] args) {
		ProblemTemplModel problemT = new ProblemTemplModel();
		problemT.setScript(script);

		ProblemModel problemM = problemT.createProblemModel(args);
		ProblemViewdata problemS = createProblemScope(problemM);

		return problemS;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProblemHeaderViewdata> getProblemHeaders() {
		List<ProblemHeaderViewdata> list = new ArrayList<ProblemHeaderViewdata>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		String query = "select from " + ProblemHeaderRow.class.getName() + " range 0,100";
		List<ProblemHeaderRow> rows = (List<ProblemHeaderRow>) pm.newQuery(query).execute();

		for (ProblemHeaderRow row : rows) {
			ProblemHeaderViewdata header = createProblemHeader(row);
			list.add(header);
		}
		
		return list;
	}

	@Override
	public ProblemTemplViewdata getProblemScript(long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		ProblemTemplModel scriptM = pm.getObjectById(ProblemTemplModel.class, id);
		ProblemTemplViewdata scriptVD = createProblemScriptViewdata(scriptM);
		return scriptVD;
	}

	@Override
	public ProblemTemplViewdata saveProblemScript(ProblemTemplViewdata script) {
		if (script == null)
			return null;

		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		ProblemTemplModel scriptM = null;
		if (script.getId() == null)
			scriptM = new ProblemTemplModel();
		else
			scriptM = pm.getObjectById(ProblemTemplModel.class, script.getId());
		
		// FIXME: How should these be set? 
		//scriptM.setRev(script.getRev());
		//scriptM.setAuthorId(script.getAuthorId());

		scriptM.setUrl(script.getUrl());
		scriptM.setAuthorName(script.getAuthorName());
		scriptM.setTopic(script.getTopic());
		scriptM.setKeywords(script.getKeywords());
		scriptM.setLanguage(script.getLanguage());
		scriptM.setScript(script.getScript());
		
		try {
			if (script.getId() == null)
				pm.makePersistent(scriptM);
		}
		finally {
			pm.close();
		}
		
		script = createProblemScriptViewdata(scriptM);
		return script;
	}
	
	public ProblemParams getProblemParams(long scriptId, int i) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		//ProblemTemplModel tmpl = pm.getObjectById(ProblemTemplModel.class, id);
		//tmpl.getProblemParams(args)
		ProblemParams params = new ProblemParams();
		
		return params;
	}

	private ProbsetViewdata createProbsetViewdata(ProbsetModel probsetM) {
		ProbsetViewdata probsetVD = new ProbsetViewdata();
		probsetVD.setId(probsetM.getId());
		probsetVD.setTitle(probsetM.getTitle());
		probsetVD.setProblemCount(probsetM.getProblemSpecs().size());
		return probsetVD;
	}

	private ProblemViewdata createProblemScope(ProblemModel problemM) {
		ProblemViewdata problemS = new ProblemViewdata();

		String sHeader = "";

		// Problem title
		String sTitle = problemM.getTitle();
		if (sTitle != null && !sTitle.isEmpty())
			sHeader = "<b>" + sTitle + "</b> ";

		// Problem points
		double nPoints = 0;
		for (QuestionModel question : problemM.getQuestions()) {
			nPoints += question.getPoints();
		}
		if (nPoints > 0) {
			String sPoints = "(Points: " + new DecimalFormat("#0.##").format(nPoints) + ")";
			sHeader += sPoints;
		}

		// Replace resource references
		String sDesc = problemM.getDescription();
		Pattern rexRes = Pattern.compile("<\\s*res\\s+name\\s*=\\s*['\"]([^'\"]+)['\"]\\s*/?>");
		while (sDesc != null) {
			Matcher match = rexRes.matcher(sDesc);
			if (!match.find())
				break;

			/*
			Document doc = null;
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setIgnoringComments(true);
				DocumentBuilder builder = dbf.newDocumentBuilder();
				doc = builder.parse(new InputSource(new StringReader(sXml)));
			}
			catch (Exception ex) {

			}
			 */

			String sKey = match.group(1).toLowerCase();
			String sValue = "";
			if (problemM.getResources().containsKey(sKey)) {
				String sType = problemM.getResourceType(sKey).toLowerCase();
				sValue = problemM.getResourceValue(sKey);
				if (sValue == null || sType == null) {
					sValue = " [RESOURCE ERROR] ";
				}
				else {
					if (sType.equals("circuit")) {
						sValue = new CircuitPainterSvg().convertToSvgObject("<circuit>" + sValue + "</circuit>");
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
			QuestionModel questionM = problemM.getQuestions().get(iQuestion);
			switch (questionM.getType()) {
			case Matlib:
				QuestionMatlibScope qMatlibS = createQuestionMatlibScope(problemM, iQuestion);
				problemS.addQuestion(qMatlibS);
			}
		}

		return problemS;
	}

	private QuestionMatlibScope createQuestionMatlibScope(ProblemModel problemM, int iQuestion) {
		QuestionMatlibScope qMatlibS = new QuestionMatlibScope();

		QuestionMatlibModel questionM = (QuestionMatlibModel) problemM.getQuestions().get(iQuestion);
		qMatlibS.setType(questionM.getType());
		qMatlibS.setAnswer(questionM.getAnswer());

		ArrayList<String> lines = new ArrayList<String>();

		String s = "";
		int nQuestions = problemM.getQuestions().size();
		if (nQuestions > 1) {
			s += "Points: " + NumberFormat.getFormat("#0.##").format(questionM.getPoints());
			if (questionM.getLevel() != null && questionM.getLevel() != Level.NONE) {
				if (!s.isEmpty())
					s += "; ";
				s += questionM.getLevel().toString();
			}
			if (!s.isEmpty()) {
				lines.add("(" + s + ")");
			}
		}

		if (questionM.getDescription() != null)
			lines.add(questionM.getDescription());

		int nFindVars = questionM.getFindVars().size();  
		if (nFindVars > 0) {
			s = "Find: ";
			for (int i = 0; i < questionM.getFindVars().size(); i++) {
				QuestionMatlibModel.Var var = questionM.getFindVars().get(i);
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
				QuestionMatlibModel.Var var = questionM.getGivenVars().get(i);
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
			QuestionMatlibModel.Var var = questionM.getFindVars().get(0);
			s = "<i style='color: blue'>" + var.getNameHtml() + "</i> =&nbsp;";
			qMatlibS.setAnswerPrefix(s);
		}
		else {
			// TODO: construct function header for answer prefix
			qMatlibS.setAnswerFooter("<code>end</code>");
		}

		return qMatlibS;
	}
	
	private ProblemHeaderViewdata createProblemHeader(ProblemHeaderRow row) {
		ProblemHeaderViewdata header = new ProblemHeaderViewdata();
		header.url = row.getSource() + "://" + row.getSourceUri();
		header.author = row.getAuthor();
		header.title = row.getTitle();
		if (header.title == null)
			header.title = "<NO TITLE>";
		header.keywords = row.getKeywords();
		if (header.keywords == null)
			header.keywords = "<NO KEYWORDS>";
		return header;
	}
	
	private ProblemTemplViewdata createProblemScriptViewdata(ProblemTemplModel scriptM) {
		ProblemTemplViewdata scriptVD = new ProblemTemplViewdata();
		scriptVD.setId(scriptM.getId());
		scriptVD.setUrl(scriptM.getUrl());
		scriptVD.setRev(scriptM.getRev());
		scriptVD.setAuthorId(scriptM.getAuthorId());
		scriptVD.setAuthorName(scriptM.getAuthorName());
		scriptVD.setTopic(scriptM.getTopic());
		scriptVD.setKeywords(scriptM.getKeywords());
		scriptVD.setLanguage(scriptM.getLanguage());
		scriptVD.setScript(scriptM.getScript());
		return scriptVD;
	}
}
