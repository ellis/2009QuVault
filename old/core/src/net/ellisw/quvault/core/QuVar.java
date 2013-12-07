package net.ellisw.quvault.core;

public class QuVar implements Comparable<QuVar> {
	private String name;
	private String nameHtml;
	private String description;
	private String value;
	
	public QuVar(String name) {
		this(name, name, null, null);
	}
	
	public QuVar(String name, String nameHtml, String description) {
		this(name, nameHtml, description, null);
	}
	
	public QuVar(String name, String nameHtml, String description, String value) {
		this.name = name;
		this.nameHtml = nameHtml;
		this.description = description;
		this.value = value;
	}
	
	public String getName() { return name; }
	public void setName(String s) { name = s; }
	
	public String getNameHtml() { return nameHtml; }
	public void setNameHtml(String s) { nameHtml = s; }
	
	public String getDescription() { return description; }
	public void setDescription(String s) { description = s; }
	
	public String getValue() { return value; }
	public void setValue(String s) { value = s; }

	@Override
	public int compareTo(QuVar b) {
		return name.compareTo(b.name);
	}
}
