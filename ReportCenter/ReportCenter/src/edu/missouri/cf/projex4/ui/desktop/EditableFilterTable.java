package edu.missouri.cf.projex4.ui.desktop;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.data.OracleCurrency;
import edu.missouri.cf.projex4.ui.desktop.filtertable.modulargenerator.ProjexCellStyleGenerator;

@SuppressWarnings("serial")
public class EditableFilterTable extends BasicFilterTable {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private boolean editing;
	private Object editingItemId;
	private Object editingPropertyId;
	private List<String> editableFields;

	public EditableFilterTable(String caption) {
		super(caption);
		init();
	}
	
	public EditableFilterTable() {
		super();
		init();
	}
	
	private void init() {
		
		setEditableFields(new ArrayList<String>());
		
		setTableFieldFactory(editableTableFieldFactory);
		
		setFilterDecorator(new ProjexFilterDecorator());
		setFilterGenerator(new ProjexFilterGenerator());
		
		addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				logger.debug("button = {}, double Click = {}", new Object[]{event.getButton(), event.isDoubleClick()});
				if (event.getButton().equals(MouseButton.LEFT) && !event.isDoubleClick()) {
					editing = false;
					if (getValue() != null && event.getItemId() != null) {
						if (getValue().equals(event.getItemId())) {
							editingItemId = event.getItemId();
							editing = true;
						}
					}
					logger.debug("setEditable({})", editing);
					setEditable(editing);
					
				} else {
					setEditable(false);
				}
			}
		});
		
		setCellStyleGenerator(new ProjexCellStyleGenerator() {
			@Override
			public String getAdditionalStyles(Table source, Object itemId, Object propertyId) {
				// add styles here for icons and text. then add the style in the table cell's in projex.scss
				// add styles here. set the name of the image that you want to use based on this:
				// background-image: url('icons/chalkwork/basic/#{$image}_16x16.png');
				// then call @include icon(confirm); where confirm is the image name
				if (source.getContainerProperty(itemId, propertyId).getType() == OracleBoolean.class) {
					if (propertyId.equals("ISPRIMARY")) {
						if (source.getContainerProperty(itemId, propertyId).getValue() != null) {
							if (((OracleBoolean) source.getContainerProperty(itemId, propertyId).getValue()).toBoolean()) {
								return "confirm";
							} else {
								return "notext";
							}
						}
					}
				}
				return null;
			}
		});
		
		/*
		this.addGeneratedColumn("ISPRIMARY", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Label label = new Label();
				if (source.getContainerProperty(itemId, columnId).getValue() != null) {
					if (((OracleBoolean) source.getContainerProperty(itemId, columnId).getValue()).toBoolean()) {
						label.addStyleName("confirm");
						label.setValue("true");
						return label;
					} 
				}
				return null;
			}
			
		});
		*/
		
	}
	
	/**
	 * Set a line in a table to editable. 
	 * @param editingItemId - Row to edit.
	 * @param editingPropertyId - Property to focus.
	 */
	public void setEditable(Object editingItemId, Object editingPropertyId) {
		editing = true;
		this.editingItemId = editingItemId;
		this.editingPropertyId = editingPropertyId;
		setEditable(true);
	}
	
	TableFieldFactory editableTableFieldFactory = new TableFieldFactory() {
		@Override
		public Field<?> createField(Container container, final Object itemId, final Object propertyId, Component uiContext) {

			final TextField field = new TextField();
			field.setWidth("100%");
			field.setNullRepresentation("");
			field.setImmediate(true);
			
			field.setData(itemId);
			
			if (propertyId != null) {
				if (getContainerProperty(itemId, propertyId).getType() == OracleCurrency.class) {
					field.addStyleName("numerical");
				}
				/*
				if (getContainerProperty(itemId, propertyId).getType() == OracleBoolean.class) {
					if (propertyId.equals("ISPRIMARY")) {
						IconField cf = new IconField(new ThemeResource("icons/chalkwork/basic/confirm_16x16.png"));
						return cf;
					}
				}
				*/
			}
			
			if (editingItemId != null) {
				if ((editingItemId.equals(itemId))) {
					field.addStyleName("bold");
					if (editing) {
						if (!editableFields.isEmpty() && editableFields.contains(propertyId)) {
							if (editingPropertyId != null) {
								if (editingPropertyId.equals(propertyId)) {
									field.focus();
								}
							}
							return field;
						}
					}
				}
			}
			field.setReadOnly(true);
			return null;

		}
	};
	
	public List<String> getEditableFields() {
		return editableFields;
	}

	public void setEditableFields(List<String> editableFields) {
		this.editableFields = editableFields;
	}
	
}
