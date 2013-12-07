package net.ellisw.quvault.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FileModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private Long prevRevId;
	
	@Persistent
	private int rev;
	
	@Persistent
	private Date date;
	
	@Persistent
	private String path;
	
	@Persistent
	private String authorId;

	@Persistent
	private Map<String, String> properties = new HashMap<String, String>();
	
	@Persistent
	private Blob blob;
	

	public Long getId() { return id; }

	public Long getPrevRevId() { return prevRevId; }
	public void setPrevRevId(Long n) { prevRevId = n; }

	public int getRev() { return rev; }
	public void setRev(int n) { rev = n; }

	public Date getDate() { return date; }
	public void setDate(Date dt) { date = dt; }

	public String getPath() { return path; }
	public void setPath(String s) { path = s; }

	public String getAuthorId() { return authorId; }
	public void setAuthorId(String s) { authorId = s; }

	public Map<String, String> getProperties() { return properties; }
	
	public Blob getBlob() { return blob; }
	public void setBlob(Blob blob) { this.blob = blob; }
}
