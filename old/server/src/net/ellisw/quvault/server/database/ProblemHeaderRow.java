package net.ellisw.quvault.server.database;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ProblemHeaderRow {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private String title;
	
	@Persistent
	private String author;
	
	@Persistent
	private String keywords;

	@Persistent
	private String source;

	@Persistent
	private String sourceUri;
	
	
	public Long getId() { return id; }

	public String getTitle() { return title; }
	public void setTitle(String s) { title = s; }

	public String getAuthor() { return author; }
	public void setAuthor(String s) { author = s; }

	public String getKeywords() { return keywords; }
	public void setKeywords(String s) { keywords = s; }

	public String getSource() { return source; }
	public void setSource(String s) { source = s; }

	public String getSourceUri() { return sourceUri; }
	public void setSourceUri(String s) { sourceUri = s; }
}
