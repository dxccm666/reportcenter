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
import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.reportcenter.Pools;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class Lists extends OracleQuery {

	static final transient Logger logger = LoggerFactory.getLogger(Lists.class);

	public Lists() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from lists");
		setRowQueryString("select * from lists where id = ?");
		setPrimaryKeyColumns("ID");
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		int retval = 0;
		try (CallableStatement call = conn.prepareCall("{ ? = call core.list(?,?,?,?,?,?,?) }")) {

			call.registerOutParameter(1, Types.VARCHAR);

			setString(call, 2, getId(row));
			setString(call, 3, getRowStamp(row));
			setString(call, 4, getOracleString(row, "LISTNAME"));
			setString(call, 5, getOracleString(row, "DESCRIPTION"));
			setString(call, 5, getOracleString(row, "HELPTEXT"));
			setBoolean(call, 6, getOracleBoolean(row, "ISACTIVE"));
			setBoolean(call, 6, getOracleBoolean(row, "ISSYSTEM"));

			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

}
