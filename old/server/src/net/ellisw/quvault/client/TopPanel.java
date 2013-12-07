package net.ellisw.quvault.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The top panel, which contains the 'welcome' message and various links.
 */
public class TopPanel extends Composite implements ClickHandler {
	private Label lblTitle;
	
	
	/**
	 * An image bundle for this widgets images.
	 */
	/*public interface Images extends ImageBundle {
		AbstractImagePrototype logo();
	}*/

	private HTML signOutLink = new HTML("<a href='javascript:;'>Sign Out</a>");
	private HTML aboutLink = new HTML("<a href='javascript:;'>About</a>");

	public TopPanel(/*Images images*/) {
		lblTitle = new Label();
		
		HorizontalPanel outer = new HorizontalPanel();
		VerticalPanel rightLayout = new VerticalPanel();

		outer.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		lblTitle.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		rightLayout.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);

		HorizontalPanel links = new HorizontalPanel();
		links.setSpacing(4);
		links.add(signOutLink);
		links.add(aboutLink);

		/*final Image logo = images.logo().createImage();
		outer.add(logo);
		outer.setCellHorizontalAlignment(logo, HorizontalPanel.ALIGN_LEFT);*/

		outer.add(lblTitle);
		outer.add(rightLayout);
		rightLayout.add(new HTML("<b>Welcome back, foo@example.com</b>"));
		rightLayout.add(links);

		signOutLink.addClickHandler(this);
		aboutLink.addClickHandler(this);

		initWidget(outer);
		//setStyleName("mail-TopPanel");
		//links.setStyleName("mail-TopPanelLinks");
	}

	public void onClick(ClickEvent event) {
		Object sender = event.getSource();
		if (sender == signOutLink) {
			Window.alert("If this were implemented, you would be signed out now.");
		} else if (sender == aboutLink) {
			// When the 'About' item is selected, show the AboutDialog.
			// Note that showing a dialog box does not block -- execution continues
			// normally, and the dialog fires an event when it is closed.
			Window.alert("If this were implemented, you would see an about dialog.");
			/*
			AboutDialog dlg = new AboutDialog();
			dlg.show();
			dlg.center();*/
		}
	}
	
	public void setProbset(ProbsetViewdata probset) {
		if (probset == null)
			lblTitle.setText("Loading...");
		else
			lblTitle.setText(probset.getTitle());
	}
}
