/**
 * 
 */
package edu.missouri.cf.projex4.data.system.core;

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
public class CronTasks extends OracleQuery {

	static final transient Logger logger = LoggerFactory.getLogger(CronTasks.class);

	/**
	 * @param connectionPool
	 */
	public CronTasks() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
		setQueryString("select * from crontasks");
		setRowQueryString("select * from crontasks where id = ?");
		setPrimaryKeyColumns("ID");
	}

	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		logger.debug("storeRow");
		int retval = 0;

		try (CallableStatement call = conn.prepareCall("{ ? = call core.crontask(?,?,?,?,?,?) }")) {
			call.registerOutParameter(1, Types.VARCHAR);

			setString(call, 2, getId(row));
			setString(call, 3, getRowStamp(row));
			setString(call, 4, getString(row, "JAVACLASS"));
			setString(call, 5, getString(row, "DESCRIPTION"));
			setString(call, 6, getString(row, "CRONEXPRESSION"));
			setBoolean(call, 7, getOracleBoolean(row, "ISACTIVE"));

			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

}
