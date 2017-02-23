package edu.missouri.cf.projex4.ui.desktop;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.OracleContainer;

import edu.missouri.cf.data.Formatter;
import edu.missouri.cf.data.OracleTimestamp;
import edu.missouri.cf.projex4.ui.desktop.filtertable.modulargenerator.ProjexCellStyleGenerator;
import edu.missouri.cf.projex4.ui.desktop.filtertable.modulargenerator.ProjexFilterFieldGenerator;

@SuppressWarnings("serial")
public class BasicFilterTable extends StandardTable {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SimpleDateFormat dateFormat = Formatter.DATE;
	
	public BasicFilterTable() {
		super();
		init();
	}
	
	public BasicFilterTable(String caption) {
		super(caption);
		init();
	}
	
	private void init() {
		setImmediate(true);
	
		setFilterFieldGenerator(new ProjexFilterFieldGenerator(this));
		setCellStyleGenerator(new ProjexCellStyleGenerator());
		
		setFilterBarVisible(true);
		setSelectable(true);
		addStyleName("projectlisting_table");
		setSizeFull();
		setColumnCollapsingAllowed(true);
		setColumnReorderingAllowed(true);
		
		setMultiSelect(true);
		setNullSelectionAllowed(true);
		
		addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				getItemInfo(getStringValue());
			}
		});
	}
	
	

	private void getItemInfo(Object itemId) {
		if (itemId != null) {
			if (getContainerDataSource() instanceof OracleContainer) {
				logger.debug("item = {}", getItem(new RowId(new Object[]{itemId.toString()})));
			} else {
				logger.debug("item = {}", getItem(itemId));
			}
		} else {
			logger.debug("itemId is null");
		}
	}
	
	public String getStringValue() {
		String value = null;
		if (isMultiSelect()) {
			if (getValue() instanceof Collection<?>) {
				Collection<?> col = (Collection<?>) getValue();
				if (!col.isEmpty()) {
					value = col.iterator().next().toString();
				}
			}
		} else {
			if (getValue() != null) {
				value = getValue().toString();
			}					
		}
		return value;
	}
	
	public RowId getRowIdValue() {
		if (isMultiSelect()) {
			if (getValue() instanceof Collection<?>) {
				Collection<?> col = (Collection<?>) getValue();
				if (!col.isEmpty()) {
					//return new RowId(new Object[]{col.iterator().next().toString()});
					Object last = null;
					Iterator<?> it = col.iterator();
					while (it.hasNext()) {
						last = it.next();
					}
					logger.debug("getRowIdValue = {}", last);
					return new RowId(new Object[]{last.toString()});
				}
			} 
		} else {
			if (getValue() != null) {
				return new RowId(new Object[]{getValue().toString()});
			}
		}
		return null;
	}
	
	@Override
    protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
        // Format oracle timestamps
		if (property instanceof OracleTimestamp) {
			
		}
		if (colId.toString().equals("FILESIZE") && property.getValue() != null) {
			return Formatter.FILESIZE.format(((BigDecimal)property.getValue()).longValue());
		}

        return super.formatPropertyValue(rowId, colId, property);
    }

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * Default date format is {@link Formatter#DATE}
	 */
	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	
}
