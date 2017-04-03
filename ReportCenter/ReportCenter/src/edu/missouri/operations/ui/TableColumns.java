package edu.missouri.operations.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.TableFieldFactory;


/**
 * Class for standardized system for setting table headers, widths and column
 * types. Currently explicitly setting is the only way that this can be used,
 * but if adding a method for pulling table metadata from a data base table or
 * from a user definable configuration table should be fairly easy.
 * 
 * @see TableColumn
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class TableColumns extends LinkedHashMap<String, TableColumn> {

	private final static transient Logger logger = LoggerFactory.getLogger(TableColumns.class);
	
	public void add(TableColumn column) {
		put(column.getDbName(), column);
	}

	/**
	 * @param standardTable
	 */
	@SuppressWarnings("unchecked")
	public void configureTable(StandardTable standardTable) {

		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> columnHeaders = new ArrayList<String>();

		TableFieldFactory factory = standardTable.getTableFieldFactory();
		Container container = standardTable.getContainerDataSource();
		CellStyleGenerator generator = standardTable.getCellStyleGenerator();

		for (Map.Entry<String,TableColumn> entry : entrySet()) {
			
			TableColumn c = entry.getValue();

			if (logger.isDebugEnabled()) {
				//logger.debug("Processing column {}", c.getDbName());
			}

			if (!columns.contains(c.getDbName())) {
				columns.add(c.getDbName());
				columnHeaders.add(c.getDisplayName());
			}

			if (c.getWidth() > 0) {

				if (logger.isDebugEnabled()) {
					//logger.debug("Setting column Width : {}", c.getWidth());
				}
				standardTable.setColumnWidth(c.getDbName(), c.getWidth());
			}

			if (standardTable.isColumnCollapsingAllowed()) {
				if (logger.isDebugEnabled()) {
					//logger.debug("Setting column collapsed {}", c.isCollapsed());
				}
				standardTable.setColumnCollapsed(c.getDbName(), c.isCollapsed());
				if (c.getExpandRatio() > 0) {
					standardTable.setColumnExpandRatio(c.getDbName(), c.getExpandRatio());
				}
			}

			if (c.getConverter() != null) {
				if (logger.isDebugEnabled()) {
					//logger.debug("Applying converter {}", c.getConverter().getClass().getSimpleName());
				}
				standardTable.setConverter(c.getDbName(), c.getConverter());
			}

			if (factory instanceof OracleFieldFactory) {

				if (c.isReadOnly()) {
					if (logger.isDebugEnabled()) {
						//logger.debug("Setting readOnly");
					}
					((OracleFieldFactory) factory).setReadOnly(c.getDbName());
				}

				if (c.getEditorClass() != null) {

					((OracleFieldFactory) factory).assign(c.getDbName(), c.getEditorClass(), c.getStyles(),
							c.getConstructorParameter());

				}

				if (c.isAlwaysEditable()) {
					if (logger.isDebugEnabled()) {
						//logger.debug("Always Editable");
					}
					((OracleFieldFactory) factory).setAlwaysEditable(c.getDbName());
				}

				if (c.getConverter() != null) {
					((OracleFieldFactory) factory).assignConverter(c.getDbName(), (Class<? extends Converter<String, ?>>) c
							.getConverter().getClass());
				}

			}
			
			/*
			
			if (generator instanceof StandardCellStyleGenerator) {

				if (c.getStyles() != null) {
					((StandardCellStyleGenerator) generator).assignStyle(c.getDbName(), c.getStyles());
				}

			}
			
			*/

			if (container instanceof OracleContainer) {

				if (c.hasOverride()) {
					((OracleContainer) container).overrideType(c.getDbName(), c.getOverrideClass());
				}

			}

		}

		standardTable.setVisibleColumns(columns.toArray());
		standardTable.setColumnHeaders(columnHeaders.toArray(new String[columns.size()]));

	}

}
