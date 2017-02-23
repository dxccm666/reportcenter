/**
 * 
 */
package edu.missouri.operations.ui.common;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Extension;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Label;

/**
 * This wraps a label in a CustomField for binding purposes
 * 
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class LabelField extends CustomField<String> {
	
	Label label = new Label();

	/* (non-Javadoc)
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		label.setContentMode(ContentMode.HTML);
		return label;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<? extends String> getType() {
		return String.class;
	}
	
	@Override
	public void setPropertyDataSource(@SuppressWarnings("rawtypes") Property newDataSource) {
		if(label==null) {
			System.err.println("Label is null"); 
		} else {
            label.setPropertyDataSource(newDataSource);
		}
	}
	
	@Override 
	@SuppressWarnings("rawtypes")
	public Property getPropertyDataSource() {
		return label.getPropertyDataSource();
	}
	
	@Override
	public void addStyleName(String style) {
		label.addStyleName(style);
	}
	
	@Override
	public void setValue(String s) {
		label.setValue(s);
	}
	
	@Override
	public String getValue() {
		return label.getValue();
	}

	/**
	 * @return
	 * @see com.vaadin.ui.AbstractComponent#getStyleName()
	 */
	public String getStyleName() {
		return label.getStyleName();
	}

	/**
	 * @param style
	 * @see com.vaadin.ui.AbstractComponent#setStyleName(java.lang.String)
	 */
	public void setStyleName(String style) {
		label.setStyleName(style);
	}

	/**
	 * @param style
	 * @see com.vaadin.ui.AbstractComponent#removeStyleName(java.lang.String)
	 */
	public void removeStyleName(String style) {
		label.removeStyleName(style);
	}

	/**
	 * @return
	 * @see com.vaadin.ui.Label#getContentMode()
	 */
	public ContentMode getContentMode() {
		return label.getContentMode();
	}

	/**
	 * @param contentMode
	 * @see com.vaadin.ui.Label#setContentMode(com.vaadin.shared.ui.label.ContentMode)
	 */
	public void setContentMode(ContentMode contentMode) {
		label.setContentMode(contentMode);
	}

	/**
	 * @return
	 * @see com.vaadin.ui.AbstractComponent#getIcon()
	 */
	public Resource getIcon() {
		return label.getIcon();
	}

	/**
	 * @param listener
	 * @see com.vaadin.ui.Label#addValueChangeListener(com.vaadin.data.Property.ValueChangeListener)
	 */
	public void addValueChangeListener(com.vaadin.data.Property.ValueChangeListener listener) {
		label.addValueChangeListener(listener);
	}

	/**
	 * @param listener
	 * @see com.vaadin.ui.Label#removeValueChangeListener(com.vaadin.data.Property.ValueChangeListener)
	 */
	public void removeValueChangeListener(com.vaadin.data.Property.ValueChangeListener listener) {
		label.removeValueChangeListener(listener);
	}

	/**
	 * @return
	 * @see com.vaadin.ui.AbstractComponent#getDescription()
	 */
	public String getDescription() {
		return label.getDescription();
	}

	/**
	 * @return
	 * @see com.vaadin.ui.Label#getConverter()
	 */
	public Converter<String, Object> getConverter() {
		return label.getConverter();
	}

	/**
	 * @param converter
	 * @see com.vaadin.ui.Label#setConverter(com.vaadin.data.util.converter.Converter)
	 */
	public void setConverter(Converter<String, ?> converter) {
		label.setConverter(converter);
	}

	/**
	 * @param extension
	 * @see com.vaadin.server.AbstractClientConnector#removeExtension(com.vaadin.server.Extension)
	 */
	public void removeExtension(Extension extension) {
		label.removeExtension(extension);
	}

	/**
	 * @return
	 * @see com.vaadin.ui.AbstractComponent#getData()
	 */
	public Object getData() {
		return label.getData();
	}

	/**
	 * @param shortcut
	 * @see com.vaadin.ui.AbstractComponent#addShortcutListener(com.vaadin.event.ShortcutListener)
	 */
	public void addShortcutListener(ShortcutListener shortcut) {
		label.addShortcutListener(shortcut);
	}

	/**
	 * @param shortcut
	 * @see com.vaadin.ui.AbstractComponent#removeShortcutListener(com.vaadin.event.ShortcutListener)
	 */
	public void removeShortcutListener(ShortcutListener shortcut) {
		label.removeShortcutListener(shortcut);
	}
}
