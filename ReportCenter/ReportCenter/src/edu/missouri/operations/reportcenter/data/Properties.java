/**
 * 
 */
package edu.missouri.operations.reportcenter.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class Properties extends OracleQuery {

	public Properties(JDBCConnectionPool connectionPool) {
		super(connectionPool);
		setRowQueryString("select * from properties where id = ?");
		setPrimaryKeyColumns("ID");
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		logger.debug("storeRow");
		int retval = 0;

		try (CallableStatement call = conn.prepareCall("{ ? = call coreproperty.property(?,?,?,?,?,?) }")) {
			
			call.registerOutParameter(1, Types.VARCHAR);
			setString(call, 2, getId(row));
			setString(call, 3, getRowStamp(row));
			setString(call, 4, getString(row,"REFIDTYPE"));
			setString(call, 5, getString(row,"REFID"));
			setString(call, 6, getString(row,"PROPERTY"));
			setString(call, 7, getString(row,"VALUE"));
			
			retval = call.executeUpdate();
			setLastId(call.getString(1));
			
		}catch (SQLException sqle) {
			logger.error("storeRow", sqle);
		}

		return retval;
	}
}
