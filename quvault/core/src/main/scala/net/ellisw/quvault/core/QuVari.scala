package net.ellisw.quvault.core

class QuVari(
  val name: String,
  val nameHtml: String,
  val description: Option[String]
) {
  require(name != null)
  
  private var m_value: String = _
  
  def this(name: String) = this(name, name, None) 
  
  def value = m_value
  def setValue(s: String) { m_value = s }
  
/*	
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
*/
//	@Override
//	def compare(that: QuVar) = name.compare(that.name)
}
