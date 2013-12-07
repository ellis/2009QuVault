package net.ellisw.quvault.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ProbsetModel {
	public class ProblemSpec {
		public String problemId;
		int[] params;
		int[] questionFilters;
	}
	
	private String id;
	private String title;
	private List<ProblemSpec> specs = new ArrayList<ProblemSpec>();
	
	public String getId() { return id; }
	public void setId(String s) { id = s; }
	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }
	public List<ProblemSpec> getProblemSpecs() { return specs; }
	
	public void addProblem(String problemId, int[] params, int[] questionFilters) {
		ProblemSpec spec = new ProblemSpec();
		spec.problemId = problemId;
		spec.params = params;
		spec.questionFilters = questionFilters;
		specs.add(spec);
	}
	
	public boolean load(String problemId) {
		boolean bOk = false;
		
		setId(problemId);

		//RhinoScriptEngine engine = new RhinoScriptEngine();
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");

		String sScriptPath = "probsets/" + problemId;
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
			}
			catch (Exception e) {
				System.err.println(e.getClass().getName());
				System.err.println(e.toString());
				System.err.println(e.getMessage());
				System.err.println(sScript);
				e.printStackTrace();
				setTitle(sScriptPath);
			}
		}
		return bOk;
	}
}
