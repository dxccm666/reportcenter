package edu.missouri.cf.projex4.ui.desktop;

import java.io.Serializable;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tepi.filtertable.FilterDecorator;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.datefield.Resolution;

@SuppressWarnings("serial")
public class ProjexFilterDecorator implements FilterDecorator, Serializable {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String getEnumFilterDisplayName(Object propertyId, Object value) {
		logger.debug("getEnumFilterDisplayName({}, {})", new Object[]{propertyId, value});
		if ("STATUS".equals(propertyId)) {
			if (value != null) {
				switch (value.toString()) {
				case "CREATED":
					return "Created";
				}
			}
		}
		return null;
	}

	@Override
	public Resource getEnumFilterIcon(Object propertyId, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
		if ("ISPRIMARY".equals(propertyId)) {
			if (value) {
				//return new ThemeResource("icons/chalkwork/basic/confirm_16x16.png");
			}
		}
		return null;
	}

	@Override
	public boolean isTextFilterImmediate(Object propertyId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getTextChangeTimeout(Object propertyId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getFromCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClearCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resolution getDateFieldResolution(Object propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDateFormatPattern(Object propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAllItemsVisibleString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberFilterPopupConfig getNumberFilterPopupConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean usePopupForNumericProperty(Object propertyId) {
		// TODO Auto-generated method stub
		return false;
	}

}
