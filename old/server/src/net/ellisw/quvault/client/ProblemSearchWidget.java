package net.ellisw.quvault.client;

import java.util.List;

import net.ellisw.quvault.core.ProblemHeader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;


public class ProblemSearchWidget extends Composite implements ClickHandler, HasSelectionHandlers<Long> {
	private static final int VISIBLE_ITEM_COUNT = 10;

	private ViewdataServiceAsync viewdataSvc = GWT.create(ViewdataService.class);
	private TextBox txtKeywords;
	private CheckBox chkCanEdit;
	public Button btnNew;
	private HTML lblCount;
	private HTML btnPrev;
	private HTML btnNext;
	private FlexTable table;

	private List<ProblemHeaderViewdata> indexes;
	private int startIndex;
	private int selectedRow = -1;


	public ProblemSearchWidget() {
		VerticalPanel vpanel;

		HorizontalPanel cmdbar = new HorizontalPanel();
		cmdbar.add(new Label("Keywords:"));
		txtKeywords = new TextBox();
		cmdbar.add(txtKeywords);
		chkCanEdit = new CheckBox("Can Edit");
		cmdbar.add(chkCanEdit);
		btnNew = new Button("New");
		cmdbar.add(btnNew);
		HorizontalPanel navbar = new HorizontalPanel();
		navbar.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		btnPrev = new HTML("<a href='javascript:;'>&lt;A</a>LO", true);
		btnPrev.addClickHandler(this);
		navbar.add(btnPrev);
		lblCount = new HTML("1 - 10 of 40");
		navbar.add(lblCount);
		btnNext = new HTML("<a href='javascript:;'>&gt;B</a>LA", true);
		btnNext.addClickHandler(this);
		navbar.add(btnNext);
		HorizontalPanel hpanel = new HorizontalPanel();
		hpanel.add(cmdbar);
		hpanel.add(navbar);

		table = new FlexTable();
		table.addClickHandler(this);
		table.setCellPadding(0);
		table.setCellSpacing(0);
		table.setWidth("100%");
		table.setText(0, 0, "Author");
		table.setText(0, 1, "Topic");
		table.getRowFormatter().setStyleName(0, "mail-ListHeader");

		vpanel = new VerticalPanel();
		vpanel.add(hpanel);
		vpanel.add(table);

		initWidget(vpanel);

		initTable();
		update();
		requestProblemList();
	}

	public HandlerRegistration addSelectionHandler(SelectionHandler<Long> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	/**
	 * Initializes the table so that it contains enough rows for a full page of
	 * emails. Also creates the images that will be used as 'read' flags.
	 */
	private void initTable() {
		// Initialize the rest of the rows.
		for (int i = 0; i < VISIBLE_ITEM_COUNT; ++i) {
			table.setText(i + 1, 0, "");
			table.setText(i + 1, 1, "");
			table.setText(i + 1, 2, "");
			table.getCellFormatter().setWordWrap(i + 1, 0, false);
			table.getCellFormatter().setWordWrap(i + 1, 1, false);
			table.getCellFormatter().setWordWrap(i + 1, 2, false);
			table.getFlexCellFormatter().setColSpan(i + 1, 2, 2);
		}
	}

	private void update() {
		// Update the older/newer buttons & label.
		int count = (indexes != null) ? indexes.size() : 0;
		int max = startIndex + VISIBLE_ITEM_COUNT;
		if (max > count) {
			max = count;
		}

		btnPrev.setVisible(startIndex != 0);
		btnNext.setVisible(startIndex + VISIBLE_ITEM_COUNT < count);
		lblCount.setText("" + (startIndex + 1) + " - " + max + " of " + count);

		// Show the selected emails.
		int i = 0;
		for (; i < VISIBLE_ITEM_COUNT; ++i) {
			// Don't read past the end.
			if (startIndex + i >= count) {
				break;
			}

			ProblemHeaderViewdata index = indexes.get(startIndex + i);
			String sSubject = "<b>" + index.title + "</b> - " + index.keywords;

			// Add a new row to the table, then set each of its columns to the
			// email's sender and subject values.
			table.setText(i + 1, 0, index.author);
			table.setWidget(i + 1, 1, new HTML(sSubject));
		}

		// Clear any remaining slots.
		for (; i < VISIBLE_ITEM_COUNT; ++i) {
			table.setHTML(i + 1, 0, "&nbsp;");
			table.setHTML(i + 1, 1, "&nbsp;");
			table.setHTML(i + 1, 2, "&nbsp;");
		}

		// Select the first row if none is selected.
		if (selectedRow == -1) {
			selectRow(0);
		}
	}

	/**
	 * Selects the given row (relative to the current page).
	 * 
	 * @param row the row to be selected
	 */
	private void selectRow(int row) {
		// When a row (other than the first one, which is used as a header) is
		// selected, display its associated MailItem.
		/*MailItem item = MailItems.getMailItem(startIndex + row);
		if (item == null) {
			return;
		}*/

		styleRow(selectedRow, false);
		styleRow(row, true);

		selectedRow = row;
		//Mail.get().displayItem(item);
		
		/* FIXME: Long id = null;
		if (indexes != null && selectedRow > 0 && selectedRow <= indexes.size())
			id = indexes.get(selectedRow - 1).id;
		SelectionEvent.fire(this, id);*/
	}

	private void styleRow(int row, boolean selected) {
		if (row != -1) {
			if (selected) {
				table.getRowFormatter().addStyleName(row + 1, "mail-SelectedRow");
			} else {
				table.getRowFormatter().removeStyleName(row + 1, "mail-SelectedRow");
			}
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		Object sender = event.getSource();
		if (sender == table) {
			// Select the row that was clicked (-1 to account for header row).
			Cell cell = table.getCellForEvent(event);
			if (cell != null) {
				int row = cell.getRowIndex();
				if (row > 0) {
					selectRow(row);
				}
			}
		}
	}

	public void requestProblemList() {
		// Setup the callback object
		viewdataSvc.getProblemHeaders(
				new AsyncCallback< List<ProblemHeaderViewdata> >() {
					public void onFailure(Throwable caught) {
						// TODO: Add error notification
					}
					public void onSuccess(List<ProblemHeaderViewdata> result) {
						indexes = result;
						update();
					}
				}
		);
	}
}
