/**
 * 
 */
package edu.missouri.operations.ui;

import org.vaadin.hene.expandingtextarea.ExpandingTextArea;

import com.vaadin.data.Property;

import edu.missouri.operations.data.OracleStringToStringConverter;



/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class OracleStringTextArea extends ExpandingTextArea {

	/**
	 * 
	 */
	public OracleStringTextArea() {
		super();
		setConverter(new OracleStringToStringConverter());
		setNullRepresentation("");
	}

	/**
	 * @param caption
	 */
	public OracleStringTextArea(String caption) {
		super(caption);
		setConverter(new OracleStringToStringConverter());
		setNullRepresentation("");
	}

	/**
	 * @param dataSource
	 */
	@SuppressWarnings("rawtypes")
	public OracleStringTextArea(Property dataSource) {
		super();
		setConverter(new OracleStringToStringConverter());
		setPropertyDataSource(dataSource);
		setNullRepresentation("");
	}

	/**
	 * @param caption
	 * @param dataSource
	 */
	@SuppressWarnings("rawtypes")
	public OracleStringTextArea(String caption, Property dataSource) {
		super(caption);
		setConverter(new OracleStringToStringConverter());
		setPropertyDataSource(dataSource);
		setNullRepresentation("");
	}

	/**
	 * @param caption
	 * @param value
	 */
	public OracleStringTextArea(String caption, String value) {
		super(caption);
		setConverter(new OracleStringToStringConverter());
		setConvertedValue(value);
		setNullRepresentation("");
	}

}
