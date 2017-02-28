/**
 * 
 */
package edu.missouri.cf.projex4.data.system.core.securitygroups;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.reportcenter.Pools;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class SecurityGroupUsersView extends OracleQuery {
	
	static Logger logger = LoggerFactory.getLogger(SecurityGroupUsersView.class);

	public SecurityGroupUsersView() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
		setQueryString("select * from securitygroupuserdetails");
		setRowQueryString("select * from securitygroupuserdetails where id = ?");
		setPrimaryKeyColumns("ID");
	}
	
	public void setUserId(String userId) {
		removeMandatoryFilters();
		setMandatoryFilters(new Compare.Equal("USERID", userId));
	}	
	
	protected static CallableStatement getCallableStatement(Connection conn, Item row ) throws SQLException {
		
		CallableStatement call = conn.prepareCall("{ ? = call SECURITY.SECURITYGROUPUSER(?,?,?,?) }");
		call.registerOutParameter(1, Types.VARCHAR);
		
		setString(call, 2, getId(row));
		setString(call, 3, getRowStamp(row));
		setString(call, 4, getOracleString(row,"SECURITYGROUPID"));
		setString(call, 5, getOracleString(row,"USERID"));
		
		return call;
	}
	
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {
		
		logger.debug("storeRow SecurityGroupUsersView");
		int retval = 0;

		try (CallableStatement call = getCallableStatement(conn, row)) {
			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}
	
}
