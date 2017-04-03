/**
 * 
 */
package edu.missouri.operations.reportcenter.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.reportcenter.Pools;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class ListItems extends OracleQuery {

	static final transient Logger logger = LoggerFactory.getLogger(ListItems.class);

	public ListItems() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from listitems");
		setRowQueryString("select * from listitems where id = ?");
		setPrimaryKeyColumns("ID");
	}
	
	public void setListID(String listId) {
		removeMandatoryFilters();
		setMandatoryFilters(new Compare.Equal("LISTID",listId));
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		int retval = 0;
		try (CallableStatement call = conn.prepareCall("{ ? = call core.listitem(?,?,?,?,?,?,?,?,?,?) }")) {

			call.registerOutParameter(1, Types.VARCHAR);

			setString(call, 2, getId(row));
			setString(call, 3, getRowStamp(row));
			setString(call, 4, getOracleString(row, "LISTID"));
			setBoolean(call, 6, getOracleBoolean(row, "ISALLCAMPUS"));
			setString(call, 4, getOracleString(row, "CAMPUSID"));
			setBigDecimal(call, 4, getDecimal(row, "DISPLAYORDER"));
			setString(call, 5, getOracleString(row, "SYSTEMVALUE"));
			setString(call, 5, getOracleString(row, "VALUE"));
			setBoolean(call, 6, getOracleBoolean(row, "ISDEFAULT"));
			setString(call, 5, getOracleString(row, "DESCRIPTION"));

			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

}
