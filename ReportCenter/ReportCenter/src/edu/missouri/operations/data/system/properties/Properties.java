/**
 * 
 */
package edu.missouri.operations.data.system.properties;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.data.OracleString;

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

	protected static CallableStatement getCallableStatement(Connection conn, String id, String rowstamp, OracleString refidtype,
			OracleString refid, OracleString property, OracleString value) throws SQLException {

		CallableStatement call = conn.prepareCall("{ ? = call coreproperty.property(?,?,?,?,?,?) }");
		call.registerOutParameter(1, Types.VARCHAR);
		setString(call, 2, id);
		setString(call, 3, rowstamp);
		setString(call, 4, refidtype);
		setString(call, 5, refid);
		setString(call, 6, property);
		setString(call, 7, value);
		
		return call;

	}
	
	protected static CallableStatement getCallableStatement(Connection conn, Item row) throws SQLException {
		
		return getCallableStatement(conn, getId(row), getRowStamp(row), getOracleString(row,"REFIDTYPE"),
				getOracleString(row,"REFID"), getOracleString(row,"PROPERTY"), getOracleString(row,"VALUE") );
		
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		logger.debug("storeRow");
		int retval = 0;

		try (CallableStatement call = getCallableStatement(conn, row )) {
			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}catch (SQLException sqle) {
			logger.error("storeRow", sqle);
		}

		return retval;
	}
}
