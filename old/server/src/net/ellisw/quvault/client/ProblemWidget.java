package net.ellisw.quvault.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProblemWidget extends Composite {
	private final HTML text = new HTML();
	private final FlexTable questionTable = new FlexTable();
	
	public ProblemWidget() {
		questionTable.setBorderWidth(0);
		questionTable.setCellPadding(0);
		questionTable.setCellSpacing(0);

		VerticalPanel vpanel = new VerticalPanel();
		
		vpanel.add(text);
		vpanel.add(questionTable);
		
		initWidget(vpanel);
	}
	
	public void setProblem(ProblemViewdata data) {
		if (data != null)
			updateWidgets(data);
		else {
			text.setHTML("");
			questionTable.clear();
		}
	}

	private void updateWidgets(ProblemViewdata data) {
		text.setHTML(data.getText());

		int nQuestions = data.getQuestions().size();
		boolean bShowInfo = (nQuestions > 1);
		for (int i = 0; i < nQuestions; i++) {
			QuestionScope question = data.getQuestions().get(i);
			if (question.getType() == QuestionType.Matlib) {
				QuestionMatlibScope qMatlib = (QuestionMatlibScope) question;
				QuestionMatlibWidget w = new QuestionMatlibWidget(qMatlib, bShowInfo);
				
				int iCol = 0;
				if (nQuestions > 1) {
					questionTable.setWidget(i, 0, new HTML(((char)('a' + i)) + ")&nbsp;&nbsp;"));
					questionTable.getCellFormatter().setAlignment(i, 0, HorizontalPanel.ALIGN_RIGHT, VerticalPanel.ALIGN_TOP);
					iCol++;
				}
				questionTable.setWidget(i, iCol, w);
			}
		}
	}
}
