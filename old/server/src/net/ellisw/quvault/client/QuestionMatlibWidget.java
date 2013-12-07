package net.ellisw.quvault.client;

import java.util.ArrayList;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class QuestionMatlibWidget extends QuestionWidget {
	public QuestionMatlibWidget(QuestionMatlibScope question, boolean bShowInfo) {
		VerticalPanel layout = new VerticalPanel();
		HorizontalPanel answerLayout = new HorizontalPanel();
		answerLayout.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);

		layout.add(new HTML(question.getText()));

		// Function header
		String s = question.getAnswerHeader();
		if (s != null && !s.isEmpty()) {
			layout.add(new HTML(s));
			// TODO: add edit box
			layout.add(new HTML(question.getAnswerFooter()));
		}
		else {
			answerLayout.add(new HTML(question.getAnswerPrefix()));
			
			TextBox answer = new TextBox();
			s = question.getAnswer();
			if (s != null && !s.isEmpty())
				answer.setText(s);
			answerLayout.add(answer);
		}
		layout.add(answerLayout);
		
		initWidget(layout);
	}
}
