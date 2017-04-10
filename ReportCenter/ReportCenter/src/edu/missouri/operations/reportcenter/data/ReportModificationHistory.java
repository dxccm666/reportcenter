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
public class ReportModificationHistory extends OracleQuery {

	static final transient Logger logger = LoggerFactory.getLogger(ReportModificationHistory.class);

	public ReportModificationHistory() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from reportmodificationhistory");
		setRowQueryString("select * from reportmodificationhistory where id = ?");
		setPrimaryKeyColumns("ID");
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		int retval = 0;
		try (CallableStatement call = conn.prepareCall("{ ? = call reports.reportmodificationhistory(?,?,?"
				+ "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?) }")) {
			
			int i=1;

			call.registerOutParameter(i++, Types.VARCHAR);
			
			setString(call, i++, getId(row));
			setString(call, i++, getOracleString(row, "REPORTID"));
			setString(call, i++, getOracleString(row, "FILENAME"));
			setTimestamp(call, i++, getOracleTimestamp(row,"REQUESTED"));
			setString(call, i++, getOracleString(row, "REQUESTEDBY"));
			
			setString(call, i++, getOracleString(row, "REQUESTERBUSINESSUNIT"));
			setString(call, i++, getOracleString(row, "REQUESTERBUSINESSUNITNAME"));
			setString(call, i++, getOracleString(row, "REQUESTERSUPERDIVISION"));
			setString(call, i++, getOracleString(row, "REQUESTERSUPERDIVISIONNAME"));
			setString(call, i++, getOracleString(row, "REQUESTERDIVISION"));
			
			setString(call, i++, getOracleString(row, "REQUESTERDIVISIONNAME"));
			setString(call, i++, getOracleString(row, "REQUESTERDEPARTMENT"));
			setString(call, i++, getOracleString(row, "REQUESTERDEPARTMENTNAME"));
			setString(call, i++, getOracleString(row, "REQUESTERSUBDEPARTMENT"));
			setString(call, i++, getOracleString(row, "REQUESTERSUBDEPARTMENTNAME"));
			
			setString(call, i++, getOracleString(row, "REQUESTERDEPTID"));
			setString(call, i++, getOracleString(row, "REQUESTERDEPTIDNAME"));
			setString(call, i++, getOracleString(row, "REASON"));
			setTimestamp(call, i++, getOracleTimestamp(row,"REGISTERED"));
			setString(call, i++, getOracleString(row, "REGISTEREDBY"));
			
			setString(call, i++, getOracleString(row, "CATEGORY"));
			setString(call, i++, getOracleString(row, "TOOL"));
			setString(call, i++, getOracleString(row, "TOOLVERSION"));
			
			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

}
