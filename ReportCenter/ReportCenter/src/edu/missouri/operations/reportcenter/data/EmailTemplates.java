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
public class EmailTemplates extends OracleQuery {

	static final transient Logger logger = LoggerFactory.getLogger(EmailTemplates.class);

	public EmailTemplates() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from emailtemplates");
		setRowQueryString("select * from emailtemplates where id = ?");
		setPrimaryKeyColumns("ID");
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		int retval = 0;
		try (CallableStatement call = conn.prepareCall("{ ? = call email.emailtemplate(?,?,?,?,?,?,?,?,?,?,?) }")) {
			
			int i=1;

			call.registerOutParameter(i++, Types.VARCHAR);
			
			setString(call, i++, getId(row));
			setString(call, i++, getRowStamp(row));
			setString(call, i++, getString(row, "EMAILNAME"));
			setString(call, i++, getString(row, "EMAILSUBJECT"));
			setString(call, i++, getString(row, "CONTENT"));
			setString(call, i++, getString(row, "HELPTEXT"));
			setBoolean(call, i++, getOracleBoolean(row, "ISACTIVE"));
			setString(call, i++, getString(row, "CREATEDBY"));
			setTimestamp(call, i++, getOracleTimestamp(row, "CREATED"));
			setString(call, i++, getString(row, "MODIFIEDBY"));
			setTimestamp(call, i++, getOracleTimestamp(row, "MODIFIED"));

			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

}
