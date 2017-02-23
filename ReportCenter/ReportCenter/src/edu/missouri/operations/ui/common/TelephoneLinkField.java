/**
 * 
 */
package edu.missouri.operations.ui.common;

import com.vaadin.data.Property;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Link;

import edu.missouri.operations.data.OracleString;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class TelephoneLinkField extends CustomField<String> {

	Link telephoneLink = new Link();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		return telephoneLink;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<? extends String> getType() {
		return String.class;
	}

	@Override
	public void setPropertyDataSource(@SuppressWarnings("rawtypes") Property newDataSource) {

		super.setPropertyDataSource(newDataSource);
		OracleString s = (OracleString) newDataSource.getValue();

		if (s != null) {
			String v = s.toString();
			
			if (null != v) {

				telephoneLink.setCaption(v);
				if (!v.startsWith("+")) {
					v = "+" + v;
				}
				telephoneLink.setResource(new ExternalResource("tel:" + v));
				setVisible(true);
				
			} else {
				setVisible(false);
			}
			
		} else {
			setVisible(false);
		}
	}
}
