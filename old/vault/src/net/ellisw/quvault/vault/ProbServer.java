package net.ellisw.quvault.vault;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import net.ellisw.quvault.core.CoreUtils;
import net.ellisw.quvault.core.IProblemTempl;
import net.ellisw.quvault.core.ProblemData;
import net.ellisw.quvault.core.ProblemDataAndParams;
import net.ellisw.quvault.core.ProblemHeader;
import net.ellisw.quvault.core.ProblemParamSpecs;
import net.ellisw.quvault.core.ProblemParams;
import net.ellisw.quvault.core.QuContainer;


public class ProbServer {

	/**
	 * Args:
	 * domain command [id] [variables-as-uri-string]
	 * 
	 * Commands:
	 * problemVars
	 * problemXml
	 * listProblems
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String cmd = null;
		URI url = null;
		if (args.length >= 1)
			cmd = args[0];
		if (args.length >= 2) {
			try {
				url = new URI(args[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (cmd == null) {
			ProbServer server2 = new ProbServer();
			try {
				//server2.run("listProblems", null, null);
				server2.run("problemXml", new URI("PT_ElementConnections(mode=RS)?count=3&values=100,sym,sym"));
				//server2.run("problemParamDefaults", new URI("PT_ElementConnections?mode=RS"), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		ProbServer server = new ProbServer();
		server.run(cmd, url);
	}
	
	public void run(String cmd, URI url) {
		if (cmd.equals("listProblems")) {
			System.out.println(toString(listProblemsXml()));
		}
		/*else if (cmd.equals("problemParamSpecs")) {
			ProblemParamSpecs specs = getProblemParamSpecs(cmd, url);
			// TODO: output specs as XML
		}*/
		else if (cmd.equals("problemParamDefaults")) {
			ProblemParams params = getProblemParamDefaults(url);
			printDocument(getProblemParamsXml(params));
		}
		/*else if (cmd.equals("problemXml")) {
			ProblemData problem = getProblemData(url);
			printElement(problem.toXml());
		}*/
	}
	
	public List<ProblemHeader> listProblems() {
		ArrayList<ProblemHeader> headers = new ArrayList<ProblemHeader>();
		PT_ElementConnections.setupHeaders(headers);
		PT_RCSeries.setupHeaders(headers);
		PT_ThreeResistorCircuit.setupHeaders(headers);
		return headers;
	}
	
	public Document listProblemsXml() {
		List<ProblemHeader> headers = listProblems();
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("listProblems");
		for (ProblemHeader header : headers) {
			root.addElement("problemHeader")
				.addAttribute("title", header.title)
				.addAttribute("author", header.author)
				.addAttribute("keywords", header.keywords)
				.addAttribute("id", header.uri);
		}
		
		return doc;
	}

	public ProblemParamSpecs getProblemParamSpecs(URI uri) {
		ProblemParamSpecs specs = new ProblemParamSpecs();

		IProblemTempl t = getProblemTempl(uri);
		t.setupParamSpecs(specs);
		
		return specs;
	}
	
	public ProblemParams getProblemParamDefaults(URI uri) {
		IProblemTempl t = getProblemTempl(uri);
		
		ProblemParams params = new ProblemParams();
		t.loadParamDefaults(params);
		return params;
	}

	public QuContainer getProblemData(URI uri) {
		IProblemTempl t = getProblemTempl(uri);
		Map<String, String> params = CoreUtils.parseUriQuery(uri.getQuery());

		QuContainer container = new QuContainer(null);
		container.addParams(params);
		t.setupProblemData(container);
		return container;
	}
	
	public ProblemDataAndParams getProblemDataAndParams(URI uri) {
		ProblemDataAndParams stuff = new ProblemDataAndParams();
		
		IProblemTempl t = getProblemTempl(uri);

		t.loadParamDefaults(stuff.params);
		Map<String, String> params = CoreUtils.parseUriQuery(uri.getQuery());
		stuff.params.updateFrom(params);
		
		stuff.problem.addParams(stuff.params.getMap());
		t.setupProblemData(stuff.problem);
		return stuff;
	}
	
	private Document getProblemParamsXml(ProblemParams params) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("problemParams");

		for (String sKey : params.keySet()) {
			root.addElement("item")
				.addAttribute("name", sKey)
				.addAttribute("value", params.get(sKey));
		}
		
		return doc;
	}
	
	private IProblemTempl getProblemTempl(URI uri) {
		String sClass = (uri != null) ? uri.getPath() : null;
		String sQuery = "";
		
		int iParenLeft = sClass.indexOf('(');
		int iParenRight= sClass.indexOf(')');
		if (iParenLeft >= 0 && iParenRight > iParenLeft) {
			sQuery = sClass.substring(iParenLeft + 1, iParenRight);
			sClass = sClass.substring(0, iParenLeft);
		}
		
		Map<String, String> params0 = CoreUtils.parseUriQuery(sQuery);
		IProblemTempl t = null;
		
		try {
			sClass = "net.ellisw.quvault.vault." + sClass;
			Class<?> clazz = this.getClass().getClassLoader().loadClass(sClass);
			t = (IProblemTempl) clazz.getConstructor().newInstance();
			t.setInitialParams(params0);
		} catch (Exception e) {
			e.printStackTrace();
			t = null;
		}
		
		return t;
	}
	
	private String toString(Document doc) {
		try {
			OutputFormat fmt = new OutputFormat();
			fmt.setNewlines(true);
			fmt.setIndent("\t");
			
			StringWriter sw = new StringWriter();
			XMLWriter w = new XMLWriter(sw, fmt);
			w.write(doc);
			return sw.toString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return "<error>" + "</error>";
		}
	}
	
	private void printDocument(Document doc) {
		try {
			OutputFormat fmt = new OutputFormat();
			fmt.setNewlines(true);
			fmt.setIndent("\t");
			XMLWriter w = new XMLWriter(System.out, fmt);
			w.write(doc);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void printElement(Element elem) {
		try {
			OutputFormat fmt = new OutputFormat();
			fmt.setNewlines(true);
			fmt.setIndent("\t");
			XMLWriter w = new XMLWriter(System.out, fmt);
			w.write(elem);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
