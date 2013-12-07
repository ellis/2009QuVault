package net.ellisw.quvault.core;

import java.util.ArrayList;
import java.util.List;

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
}
