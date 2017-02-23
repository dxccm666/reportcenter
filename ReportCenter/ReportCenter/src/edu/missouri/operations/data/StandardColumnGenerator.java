package edu.missouri.operations.data;

import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

/**
 * TODO 
 * May need to implement this for new Oracle Classes.
 * @author graumannc
 *
 */

@SuppressWarnings("serial")
public class StandardColumnGenerator implements ColumnGenerator {

	public StandardColumnGenerator() {
		
	}
	
	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		/* 
		Field field = createField(getColumnType(columnId));
		Property prop = source.getItem(itemId).getItemProperty(columnId);
		field.setValue(prop.getValue());
		field.setReadOnly(true);
		return field;
		*/
		return null;
	}

}
