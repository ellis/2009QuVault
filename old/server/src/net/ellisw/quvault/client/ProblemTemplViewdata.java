package net.ellisw.quvault.client;

import java.io.Serializable;


public class ProblemTemplViewdata implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String url;
	private int rev;
	private String authorId;
	private String authorName;
	private String topic;
	private String keywords;
	private String language;
	private String script;

	public Long getId() { return id; }
	public void setId(Long n) { id = n; }

	public String getUrl() { return url; }
	public void setUrl(String s) { url = s; }

	public int getRev() { return rev; }
	public void setRev(int n) { rev = n; }

	public String getAuthorId() { return authorId; }
	public void setAuthorId(String s) { authorId = s; }

	public String getAuthorName() { return authorName; }
	public void setAuthorName(String s) { authorName = s; }

	public String getKeywords() { return keywords; }
	public void setKeywords(String s) { keywords = s; }

	public String getLanguage() { return language; }
	public void setLanguage(String s) { language = s; }

	public String getTopic() { return topic; }
	public void setTopic(String s) { topic = s; }

	public String getScript() { return script; }
	public void setScript(String s) { script = s; }
}
