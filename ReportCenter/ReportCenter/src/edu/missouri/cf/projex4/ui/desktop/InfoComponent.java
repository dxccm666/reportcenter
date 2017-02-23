package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.CustomLayout;

@SuppressWarnings("serial")
public abstract class InfoComponent extends CustomLayout {
	
	public enum FIELD {
		UUID, ID
	}
	
	Item data;
	
	public InfoComponent(String template) {
		super(template);
		addStyleName(template);
	}
	
	public abstract void setItemDataSource(FIELD field, String value);
	public abstract Item getItemDataSource(FIELD field, String value);
	
	/**
	 * 
	 * Set the item data source to a previously constructed data source.
	 * 
	 * @param item
	 * 
	 */
	public void setItemDataSource(Item item) {
		data = item;
		FieldGroup f = new FieldGroup(data);
		f.bindMemberFields(this);
	}
	
	public Item getItemDataSource() {
		return data;
	}
	
	
	public Object getValue(String propertyId) {
		
		if(data!=null && data.getItemProperty(propertyId)!=null) {
			return data.getItemProperty(propertyId).getValue();
		}
		
		return null;
	}
	
	protected String objectName;
	
	public String getObjectName() {
		return objectName;
	}

}
