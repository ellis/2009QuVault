package net.ellisw.quvault.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class QuVault implements EntryPoint {
	private static QuVault instance;
	private ProbsetListPage probsetList;
	private ProbsetPage probset;
	private ProblemTemplEditPage editProblem;

	
	public QuVault() {
		instance = this;
	}
	
	public static QuVault getInstance() { return instance; }
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Element page = DOM.getElementById("page");
		if (page == null) {
			return;
		}
		
		String sPage = page.getAttribute("value");
		if (sPage == null)
			return;
		
		if (sPage.equals("probsets")) {
			probsetList = new ProbsetListPage();
			probsetList.onLoad();
		}
		else if (sPage.equals("probset")) {
			String probsetId = Window.Location.getParameter("id");
			if (probsetId == null)
				return;
			probset = new ProbsetPage();
			probset.load(probsetId);
		}
		else if (sPage.equals("edit_problem")) {
			editProblem = new ProblemTemplEditPage();
			editProblem.load();
		}
	}
	
	public void showProbsetPage(String probsetId) {
		if (probset == null) {
			probset = new ProbsetPage();
		}
		RootPanel.get("dockContainer").clear();
		probset.load(probsetId);
	}
}
