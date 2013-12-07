package net.ellisw.quvault.vault;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

public class QuContainer extends QuObject {
	private List<QuObject> children = new ArrayList<QuObject>();
	
	public List<QuObject> getChildren() { return children; }
	
	
	public QuContainer(QuObject parent) {
		super(parent);
	}
	
	public QuContainer createContainer() {
		QuContainer o = new QuContainer(this);
		children.add(o);
		return o; 
	}
	
	public QuProblem createProblem() {
		QuProblem o = new QuProblem(this);
		children.add(o);
		return o;
	}
	
	public QuProblemElec createProblemElec() {
		QuProblemElec o = new QuProblemElec(this);
		children.add(o);
		return o;
	}
	
	public QuQuestionMathlib createQuestionMathlib() {
		QuQuestionMathlib o = new QuQuestionMathlib(this);
		children.add(o);
		return o; 
	}
	
	public QuText createText() {
		QuText o = new QuText(this);
		children.add(o);
		return o; 
	}

	@Override
	public String getXmlTagName() {
		return "container";
	}

	@Override
	public void fillElement(Element elem) {
		super.fillElement(elem);
		
		for (QuObject child : children) {
			Element elemChild = child.getXml();
			elem.add(elemChild);
		}
	}
}
