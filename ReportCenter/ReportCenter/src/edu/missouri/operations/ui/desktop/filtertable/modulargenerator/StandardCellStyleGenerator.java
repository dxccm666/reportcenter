package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import java.math.BigDecimal;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;

import edu.missouri.operations.data.OracleBoolean;
import edu.missouri.operations.data.OracleCurrency;
import edu.missouri.operations.data.OracleDecimal;
import edu.missouri.operations.data.OracleRowNumber;

@SuppressWarnings("serial")
public class StandardCellStyleGenerator implements CellStyleGenerator {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	HashMap<String, String> propertyStyles = new HashMap<String, String>();
	
	public void assignStyle(String propertyId, String style) {
		propertyStyles.put(propertyId, style);
	}

	@Override
	public String getStyle(Table source, Object itemId, Object propertyId) {
		if (propertyId != null) {
			
			if (source.getContainerProperty(itemId, propertyId) == null) {
				logger.warn("null found in cellstylegenerator");
				return null;
			}
			
			if(propertyStyles.containsKey(propertyId)) {
				return propertyStyles.get(propertyId);
			}
			
			Class<?> propertyType = source.getContainerProperty(itemId, propertyId).getType();
			if (propertyType == OracleCurrency.class || propertyType == OracleDecimal.class || propertyType == BigDecimal.class
					|| propertyType == OracleRowNumber.class) {
				
				Number n = (Number) source.getContainerProperty(itemId, propertyId).getValue();
				if(n!=null && n.intValue() < 0) {
					return "numerical negative";
				} else {
					return "numerical";
				}
			}
			
			// add styles here for icons and text. then add the style in the
			// table cell's in projex.scss
			// add styles here. set the name of the image that you want to use
			// based on this:
			// background-image:
			// url('icons/chalkwork/basic/#{$image}_16x16.png');
			// then call @include icon(confirm); where confirm is the image name
			
			if (propertyType == OracleBoolean.class) {
				if (!source.isEditable()) {
					if (source.getContainerProperty(itemId, propertyId).getValue() != null) {
						if (((OracleBoolean) source.getContainerProperty(itemId, propertyId).getValue()).toBoolean()) {
							return "confirm_notext";
						} else {
							return "notext";
						}
					}
				} else {
					return "";
				}
			}
			return getAdditionalStyles(source, itemId, propertyId);
		}
		return null;
	}

	/**
	 * Override this to add extra styles. add styles here for icons and text.
	 * then add the style in the table cell's in projex.scss add styles here.
	 * set the name of the image that you want to use based on this:
	 * background-image: url('icons/chalkwork/basic/#{$image}_16x16.png'); then
	 * call @include icon(confirm); where confirm is the image name
	 */
	public String getAdditionalStyles(Table source, Object itemId, Object propertyId) {
		return null;
	}
}
