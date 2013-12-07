package net.ellisw.quvault.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.ellisw.quvault.client.QuestionType;


public class ProblemModel {
	private String url;
	private int[] args;
	private String title;
	private String description;
	private final List<QuestionModel> questions = new ArrayList<QuestionModel>();
	private final Map<String, String[]> resources = new HashMap<String, String[]>();


	public String getUrl() { return url; }
	public void setUrl(String s) { url = s; }

	public int[] getArgs() { return args; }
	public void setArg(int i, int n) { args[i] = n; }

	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }

	public String getDescription() { return description; }
	public void setDescription(String s) { description = s; }

	public List<QuestionModel> getQuestions() { return questions; }

	public void addQuestion(QuestionModel question) {
		questions.add(question);
	}

	public QuestionMatlibModel createMatlibQuestion() {
		QuestionMatlibModel question = new QuestionMatlibModel();
		question.setType(QuestionType.Matlib);
		questions.add(question);
		return question;
	}

	public Map<String, String[]> getResources() { return resources; }
	public String getResourceType(String key) { return resources.get(key)[0]; }
	public String getResourceValue(String key) { return resources.get(key)[1]; }
	public void setResource(String key, String type, String value) { resources.put(key.toLowerCase(), new String[] { type, value }); }

	public boolean load(String problemId) {
		boolean bOk = false;
		
		setUrl(problemId);

		//RhinoScriptEngine engine = new RhinoScriptEngine();
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");

		String sScriptPath = "problems/" + problemId;
		File file = new File(sScriptPath);
		if (file.exists() && engine != null) {
			String sScript = "";
			try {
				sScript = FileFunctions.readTextFile(sScriptPath);
				// evaluate script
				engine.eval(sScript);
				// javax.script.Invocable is an optional interface.
				// Check whether your script engine implements or not!
				// Note that the JavaScript engine implements Invocable interface.
				Invocable inv = (Invocable) engine;
				// invoke the global function named "setup"
				inv.invokeFunction("setup", this);
				bOk = true;
			} 
			catch (ScriptException e) {
				System.err.println("ScriptException");
				System.err.println(e.getLineNumber());
				System.err.println(e.getCause());
				System.err.println(e.toString());
				System.err.println(e.getMessage());
				System.err.println(sScript);
				e.printStackTrace();
				this.setTitle(sScriptPath);
				this.setDescription("<code>" + sScript + "</code>");
			}
			catch (Exception e) {
				System.err.println(e.getClass().getName());
				System.err.println(e.toString());
				System.err.println(e.getMessage());
				System.err.println(sScript);
				e.printStackTrace();
				setTitle(sScriptPath);
				setDescription("<code>" + sScript + "</code>");
			}
		}
		return bOk;
	}
}
