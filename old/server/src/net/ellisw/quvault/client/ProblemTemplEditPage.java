package net.ellisw.quvault.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class ProblemTemplEditPage {
	private enum Mode {
		None,
		Viewing,
		EditNew,
		EditExisting
	}
	
	private TopPanel topPanel = new TopPanel();
	private ProblemSearchWidget search = new ProblemSearchWidget();
	private FlexTable inputPanel = new FlexTable();
	private Label rev = new Label();
	private TextBox url = new TextBox();
	private TextBox authorId = new TextBox();
	private TextBox authorName = new TextBox();
	private TextBox topic = new TextBox();
	private TextBox keywords = new TextBox();
	private TextBox language = new TextBox();
	private TextArea scriptArea = new TextArea();
	private Button btnNew = new Button("New");
	private Button btnEdit = new Button("Edit");
	private Button btnCopy = new Button("Copy");
	private Button btnAbort = new Button("Abort");
	private Button btnDelete = new Button("Delete");
	private Button btnSave = new Button("Save");
	private Button btnPublish = new Button("Publish");
	private Label status = new Label();
	private ProblemWidget problem = new ProblemWidget();
	private ViewdataServiceAsync viewdataSvc = GWT.create(ViewdataService.class);

	private Mode mode;
	private boolean bBusy;
	private ProblemTemplViewdata script;
	
	
	public ProblemTemplEditPage() {
		topPanel.setWidth("100%");
		btnNew.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				createNewProblemScript();
			}
		});
		btnEdit.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				on_btnEdit_clicked();
			}
		});
		btnSave.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				on_btnSave_clicked();
			}
		});
		search.btnNew.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				createNewProblemScript();
			}
		});
		search.addSelectionHandler(new SelectionHandler<Long>() {
			public void onSelection(SelectionEvent<Long> event) {
				if (event.getSelectedItem() != null)
					requestProblemScript((long) event.getSelectedItem());
				else
					setProblemScript(null);
			}
		});
	}
	
	public void load() {
		DockPanel dock = new DockPanel();
		DisclosurePanel searchPanel = new DisclosurePanel("Search");
		FlexTable panel = new FlexTable();
		HorizontalPanel inputPanelButtons = new HorizontalPanel();
		VerticalPanel problemPanel = new VerticalPanel();
		
		search.setWidth("100%");
		searchPanel.setWidth("100%");
		searchPanel.setContent(search);
		searchPanel.setOpen(true);

		inputPanelButtons.add(btnNew);
		inputPanelButtons.add(btnEdit);
		inputPanelButtons.add(btnCopy);
		inputPanelButtons.add(btnAbort);
		inputPanelButtons.add(btnDelete);
		inputPanelButtons.add(btnSave);
		inputPanelButtons.add(btnPublish);
		inputPanelButtons.add(status);

		url.setWidth("100%");
		authorId.setWidth("100%");
		authorId.setReadOnly(true);
		authorName.setWidth("100%");
		topic.setWidth("100%");
		keywords.setWidth("100%");
		
		int iRow = 0;
		inputPanel.setWidth("100%");
		inputPanel.getColumnFormatter().setWidth(0, "1%");
		inputPanel.setWidget(iRow, 0, inputPanelButtons);
		inputPanel.getFlexCellFormatter().setColSpan(iRow, 0, 2);
		iRow++;
		inputPanel.setWidget(iRow, 0, new Label("Rev #:"));
		inputPanel.setWidget(iRow, 1, rev);
		iRow++;
		inputPanel.setWidget(iRow, 0, new Label("Url:"));
		inputPanel.setWidget(iRow, 1, url);
		iRow++;
		inputPanel.setWidget(iRow, 0, new Label("Author ID:"));
		inputPanel.setWidget(iRow, 1, authorId);
		iRow++;
		inputPanel.setWidget(iRow, 0, new HTML("Author&nbsp;Name:"));
		inputPanel.setWidget(iRow, 1, authorName);
		iRow++;
		inputPanel.setWidget(iRow, 0, new Label("Topic:"));
		inputPanel.setWidget(iRow, 1, topic);
		iRow++;
		inputPanel.setWidget(iRow, 0, new Label("Keywords:"));
		inputPanel.setWidget(iRow, 1, keywords);
		iRow++;
		inputPanel.setWidget(iRow, 0, new Label("Language:"));
		inputPanel.setWidget(iRow, 1, language);
		iRow++;
		inputPanel.setWidget(iRow, 0, new Label("Script:"));
		iRow++;
		scriptArea.setWidth("100%");
		scriptArea.setVisibleLines(20);
		inputPanel.setWidget(iRow, 0, scriptArea);
		inputPanel.getFlexCellFormatter().setColSpan(iRow, 0, 2);

		problemPanel.add(new HTML("PROBLEM"));
		problemPanel.add(problem);
				
		panel.setWidth("100%");
		panel.setBorderWidth(1);
		panel.setWidget(0, 0, searchPanel);
		panel.getFlexCellFormatter().setColSpan(0, 0, 2);
		panel.setWidget(1, 0, inputPanel);
		panel.getFlexCellFormatter().setRowSpan(1, 0, 2);
		panel.setWidget(1, 1, new HTML("PARAMETERS"));
		panel.setWidget(2, 0, problemPanel);
		
		dock.setWidth("100%");
		dock.setBorderWidth(1);
		dock.setWidth("100%");		
		dock.add(topPanel, DockPanel.NORTH);
		dock.add(panel, DockPanel.CENTER);
		
		updateButtonVisibility();
		topPanel.setProbset(null);
		setProblemScript(null);
		setMode(Mode.None, false, "");

		RootPanel.get("dockContainer").add(dock);
	}
	
	public void createNewProblemScript() {
		setProblemScript(new ProblemTemplViewdata());
		setMode(Mode.EditNew, false, "");
	}

	public void requestProblemScript(long id) {
		setProblemScript(null);
		setMode(Mode.None, true, "Loading...");

		// Setup the callback object
		viewdataSvc.getProblemScript(id,
			new AsyncCallback<ProblemTemplViewdata>() {
				public void onFailure(Throwable caught) {
					setMode(Mode.None, false, "");
				}
				public void onSuccess(ProblemTemplViewdata result) {
					setProblemScript(result);
					setMode(Mode.Viewing, false, "");
				}
			}
		);
	}
	
	public void setProblemScript(ProblemTemplViewdata script) {
		this.script = script;
		if (script != null) {
			url.setText(script.getUrl());
			rev.setText(Integer.toString(script.getRev()));
			authorId.setText(script.getAuthorId());
			authorName.setText(script.getAuthorName());
			topic.setText(script.getTopic());
			keywords.setText(script.getKeywords());
			language.setText(script.getLanguage());
			scriptArea.setText(script.getScript());
			requestProblemViewdata();
		}
		else {
			url.setText("");
			rev.setText("");
			authorId.setText("");
			authorName.setText("");
			topic.setText("");
			keywords.setText("");
			language.setText("");
			scriptArea.setText("");
			problem.setProblem(null);
		}
	}
	
	private void setMode(Mode mode, boolean bBusy, String sStatus) {
		this.mode = mode;
		this.bBusy = bBusy;
		status.setText(sStatus);
		
		updateButtonVisibility();
		updateInputEnabled();
	}
	
	private void updateButtonVisibility() {
		btnNew.setVisible(mode == Mode.None || mode == Mode.Viewing);
		btnEdit.setVisible(mode == Mode.Viewing);
		btnCopy.setVisible(mode == Mode.Viewing);
		btnDelete.setVisible(mode == Mode.Viewing);
		btnAbort.setVisible(mode == Mode.EditNew || mode == Mode.EditExisting);
		btnSave.setVisible(mode == Mode.EditNew || mode == Mode.EditExisting);
		btnPublish.setVisible(mode == Mode.EditExisting);
	}
	
	private void updateInputEnabled() {
		boolean bEnable = (mode == Mode.EditNew || mode == Mode.EditExisting) && !bBusy;
		boolean b = !bEnable;
		url.setReadOnly(b);
		authorName.setReadOnly(b);
		keywords.setReadOnly(b);
		topic.setReadOnly(b);
		language.setReadOnly(b);
		scriptArea.setReadOnly(b);
	}

	private void requestProblemViewdata() {
		// Setup the callback object
		viewdataSvc.getProblemViewdataFromScript(script.getLanguage(), script.getScript(), new int[0],
			new AsyncCallback<ProblemViewdata>() {
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(ProblemViewdata result) {
					problem.setProblem(result);
				}
			}
		);
	}
	
	private void on_btnEdit_clicked() {
		setMode(Mode.EditExisting, false, "");
	}
	
	private void on_btnSave_clicked() {
		script.setUrl(url.getText());
		script.setAuthorName(authorName.getText());
		script.setTopic(topic.getText());
		script.setKeywords(keywords.getText());
		script.setLanguage(language.getText());
		script.setScript(scriptArea.getText());
	
		setMode(mode, true, "Saving...");
		
		viewdataSvc.saveProblemScript(script, new AsyncCallback<ProblemTemplViewdata>() {
			public void onFailure(Throwable caught) {
				setMode(mode, false, "Error");
			}
			public void onSuccess(ProblemTemplViewdata script) {
				search.requestProblemList();
				setProblemScript(script);
				if (script != null)
					setMode(Mode.EditExisting, false, "");
				else
					setMode(Mode.None, false, "");
			}
		});
	}
}
