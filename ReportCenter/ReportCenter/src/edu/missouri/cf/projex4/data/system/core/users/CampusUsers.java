/**
 * 
 */
package edu.missouri.cf.projex4.data.system.core.users;

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
public class CampusUsers extends OracleQuery {

	static Logger logger = LoggerFactory.getLogger(CampusUsers.class);

	public CampusUsers() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
		setQueryString("select * from campususers");
		setRowQueryString("select * from campususers where id = ?");
		setPrimaryKeyColumns("ID");
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		logger.debug("storeRow");
		int retval = 0;

		try (CallableStatement call = conn.prepareCall("{ ? = call core.campususer(?,?,?,?) }")) {
			call.registerOutParameter(1, Types.VARCHAR);

			setString(call, 2, getId(row));
			setString(call, 3, getRowStamp(row));
			setString(call, 4, getString(row, "CAMPUSID"));
			setString(call, 5, getString(row, "USERID"));
			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

}
