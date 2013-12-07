package net.ellisw.quvault.vault;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


public class ProbServer {
	private URI uri;
	private Element root;
	
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
		if (args.length == 0)
			args = new String[] { "questions/PT_ThreeResistorCircuit/xml-and-params?finds=pR1,iV,vab,vbc,vac,iR1,iR2,iR3,pR3,pR2&givens=R1,V,R2,R3" };
		String sXml = getStringResponse(args);
		System.out.println(sXml);
	}
	
	public static String getStringResponse(String[] args) {
		URI uri = null;
		String data = null;
		if (args.length >= 1) {
			try {
				uri = new URI(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (args.length >= 2) {
			data = args[1];
		}
		
		if (uri == null) {
			//ProbServer server2 = new ProbServer();
			try {
				//server2.run("listProblems", null, null);
				//server2.run("problemXml", new URI("PT_ElementConnections(mode=RS)?count=3&values=100,sym,sym"));
				//server2.run("problemParamDefaults", new URI("PT_ElementConnections?mode=RS"), null);
				//server2.run(cmd, url)
				uri = new URI("questions/");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		ProbServer server = new ProbServer(uri, data);
		String sXml = server.run();
		return sXml;
	}
	
	public ProbServer(URI uri, String data) {
		this.uri = uri;
		// TODO: this.data = data;
		
		root = DocumentHelper.createElement("vault");
	}
	
	public String run() {
		String sPath = uri.getPath();
		String[] as = sPath.split("/");
		List<String> asPathParts = new ArrayList<String>();
		for (String s : as) {
			if (!s.isEmpty())
				asPathParts.add(s);
		}

		run0(asPathParts);
		
		Document doc = DocumentHelper.createDocument();
		if (root.elements().size() == 1)
			doc.add((Element) root.elements().get(0));
		else
			doc.add(root);
		String sXml = toString(doc);
		return sXml;
	}
	
	private void run0(List<String> asPathParts) {
		if (asPathParts.size() == 0) {
			root.addElement("questions").addAttribute("href", "questions/");
			root.addElement("probsets").addAttribute("href", "probsets/");
		}
		else {
			String sPathPart = asPathParts.get(0);
			asPathParts.remove(0);
			if (sPathPart.equals("questions")) {
				QuestionServer qsrv = new QuestionServer();
				Element elem = qsrv.run(asPathParts, uri.getQuery());
				root.add(elem);
			}
			//else if (sPathPart.equals("probsets"))
			else
				root.addElement("error").setText("Unknown path");
		}
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
	
	/*
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
	}*/
}
