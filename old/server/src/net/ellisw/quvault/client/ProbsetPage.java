package net.ellisw.quvault.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Page for displaying a problem set
 */
public class ProbsetPage {
	private final TopPanel topPanel = new TopPanel();
	private final ProbsetControlPanel probsetControlPanel = new ProbsetControlPanel();
	private final ProbsetProblemsPanel probsetProblemsPanel = new ProbsetProblemsPanel();
	private ViewdataServiceAsync viewdataSvc;
	
	private ProbsetViewdata probset;

	
	public ProbsetPage() {
		topPanel.setWidth("100%");
		probsetControlPanel.setWidth("1%");
		probsetProblemsPanel.setWidth("100%");
	}
	
	/**
	 * This is the entry point method.
	 */
	public void load(String probsetId) {
		final DockPanel dock = new DockPanel();
		dock.setWidth("100%");		
		dock.setBorderWidth(1);
		dock.add(topPanel, DockPanel.NORTH);
		dock.add(probsetControlPanel, DockPanel.WEST);
		dock.add(probsetProblemsPanel, DockPanel.CENTER);
		RootPanel.get("dockContainer").add(dock);
		
		topPanel.setProbset(null);
		probsetControlPanel.setProbset(null);

		if (viewdataSvc == null)
			viewdataSvc = GWT.create(ViewdataService.class);

		requestProbset(probsetId);
	}

	private void requestProbset(String probsetId) {
		// Setup the callback object
		viewdataSvc.getProbsetViewdata(probsetId,
			new AsyncCallback<ProbsetViewdata>() {
				public void onFailure(Throwable caught) {
					// TODO: Add error notification
				}
				public void onSuccess(ProbsetViewdata result) {
					setProbset(result);
				}
			}
		);
	}
	
	private void setProbset(ProbsetViewdata probset) {
		this.probset = probset;
		topPanel.setProbset(probset);
		probsetControlPanel.setProbset(probset);
		
		//for (ProblemData problem : probset.getProblems())
		//	probsetProblemsPanel.addProblem(problem);
		requestNextProblem();
	}
	
	private void requestNextProblem() {
		// Setup the callback object
		viewdataSvc.getProblemViewdata(probset.getId(), 0,
			new AsyncCallback<ProblemViewdata>() {
				public void onFailure(Throwable caught) {
					// TODO: Add error notification
				}
				public void onSuccess(ProblemViewdata result) {
					addProblem(result);
				}
			}
		);
	}
	
	private void addProblem(ProblemViewdata problem) {
		if (problem != null)
			probsetProblemsPanel.addProblem(problem);
		// TODO: now request the next problem if there are more
	}
}
