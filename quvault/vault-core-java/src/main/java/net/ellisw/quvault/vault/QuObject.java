package net.ellisw.quvault.vault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public abstract class QuObject {
	private QuObject parent;
	private boolean inheritParentParams;
	private boolean inheritParentVars;
	private boolean inheritParentResources;
	private boolean enumerated;
	private Map<String, String> params = new HashMap<String, String>();
	// REFACTOR: change vars to LinkedHashMap and get rid of varMap -- ellis, 2009-05-23
	private Set<QuVar> vars = new LinkedHashSet<QuVar>();
	private Map<String, QuVar> varMap = new HashMap<String, QuVar>();
	private Map<String, QuResource> resources = new HashMap<String, QuResource>();

	
	public QuObject(QuObject parent) {
		this.parent = parent;
		
		inheritParentParams = true;
		inheritParentVars = true;
		inheritParentResources = true;
	}
	
	public QuObject getParent() { return parent; }
	public void setParent(QuObject o) { parent = o; }

	public boolean getInheritParentParams() { return inheritParentParams; }
	public void setInheritParentParams(boolean b) { inheritParentParams = b; }
	
	public boolean getInheritParentVars() { return inheritParentVars; }
	public void setInheritParentVars(boolean b) { inheritParentVars = b; }
	
	public boolean getInheritParentResources() { return inheritParentResources; }
	public void setInheritParentResources(boolean b) { inheritParentResources = b; }
	
	public boolean getEnumerated() { return enumerated; }
	public void setEnumerated(boolean b) { enumerated = b; }
	
	public String getParam(String name) {
		if (params.containsKey(name)) {
			return params.get(name);
		}
		else if (parent != null && inheritParentParams) {
			return parent.getParam(name);
		}
		return null;
	}
	
	public QuVar getVar(String name) {
		if (varMap.containsKey(name)) {
			return varMap.get(name);
		}
		else if (parent != null && inheritParentVars) {
			return parent.getVar(name);
		}
		return null;
	}

	public QuResource getResource(String name) {
		if (resources.containsKey(name))
			return resources.get(name);
		else if (parent != null && inheritParentResources)
			return parent.getResource(name);
		return null;
	}
	
	public void addParams(Map<String, String> params) {
		this.params.putAll(params);
	}
	
	public void setParam(String name, String value) {
		params.put(name, value);
	}
	
	public void setVar(String name) {
		QuVar var = new QuVar(name);
		varMap.put(name, var);
		vars.add(var);
	}
	
	public void setVar(String name, String nameHtml, String description) {
		QuVar var = new QuVar(name, nameHtml, description);
		varMap.put(name, var);
		vars.add(var);
	}
	
	public void setResource(String name, String type, Node content) {
		QuResource resource = new QuResource();
		resource.name = name;
		resource.type = type;
		resource.content = content;
		resources.put(name, resource);
	}

	public Map<String, String> fillParamMap() {
		Map<String, String> map = new HashMap<String, String>();
		fillParamMap(map);
		return map;
	}
	
	public void fillParamMap(Map<String, String> map) {
		if (parent != null && inheritParentParams) {
			parent.fillParamMap(map);
		}
		map.putAll(params);
	}
	
	public void fillVarMap(Map<String, QuVar> map) {
		if (parent != null && inheritParentParams) {
			parent.fillVarMap(map);
		}
		map.putAll(varMap);
	}
	
	public void fillVarSet(Set<QuVar> vset) {
		if (parent != null && inheritParentParams) {
			parent.fillVarSet(vset);
		}
		vset.addAll(vars);
	}
	
	public abstract String getXmlTagName();
	
	public Element getXml() {
		String name = getXmlTagName();
		Element elem = DocumentHelper.createElement(name);
		fillElement(elem);
		return elem;
	}
	
	protected void fillElement(Element elem) {
		if (!inheritParentParams)
			elem.addAttribute("inheritParentParams", "0");
		if (!inheritParentVars)
			elem.addAttribute("inheritParentVars", "0");
		if (!inheritParentResources)
			elem.addAttribute("inheritParentResources", "0");
		if (enumerated)
			elem.addAttribute("enumerated", "1");

		for (QuVar var : vars) {
			Element elemVar = elem.addElement("variable");
			elemVar.addAttribute("name", var.getName());
			elemVar.addAttribute("nameXhtml", var.getNameHtml());
			if (var.getDescription() != null)
				elemVar.setText(var.getDescription());
		}
		
		List<String> keys;
		keys = new ArrayList<String>(resources.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		for (String key : keys) {
			QuResource res = resources.get(key);
			Element elemRes = elem.addElement("resource");
			elemRes.addAttribute("name", key);
			elemRes.addAttribute("type", res.type);
			elemRes.add(res.content);
		}
	}
}
