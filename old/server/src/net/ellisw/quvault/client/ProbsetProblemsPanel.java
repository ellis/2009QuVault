package net.ellisw.quvault.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProbsetProblemsPanel extends Composite {
	private final FlexTable table = new FlexTable();
	
	public ProbsetProblemsPanel() {
		//table.setBorderWidth(1);
		table.setWidth("100%");
		table.setCellSpacing(4);
		table.getColumnFormatter().setWidth(0, "1%");
		table.getColumnFormatter().setWidth(1, "100%");
		initWidget(table);
	}
	
	public void addProblem(ProblemViewdata problem) {
		int iRow = table.getRowCount();
		ProblemWidget w = new ProblemWidget();
		w.setProblem(problem);
		table.setWidget(iRow, 0, new HTML(Integer.toString(iRow + 1) + ".&nbsp;"));
		table.getCellFormatter().setAlignment(iRow, 0, HorizontalPanel.ALIGN_RIGHT, VerticalPanel.ALIGN_TOP);
		table.setWidget(iRow, 1, w);
	}
}
