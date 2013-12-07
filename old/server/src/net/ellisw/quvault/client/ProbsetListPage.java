package net.ellisw.quvault.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class ProbsetListPage {
	private final TopPanel topPanel = new TopPanel();
	private final FlexTable publicList = new FlexTable();

	private ViewdataServiceAsync viewdataSvc;
	private List<ProbsetIndex> indexes;

	
	public ProbsetListPage() {
		topPanel.setWidth("100%");

		publicList.setText(0, 0, "Problem Set");
		publicList.setStyleName("probsetList");
		//publicList.addClickHandler(new ClickHandler() { public void onClick(ClickEvent e) { onPublicListClick(e); } });

		viewdataSvc = GWT.create(ViewdataService.class);
	}
	
	public void onLoad() {
		final DockPanel dock = new DockPanel();
		dock.setWidth("100%");		
		dock.setBorderWidth(1);
		dock.add(topPanel, DockPanel.NORTH);
		dock.add(publicList, DockPanel.CENTER);
		RootPanel.get("dockContainer").add(dock);
		
		requestProbsetIndexes();
	}
	
	private void requestProbsetIndexes() {
		// Setup the callback object
		viewdataSvc.getProbsetIndexes(
			new AsyncCallback< List<ProbsetIndex> >() {
				public void onFailure(Throwable caught) {
					// TODO: Add error notification
				}
				public void onSuccess( List<ProbsetIndex> result) {
					setProbsetIndexes(result);
				}
			}
		);
	}
	
	private void setProbsetIndexes( List<ProbsetIndex> indexes) {
		this.indexes = indexes;
		
		int iRow = 1;
		for (ProbsetIndex index : indexes) {
			HTML name = new HTML(
					"<a href='probset.html?id=" +
					index.getId() +
					"'>" +
					index.getTitle() +
					"</a>");
			name.setWidth("100%");
			publicList.setWidget(iRow, 0, name);
			/*
			HTML name = new HTML(index.getTitle());
			name.setWidth("100%");
			publicList.setWidget(iRow, 0, name);
			publicList.getRowFormatter().addStyleName(iRow, "probsetListRow");
			*/
			iRow++;
		}
	}
	
	private void onPublicListClick(ClickEvent e) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = publicList.getCellForEvent(e);
		if (cell != null) {
			int row = cell.getRowIndex();
			if (row > 0) {
				//selectRow(row - 1);
				String probsetId = indexes.get(row - 1).getId();
				QuVault.getInstance().showProbsetPage(probsetId);
			}
		}
	}
}
