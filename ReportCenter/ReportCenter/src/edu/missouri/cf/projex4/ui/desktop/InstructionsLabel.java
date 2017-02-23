package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class InstructionsLabel extends Label {

	public InstructionsLabel(String content) {
		super("<span style=\"padding-top: 16px; padding-bottom: 6px\"><span style=\"font-size: 1.5em; font-weight: bold;\">Instructions</span><br/>" + content +"</span>", ContentMode.HTML);
	}

	@SuppressWarnings("rawtypes")
	public InstructionsLabel(Property contentSource) {
		super(contentSource);
	}

}
