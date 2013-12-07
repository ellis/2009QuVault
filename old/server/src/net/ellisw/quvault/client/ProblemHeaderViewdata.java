package net.ellisw.quvault.client;

import java.io.Serializable;

public class ProblemHeaderViewdata implements Serializable {
	public String url;
	public String title;
	public String author;
	public String keywords;
	
	public ProblemHeaderViewdata() {
	}
	
	public ProblemHeaderViewdata(String url, String title, String author, String keywords) {
		this.url = url;
		this.title = title;
		this.author = author;
		this.keywords = keywords;
	}
}
