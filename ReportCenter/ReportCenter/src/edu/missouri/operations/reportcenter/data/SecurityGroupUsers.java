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
public class SecurityGroupUsers extends OracleQuery {

	static Logger logger = LoggerFactory.getLogger(SecurityGroupUsers.class);

	public SecurityGroupUsers() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from securitygroupuserlisting");
		setRowQueryString("select * from securitygroupuserlisting where id = ?");
		setPrimaryKeyColumns("ID");
	}

	public void setSecurityGroupId(String id) {
		removeMandatoryFilters();
		setMandatoryFilters(new Compare.Equal("SECURITYGROUPID", id));
	}
	
	public void setUserId(String id) {
		removeMandatoryFilters();
		setMandatoryFilters(new Compare.Equal("USERID", id));
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		logger.debug("storeRow");
		int retval = 0;

		try (CallableStatement call = conn.prepareCall("{ ? = call security.securitygroupuser(?,?,?,?,?) }")) {
			call.registerOutParameter(1, Types.VARCHAR);

			setString(call, 2, getId(row));
			setString(call, 3, getRowStamp(row));
			setString(call, 4, getString(row, "SECURITYGROUPID"));
			setString(call, 5, getString(row, "USERID"));
			setTimestamp(call, 5, getOracleTimestamp(row, "CREATED"));

			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}
		return retval;
	}

	public static boolean memberOf(String securitygroup, String userId) {

		Connection conn = null;

		try {
			conn = Pools.getConnection(Pools.Names.REPORTCENTER);

			try (PreparedStatement stmt = conn.prepareStatement(
					"select count(*) from securitygroupuserlisting s where s.securitygroupname = ? and s.userid = ? ")) {
				setString(stmt, 1, securitygroup);
				setString(stmt, 2, userId);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						int x = rs.getInt(1);
						return x > 0;
					}
				}
			}

		} catch (SQLException sqle) {
			logger.error("Error getting group membership", sqle);
		} finally {
			Pools.releaseConnection(Pools.Names.REPORTCENTER, conn);
		}
		return false;

	}

}
