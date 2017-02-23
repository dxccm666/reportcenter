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
public class EmailLinkField extends CustomField<String> {

	Link addressLink = new Link();

	@Override
	protected Component initContent() {
		return addressLink;
	}

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
				addressLink.setCaption(v);
				addressLink.setResource(new ExternalResource("mailto:" + v));
				setVisible(true);
			} else {
				setVisible(false);
			}
			
		} else {
			setVisible(false);
		}

	}

}
