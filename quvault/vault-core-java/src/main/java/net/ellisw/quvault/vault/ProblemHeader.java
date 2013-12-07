package net.ellisw.quvault.vault;

public class ProblemHeader {
	public String uri;
	public String title;
	public String author;
	public String keywords;
	
	public ProblemHeader() {
	}
	
	public ProblemHeader(String uri, String title, String author, String keywords) {
		this.uri = uri;
		this.title = title;
		this.author = author;
		this.keywords = keywords;
	}
}
