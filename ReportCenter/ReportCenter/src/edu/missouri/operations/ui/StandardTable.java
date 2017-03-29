package edu.missouri.operations.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tepi.filtertable.FilterTable;
import c10n.C10N;

import com.vaadin.data.Container;
import com.vaadin.server.Page;

import edu.missouri.cf.projex4.ui.c10n.CommonText;
import edu.missouri.cf.projex4.ui.common.OracleFieldFactory;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.TableColumns;
import edu.missouri.operations.data.User;
import edu.missouri.operations.reportcenter.Pools;
import edu.missouri.operations.ui.desktop.filtertable.modulargenerator.StandardCellStyleGenerator;
import edu.missouri.operations.ui.desktop.filtertable.modulargenerator.StandardFilterFieldGenerator;

@SuppressWarnings("serial")
public class StandardTable extends FilterTable {

	TableColumns columns;

	protected final static transient Logger logger = LoggerFactory.getLogger(StandardTable.class);

	private boolean autoSelectFirstItem;

	private String tableName;

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public StandardTable() {
		init(true);
	}

	public StandardTable(String caption) {
		super(caption);
		init(true);
	}

	public StandardTable(boolean useFilterFieldGenerator) {
		init(useFilterFieldGenerator);
	}

	public void resetColumns() {
		columns.clear();
	}

	public void selectLastSelectedItems() {

		Object lastItem = User.getUser().getLastSelectedItem();
		if (lastItem != null) {

			if (isMultiSelect()) {

				for (Object item : ((Collection<?>) lastItem)) {
					select(item);
				}

			} else {
				select(lastItem);
			}

		}

	}

	private void init(boolean useFilterFieldGenerator) {

		if (useFilterFieldGenerator) {
			setFilterFieldGenerator(new StandardFilterFieldGenerator(this));
			setCellStyleGenerator(new StandardCellStyleGenerator());
		}
		setTableFieldFactory(new OracleFieldFactory());
		setImmediate(true);
		setFilterBarVisible(true);
		setSelectable(true);
		setMultiSelect(true);
		addStyleName("projectlisting_table");
		setSizeFull();
		setColumnCollapsingAllowed(true);
		setColumnReorderingAllowed(true);
		columns = new TableColumns();
		autoSelectFirstItem = true;

	}

	@Override
	public void setColumnCollapsed(Object propertyId, boolean collapsed) throws IllegalStateException {
		super.setColumnCollapsed(propertyId, collapsed);
	}

	class ColumnSettings {

		String columnName;
		String columnHeader;
		boolean columnCollapsed;
		int columnWidth;
		float columnExpandRatio;

	}

	public void setColumnSettings() {

		if (tableName != null) {

			Connection conn = null;
			try {

				conn = Pools.getConnection(Pools.Names.REPORTCENTER);

				try (PreparedStatement stmt = conn.prepareStatement("select * from usertablesettings where userid = ? and pageurl = ? and tablename= ? order by ordernumber")) {

					stmt.setString(1, User.getUser().getUserId());
					stmt.setString(2, Page.getCurrent().getLocation().toString());
					stmt.setString(3, tableName);
					try (ResultSet rs = stmt.executeQuery()) {

						Object[] currentColumns = getVisibleColumns();
						Object[] currentColumnHeaders = getColumnHeaders();
						ArrayList<ColumnSettings> columns = new ArrayList<ColumnSettings>();

						while (rs.next()) {

							ColumnSettings setting = new ColumnSettings();

							setting.columnName = rs.getString("COLUMNNAME");
							logger.debug("Reading in values for Column {}", setting.columnName);

							int x = 0;
							for (Object o : currentColumns) {
								if (setting.columnName.equals(o.toString())) {
									setting.columnHeader = currentColumnHeaders[x].toString();
									break;
								}
								x++;
							}

							setting.columnCollapsed = (rs.getInt("ISCOLLAPSED") == 1);
							setting.columnWidth = rs.getInt("COLUMNWIDTH");
							setting.columnExpandRatio = rs.getFloat("COLUMNEXPANDRATIO");

							columns.add(setting);
						}

						if (columns.size() > 0) {

							ArrayList<String> visibleColumns = new ArrayList<String>();
							ArrayList<String> columnHeaders = new ArrayList<String>();

							for (ColumnSettings setting : columns) {
								visibleColumns.add(setting.columnName);
								columnHeaders.add(setting.columnHeader);
							}

							setVisibleColumns(visibleColumns.toArray());
							setColumnHeaders(columnHeaders.toArray(new String[columnHeaders.size()]));

							for (ColumnSettings setting : columns) {
								setColumnCollapsed(setting.columnName, setting.columnCollapsed);
								if (setting.columnWidth != -1) {
									setColumnWidth(setting.columnName, setting.columnWidth);
								}
								if (setting.columnExpandRatio != -1.0f) {
									setColumnExpandRatio(setting.columnName, setting.columnExpandRatio);
								}
							}

						}

					}

				}

			} catch (SQLException sqle) {
				if (logger.isErrorEnabled()) {
					logger.error("Could not execute sql", sqle);
				}
			} finally {
				Pools.releaseConnection(Pools.Names.REPORTCENTER, conn);
			}

		}

	}
	
	public void deleteColumnSettings() {
		
		if (tableName != null) {

			Connection conn = null;
			try {
				conn = Pools.getConnection(Pools.Names.REPORTCENTER);

				try (PreparedStatement stmt = conn.prepareStatement("delete from usertablesettings where userid = ? and pageurl = ? and tablename = ?")) {
					stmt.setString(1, User.getUser().getUserId());
					stmt.setString(2, Page.getCurrent().getLocation().toString());
					stmt.setString(3, tableName);
					stmt.executeUpdate();
				}

				conn.commit();

			} catch (SQLException sqle) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not execute SQLStatement", sqle);
				}

			} finally {
				Pools.releaseConnection(Pools.Names.REPORTCENTER, conn);
			}
			
			columns.configureTable(this);

		}

	}

	public void saveColumnSettings() {

		if (tableName != null) {

			Connection conn = null;
			try {
				conn = Pools.getConnection(Pools.Names.REPORTCENTER);

				try (PreparedStatement stmt = conn.prepareStatement("delete from usertablesettings where userid = ? and pageurl = ? and tablename = ?")) {
					stmt.setString(1, User.getUser().getUserId());
					stmt.setString(2, Page.getCurrent().getLocation().toString());
					stmt.setString(3, tableName);
					stmt.executeUpdate();
				}

				try (PreparedStatement stmt = conn.prepareStatement(
						"insert into usertablesettings (id, userid, pageurl, tablename, ordernumber, columnname, iscollapsed, columnwidth, columnexpandratio ) values (usertablesettingseq.nextval,?,?,?,?,?,?,?,?)")) {

					int x = 1;

					for (Object column : getVisibleColumns()) {

						if (logger.isDebugEnabled()) {
							logger.debug("saving column {}", column);
						}

						stmt.setString(1, User.getUser().getUserId());
						stmt.setString(2, Page.getCurrent().getLocation().toString());
						stmt.setString(3, tableName);
						stmt.setInt(4, x++);
						stmt.setString(5, column.toString());
						stmt.setInt(6, (isColumnCollapsed(column) ? 1 : 0));
						stmt.setInt(7, getColumnWidth(column));
						stmt.setFloat(8, getColumnExpandRatio(column));
						stmt.addBatch();
					}

					stmt.executeBatch();

				}

				conn.commit();

			} catch (SQLException sqle) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not execute SQLStatement", sqle);
				}

			} finally {
				Pools.releaseConnection(Pools.Names.REPORTCENTER, conn);
			}

		}

	}

	public void add(TableColumn col) {
		columns.add(col);
	}

	public TableColumn get(String dbName) {
		return columns.get(dbName);
	}

	public void configure() {

		if (logger.isDebugEnabled()) {
			logger.debug("Call to StandardTable configure made - consider using configure(String tableName) instead to enable user defined settings.");
		}

		columns.configureTable(this);
		setColumnSettings();
	}

	public void configure(String tableName) {
		columns.configureTable(this);
		setTableName(tableName);
		setColumnSettings();
	}

	public TableColumns getTableColumns() {
		return columns;
	}

	/**
	 * Autoselection only works on initial table load, not filtering.
	 */

	/**
	 * @return the autoSelectFirstItem
	 */
	public boolean isAutoSelectFirstItem() {
		return autoSelectFirstItem;
	}

	/**
	 * @param autoSelectFirstItem
	 *            the autoSelectFirstItem to set
	 */
	public void setAutoSelectFirstItem(boolean autoSelectFirstItem) {
		this.autoSelectFirstItem = autoSelectFirstItem;
	}

	@Override
	public void setContainerDataSource(Container newDataSource) {

		if (newDataSource != null) {

			super.setContainerDataSource(newDataSource);

			if (autoSelectFirstItem && newDataSource.size() == 1 && newDataSource.getItemIds() != null && !newDataSource.getItemIds().isEmpty()) {
				select(newDataSource.getItemIds().iterator().next());
			}
		}

	}

}
