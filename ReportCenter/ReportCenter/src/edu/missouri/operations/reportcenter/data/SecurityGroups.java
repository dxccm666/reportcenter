/**
 * 
 */
package edu.missouri.operations.reportcenter.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.data.OracleString;
import edu.missouri.operations.data.OracleTimestamp;
import edu.missouri.operations.reportcenter.Pools;

/**
 * @author graumannc
 *
 */
@SuppressWarnings({ "serial", "unused" })
public class SecurityGroups extends OracleQuery {

	static Logger logger = LoggerFactory.getLogger(SecurityGroups.class);

	public SecurityGroups() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from securitygroups");
		setRowQueryString("select * from securitygroups where id = ?");
		setPrimaryKeyColumns("ID");
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		logger.debug("storeRow");
		int retval = 0;

		try (CallableStatement call = conn.prepareCall("{ ? = call security.securitygroup(?,?,?,?,?,?) }")) {
			call.registerOutParameter(1, Types.VARCHAR);

			setString(call, 2, getId(row));
			setString(call, 3, getRowStamp(row));
			setString(call, 4, getString(row, "SECURITYGROUPNAME"));
			setString(call, 5, getString(row, "DESCRIPTION"));
			setBoolean(call, 6, getOracleBoolean(row, "ISACTIVE"));
			setBoolean(call, 7, getOracleTimestamp(row, "MODIFIED"));
			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

}
