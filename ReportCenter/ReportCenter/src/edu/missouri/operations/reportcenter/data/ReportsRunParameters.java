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
public class ReportsRunParameters extends OracleQuery {

	static final transient Logger logger = LoggerFactory.getLogger(ReportsRunParameters.class);

	public ReportsRunParameters() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from reportrunparameters");
		setRowQueryString("select * from reportrunparameters where id = ?");
		setPrimaryKeyColumns("ID");
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		int retval = 0;
		try (CallableStatement call = conn.prepareCall("{ ? = call reports.reportrunparameter(?,?,?,?,?,?) }")) {
			
			int i=1;

			call.registerOutParameter(i++, Types.VARCHAR);
			
			setString(call, i++, getId(row));
			setString(call, i++, getOracleString(row,"REPORTRUNHISTORYID"));
			setBigDecimal(call, i++, getDecimal(row,"PARAMETERNUMBER"));
			setString(call, i++, getOracleString(row,"PARAMETERNAME"));
			setString(call, i++, getOracleString(row, "PARAMETERVALUE"));
			
			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

}
