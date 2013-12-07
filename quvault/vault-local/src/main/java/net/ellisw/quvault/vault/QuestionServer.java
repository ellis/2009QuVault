package net.ellisw.quvault.vault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import net.ellisw.quvault.vault.CoreUtils;
import net.ellisw.quvault.vault.IProblemTempl;
import net.ellisw.quvault.vault.ProblemHeader;
import net.ellisw.quvault.vault.ProblemParamSpecs;
import net.ellisw.quvault.vault.QuContainer;


public class QuestionServer {
	private String m_sQuery;
	private Map<String, String> m_paramsClass; 
	private Map<String, String> m_paramsUri; 
	
	
	public QuestionServer() {
	}
	
	public Element run(List<String> asPathParts, String sQuery) {
		m_sQuery = sQuery;
		return handle_questions(asPathParts);
	}
	
	private Element handle_questions(List<String> asPathParts) {
		Element root = null;
		
		if (asPathParts.size() == 0) {
			root = DocumentHelper.createElement("questions"); 
			List<ProblemHeader> headers = getQuestionList();
			for (ProblemHeader header : headers) {
				root.addElement("header")
					.addAttribute("title", header.title)
					.addAttribute("author", header.author)
					.addAttribute("keywords", header.keywords)
					.addAttribute("id", header.uri);
			}
		}
		else {
			String sPathPart = asPathParts.get(0);
			asPathParts.remove(0);

			IProblemTempl t = getProblemTempl(sPathPart);
			if (t != null)
				root = handle_question(t, asPathParts);
			else {
				root = DocumentHelper.createElement("error");
				root.setText("Unknown path");
			}
		}
		
		return root;
	}
	
	private Element handle_question(IProblemTempl t, List<String> asPathParts) {
		Element root = null;
		
		if (asPathParts.size() == 0) {
			root = DocumentHelper.createElement("listing");
			root.addElement("a").addAttribute("href", "param-specs/").setText("param-specs");
			root.addElement("a").addAttribute("href", "param-defaults/").setText("param-defaults");
			root.addElement("a").addAttribute("href", "param-fixup/").setText("param-fixup");
			root.addElement("a").addAttribute("href", "xml/").setText("xml");
			root.addElement("a").addAttribute("href", "xml-and-params/").setText("xml-and-params");
		}
		else if (asPathParts.size() == 1) {
			String sPathPart = asPathParts.get(0);

			// Get passed parameters
			m_paramsUri = CoreUtils.parseUriQuery(m_sQuery);
			
			if (sPathPart.equals("param-specs") || sPathPart.equals("param-specs-0"))
				root = handle_question_specs(t, 0);
			if (sPathPart.equals("param-specs-1"))
				root = handle_question_specs(t, 1);
			if (sPathPart.equals("param-specs-2"))
				root = handle_question_specs(t, 2);
			else if (sPathPart.equals("param-defaults") || sPathPart.equals("param-defaults-0"))
				root = handle_question_defaults(t, 0);
			else if (sPathPart.equals("param-defaults-1"))
				root = handle_question_defaults(t, 1);
			else if (sPathPart.equals("param-defaults-2"))
				root = handle_question_defaults(t, 2);
			else if (sPathPart.equals("param-fixup"))
				root = handle_question_fixup(t);
			else if (sPathPart.equals("xml"))
				root = handle_question_xml(t);
			else if (sPathPart.equals("xml-and-params"))
				root = handle_question_xml_and_params(t);
		}
		
		if (root == null) {
			root = DocumentHelper.createElement("error");
			root.setText("Unknown path");
		}
		return root;
	}
	
	private Element handle_question_specs(IProblemTempl t, int iLevel) {
		t.setParams(m_paramsUri);

		Element xmlSpecs = DocumentHelper.createElement("param-specs");
		getQuestionParamSpecs(t, iLevel, xmlSpecs);
		return xmlSpecs;
	}
	
	private Element handle_question_defaults(IProblemTempl t, int iLevel) {
		t.setParams(m_paramsUri);

		Map<String, String> params = getQuestionParamDefaults(t, iLevel);
		return getQuestionParamsElement(params);
	}
	
	private Element handle_question_fixup(IProblemTempl t) {
		t.setParams(m_paramsUri);
		Map<String, String> params = getQuestionParamFixup(t);
		return getQuestionParamsElement(params);
	}
	
	private Element handle_question_xml(IProblemTempl t) {
		t.setParams(m_paramsUri);

		return getQuestion(t);
	}
	
	private Element handle_question_xml_and_params(IProblemTempl t) {
		t.setParams(m_paramsUri);
		Map<String, String> params = getQuestionParamFixup(t);
		t.setParams(params);
		
		Element root = DocumentHelper.createElement("root");
		getQuestionParamSpecs(t, root);
		root.add(getQuestionParamsElement(params));
		root.add(getQuestion(t));
		return root;
	}
	
	private List<ProblemHeader> getQuestionList() {
		ArrayList<ProblemHeader> headers = new ArrayList<ProblemHeader>();
		//PT_ElementConnections.setupHeaders(headers);
		PT_RCSeries.setupHeaders(headers);
		PT_ThreeResistorCircuit.setupHeaders(headers);
		return headers;
	}
	
	private IProblemTempl getProblemTempl(String sQuestionId) {
		String sClass = sQuestionId;
		String sQuery = "";
		
		int iParenLeft = sClass.indexOf('(');
		int iParenRight= sClass.indexOf(')');
		if (iParenLeft >= 0 && iParenRight > iParenLeft) {
			sQuery = sClass.substring(iParenLeft + 1, iParenRight);
			sClass = sClass.substring(0, iParenLeft);
		}
		
		m_paramsClass = CoreUtils.parseUriQuery(sQuery);
		IProblemTempl t = null;
		
		try {
			sClass = "net.ellisw.quvault.vault." + sClass;
			Class<?> clazz = this.getClass().getClassLoader().loadClass(sClass);
			t = (IProblemTempl) clazz.getConstructor().newInstance();
			t.setInitialParams(m_paramsClass);
		} catch (Exception e) {
			e.printStackTrace();
			t = null;
		}
		
		return t;
	}
	
	private void getQuestionParamSpecs(IProblemTempl t, Element parent) {
		int iLevel = 0;
		while (true) {
			Element xmlSpecs = DocumentHelper.createElement("param-specs");
			boolean bContinue = getQuestionParamSpecs(t, iLevel, xmlSpecs);
			if (!bContinue)
				break;
			parent.add(xmlSpecs);
			iLevel++;
		}
	}
	
	private boolean getQuestionParamSpecs(IProblemTempl t, int iLevel, Element xmlSpecs) {
		ProblemParamSpecs specs = new ProblemParamSpecs();
		boolean bContinue = t.setupParamSpecs(iLevel, specs);

		xmlSpecs.addAttribute("level", Integer.toString(iLevel));
		for (ProblemParamSpecs.Var var : specs.getVars()) {
			xmlSpecs.add(var.getXml());
		}
		return bContinue;
	}
	
	private Map<String, String> getQuestionParamDefaults(IProblemTempl t, int iLevel) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		t.loadParamDefaults(iLevel, params);
		return params;
	}
	
	private Map<String, String> getQuestionParamFixup(IProblemTempl t) {
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String> defaults = new HashMap<String, String>();
		int iLevel = 0;
		while (true) {
			boolean bContinue = t.loadParamDefaults(iLevel, defaults);

			boolean bChanged = false;
			for (Entry<String, String> entry : defaults.entrySet()) {
				String sName = entry.getKey();
				String sValue = entry.getValue();
				if (m_paramsUri.containsKey(sName)) {
					if (!m_paramsUri.get(sName).equals(sValue)) {
						sValue = m_paramsUri.get(sName);
						bChanged = true;
					}
				}
				else {
					bChanged = true;
				}
				params.put(sName, sValue);
			}
			
			if (bChanged)
				t.setParams(params);
			
			if (!bContinue)
				break;
			iLevel++;
		}
		/*
		// Make a copy of the URI parameters
		Map<String, String> paramsUri = new HashMap<String, String>(m_paramsUri);

		// Get default parameters
		Map<String, String> params = new LinkedHashMap<String, String>();
		int iLevel = 0;
		while (true) {
			boolean bContinue = t.loadParamDefaults(iLevel, params);

			boolean bChanged = false;
			for (Entry<String, String> entry : params.entrySet()) {
				String sName = entry.getKey();
				String sValue = entry.getValue();
				if (paramsUri.containsKey(sName)) {
					if (!paramsUri.get(sName).equals(sValue)) {
						
					}
					paramsUri.remove(sName);
				}
				if (!dest.containsKey(sName) || !dest.get(Name).equals(sValue)) {
					dest.put(entry.getKey(), entry.getValue());
					bChanged = true;
				}
			}
			
		}
		*/
		/*Map<String, String> params = new HashMap<String, String>(m_paramsUri);
		for (int iLevel = 0; t.loadParamDefaults(iLevel, params); iLevel++) {
			t.setParams(params);
		}*/
		
		return params;
	}
	
	private  Element getQuestionParamsElement(Map<String, String> params) {
		Element root = DocumentHelper.createElement("params");
		for (String name : params.keySet()) {
			root.addElement("param").addAttribute("name", name).addAttribute("value", params.get(name));
		}
		return root;
	}

	private Element getQuestion(IProblemTempl t) {
		// Get default parameters
		Map<String, String> params = getQuestionParamFixup(t);

		QuContainer container = new QuContainer(null);
		container.addParams(params);
		t.setParams(params);
		t.setupProblemData(container);

		return container.getXml();
	}
	
	/*
	private boolean updateMapFromMap(Map<String, String> dest, Map<String, String> source) {
		boolean bChanged = false;
		for (Entry<String, String> entry : source.entrySet()) {
			String sName = entry.getKey();
			String sValue = entry.getValue();
			if (!dest.containsKey(sName) || !dest.get(sName).equals(sValue)) {
				dest.put(entry.getKey(), entry.getValue());
				bChanged = true;
			}
		}
		return bChanged;
	}
	*/
}
