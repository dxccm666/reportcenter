package edu.missouri.operations.ui;

import com.vaadin.data.util.converter.Converter;

/**
 * @author graumannc
 *
 * Simple Bean for table column data.
 */
public class TableColumn {
	
	private String dbName;
	private String displayName;
	private Class<?> overrideClass;
	private Class<?> displayClass;
	private Class<?> editorClass;
	private int width = 0;
	private boolean collapsed = false;
	private boolean readOnly = false;
	private float expandRatio = 0.0f;
	private String styles;
	private boolean required = false;
	private boolean alwaysEditable = false;
	private Converter<String,?> converter = null;
	private String constructorParameter;
	
	public TableColumn() {
	}
	
	/**
	 * @param dbName
	 * @param displayName
	 */
	public TableColumn(String dbName, String displayName) {
		setDbName(dbName);
		setDisplayName(displayName);
	}
	
	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName the dbName to set
	 */
	public TableColumn setDbName(String dbName) {
		this.dbName = dbName;
		return this;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public TableColumn setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	/**
	 * @return the overrideClass
	 */
	public Class<?> getOverrideClass() {
		return overrideClass;
	}

	/**
	 * @param overrideClass the overrideClass to set
	 */
	public TableColumn setOverrideClass(Class<?> overrideClass) {
		this.overrideClass = overrideClass;
		return this;
	}
	
	public boolean hasOverride() {
		return (overrideClass!=null);
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public TableColumn setWidth(int width) {
		this.width = width;
		return this;
	}

	/**
	 * @return the collapsed
	 */
	public boolean isCollapsed() {
		return collapsed;
	}

	/**
	 * @param collapsed the collapsed to set
	 */
	public TableColumn setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
		return this;
	}

	/**
	 * @return the displayClass
	 */
	public Class<?> getDisplayClass() {
		return displayClass;
	}

	/**
	 * @param displayClass the displayClass to set
	 */
	public TableColumn setDisplayClass(Class<?> displayClass) {
		this.displayClass = displayClass;
		return this;
	
	
	}
	
	/**
	 * @return the displayClass
	 */
	public Class<?> getEditorClass() {
		return editorClass;
	}

	/**
	 * @param displayClass the displayClass to set
	 */
	public TableColumn setEditorClass(Class<?> editorClass) {
		this.editorClass = editorClass;
		return this;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public TableColumn setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	/**
	 * @return the exandRatio
	 */
	public float getExpandRatio() {
		return expandRatio;
	}

	/**
	 * @param exandRatio the exandRatio to set
	 */
	public TableColumn setExpandRatio(float expandRatio) {
		this.expandRatio = expandRatio;
		return this;
	}

	/**
	 * @return the styles
	 */
	public String getStyles() {
		return styles;
	}

	/**
	 * @param styles the styles to set
	 */
	public TableColumn setStyles(String styles) {
		this.styles = styles;
		return this;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required the required to set
	 */
	public TableColumn setRequired(boolean required) {
		this.required = required;
		return this;
	}

	/**
	 * @return the alwaysEditable
	 */
	public boolean isAlwaysEditable() {
		return alwaysEditable;
	}

	/**
	 * @param alwaysEditable the alwaysEditable to set
	 */
	public TableColumn setAlwaysEditable(boolean alwaysEditable) {
		this.alwaysEditable = alwaysEditable;
		return this;
	}
	
	public TableColumn setConverter(Converter<String,?> converter) {
		this.converter = converter;
		return this;
	}
	
	public Converter<String,?> getConverter() {
		return converter;
	}
	
	public TableColumn setConstructorParameter(String constructorParameter) {
		System.err.println("ConstructorParameter = " + constructorParameter);
		this.constructorParameter = constructorParameter;
		return this;
	}
	
	public String getConstructorParameter() {
		return constructorParameter;
	}

}
