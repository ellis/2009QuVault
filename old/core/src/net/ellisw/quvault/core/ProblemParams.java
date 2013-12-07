package net.ellisw.quvault.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ProblemParams {
	private List<String> names = new ArrayList<String>();
	private Map<String, String> values = new HashMap<String, String>();
	
	public String get(String name) {
		return values.get(name);
	}
	
	public void put(String name, String value) {
		if (!values.containsKey(name))
			names.add(name);
		values.put(name, value);
	}
	
	public List<String> keySet() {
		return names;
	}
	
	public Map<String, String> getMap() { return values; }
	
	public void updateFrom(Map<String, String> map) {
		for (Entry<String, String> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
}
