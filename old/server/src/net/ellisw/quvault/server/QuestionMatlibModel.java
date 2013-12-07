package net.ellisw.quvault.server;

import java.util.ArrayList;
import java.util.List;

public class QuestionMatlibModel extends QuestionModel {
	public static class Var {
		private String namePlain;
		private String nameHtml;
		private String description;
		
		public Var(String namePlain, String nameHtml, String description) {
			this.namePlain = namePlain;
			this.nameHtml = nameHtml;
			this.description = description;
		}
		
		public String getNamePlain() { return namePlain; }
		public void setNamePlain(String s) { namePlain = s; }
		
		public String getNameHtml() { return nameHtml; }
		public void setNameHtml(String s) { nameHtml = s; }
		
		public String getDescription() { return description; }
		public void setDescription(String s) { description = s; }
	}
	
	private final List<Var> findVars = new ArrayList<Var>();
	private final List<Var> givenVars = new ArrayList<Var>();
	
	public List<Var> getFindVars() { return findVars; }
	public List<Var> getGivenVars() { return givenVars; }
	
	public void addFindVar(String namePlain, String nameHtml, String description) {
		Var var = new Var(namePlain, nameHtml, description);
		findVars.add(var);
	}
	
	public void addGivenVar(String namePlain, String nameHtml, String description) {
		Var var = new Var(namePlain, nameHtml, description);
		givenVars.add(var);
	}
}
