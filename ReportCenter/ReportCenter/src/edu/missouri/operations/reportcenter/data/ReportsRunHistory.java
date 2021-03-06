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
public class ReportsRunHistory extends OracleQuery {

	static final transient Logger logger = LoggerFactory.getLogger(ReportsRunHistory.class);

	public ReportsRunHistory() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from reportrunhistory");
		setRowQueryString("select * from reportrunhistory where id = ?");
		setPrimaryKeyColumns("ID");
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		int retval = 0;
		try (CallableStatement call = conn.prepareCall("{ ? = call reports.reportrunhistory(?,?,?,?,?,?) }")) {
			
			int i=1;

			call.registerOutParameter(i++, Types.VARCHAR);
			
			setString(call, i++, getId(row));
			setString(call, i++, getOracleString(row,"USERID"));
			setString(call, i++, getOracleString(row,"REPORTID"));
			setString(call, i++, getOracleString(row,"FILEFORMAT"));
			setTimestamp(call, i++, getOracleTimestamp(row,"RANON"));
			setString(call, i++, getOracleString(row, "FILELOCATION"));
			
			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

}
