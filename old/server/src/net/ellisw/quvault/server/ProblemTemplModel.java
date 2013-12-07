package net.ellisw.quvault.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.ellisw.quvault.client.ProblemParams;

import com.google.appengine.api.datastore.Text;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ProblemTemplModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String url;
	
	@Persistent
	private int rev;
	
	@Persistent
	private String authorId;
	
	@Persistent
	private String authorName;

	@Persistent
	private String topic;

	@Persistent
	private String keywords;

	@Persistent
	private String language;

	@Persistent
	private Text script;

	@NotPersistent
	private Invocable m_engine;
	

	public Long getId() { return id; }

	public String getUrl() { return url; }
	public void setUrl(String s) { url = s; }

	public int getRev() { return rev; }
	public void setRev(int n) { rev = n; }

	//public String getAuthorEmail() { return authorEmail; }
	//public void setAuthorEmail(String s) { authorEmail = s; }

	public String getAuthorId() { return authorId; }
	public void setAuthorId(String s) { authorId = s; }

	public String getAuthorName() { return authorName; }
	public void setAuthorName(String s) { authorName = s; }

	public String getKeywords() { return keywords; }
	public void setKeywords(String s) { keywords = s; }

	public String getLanguage() { return language; }
	public void setLanguage(String s) { language = s; m_engine = null; }

	public String getTopic() { return topic; }
	public void setTopic(String s) { topic = s; }

	public String getScript() { return script.getValue(); }
	public void setScript(String s) { script = new Text(s); m_engine = null; }

	public ProblemParams getProblemParams(int[] args) {
		ProblemParams params = new ProblemParams();

		try {
			if (m_engine == null)
				evaluateScript();
			// invoke the global function named "setup"
			m_engine.invokeFunction("setupParams", params, args);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return params;
	}
	
	public ProblemModel createProblemModel(int[] args) {
		ProblemModel problemM = new ProblemModel();
		
		try {
			if (m_engine == null)
				evaluateScript();
			// invoke the global function named "setup"
			m_engine.invokeFunction("setup", problemM, args);
		} 
		catch (ScriptException e) {
			System.err.println("ScriptException");
			System.err.println(e.getLineNumber());
			System.err.println(e.getCause());
			System.err.println(e.toString());
			System.err.println(e.getMessage());
			System.err.println(script.getValue());
			e.printStackTrace();
			problemM.setTitle("ERROR");
			problemM.setDescription("<code>" + script + "</code>");
		}
		catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.toString());
			System.err.println(e.getMessage());
			System.err.println(script.getValue());
			e.printStackTrace();
			problemM.setTitle("ERROR");
			problemM.setDescription("<code>" + script + "</code>");
		}

		return problemM;
	}
	
	private void evaluateScript() throws ScriptException {
		m_engine = null;
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");

		if (engine != null && script != null) {
			String sScript = script.getValue(); 
			// evaluate script
			engine.eval(sScript);
			// javax.script.Invocable is an optional interface.
			// Check whether your script engine implements or not!
			// Note that the JavaScript engine implements Invocable interface.
			m_engine = (Invocable) engine;
		}
	}
}
