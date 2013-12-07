package net.ellisw.quvault.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProblemParams implements Serializable {
	public static class NamedList implements Serializable {
		public String name;
		public String[] list;
		
		public NamedList(String name, String[] list) {
			this.name = name;
			this.list = list;
		}
	}
	
	private String[] alternates;
	private List<NamedList> options = new ArrayList<NamedList>();
	private List<NamedList> settings = new ArrayList<NamedList>();
	
	public String[] getAlternates() { return alternates; }
	public void setAlternates(String[] list) { alternates = list; }
	
	public List<NamedList> getOptions() { return options; }
	public List<NamedList> getSettings() { return settings; }
	
	public void addOption(String name, String[] list) {
		NamedList nl = new NamedList(name, list);
		options.add(nl);
	}
	
	public void addSetting(String name, String[] list) {
		NamedList nl = new NamedList(name, list);
		options.add(nl);
	}
}
