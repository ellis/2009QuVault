package net.ellisw.quvault.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProbsetControlPanel extends Composite {
	private Button btnCheck;
	private Button btnAddProblem;
	//private Button btnAddQuestion;
	private ProbsetViewdata probset;
	
	
	public ProbsetControlPanel() {
		btnCheck = new Button("Check");
		btnAddProblem = new Button("AddProblem");
		
		VerticalPanel layout = new VerticalPanel();
		layout.add(btnCheck);
		layout.add(btnAddProblem);
		initWidget(layout);
		
		updateWidgets();
	}
	
	public void setProbset(ProbsetViewdata probset) {
		this.probset = probset;
		updateWidgets();
	}
	
	public void updateWidgets() {
		boolean b = (probset != null);
		btnCheck.setEnabled(b);
	}
	
	
}
