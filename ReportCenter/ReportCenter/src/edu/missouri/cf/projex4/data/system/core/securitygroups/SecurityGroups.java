/**
 * 
 */
package edu.missouri.cf.projex4.data.system.core.securitygroups;

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
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.operations.reportcenter.Pools;
import edu.missouri.cf.projex4.data.system.core.SecurityGroup;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class SecurityGroups extends OracleQuery {

	static Logger logger = LoggerFactory.getLogger(SecurityGroups.class);

	public SecurityGroups() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
		setQueryString("select * from securitygroups");
		setRowQueryString("select * from securitygroups where id = ?");
		setPrimaryKeyColumns("ID");
	}

	public void setSecurityGroupId(String securityGroupId) {
		removeMandatoryFilters();
		setMandatoryFilters(new Compare.Equal("ID", securityGroupId));
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
			setBoolean(call, 7, getOracleBoolean(row, "ISSYSTEMSECURITYGROUP"));
			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

	static ArrayList<SecurityGroup> securitygroups;

	public static Collection<SecurityGroup> getSecurityGroups() {

		if (securitygroups == null) {
			securitygroups = new ArrayList<SecurityGroup>();

			Connection conn = null;
			try {

				conn = Pools.getConnection(Pools.Names.PROJEX);
				try (PreparedStatement stmt = conn.prepareStatement("select id, SECURITYGROUPNAME from securitygroups")) {

					try (ResultSet rs = stmt.executeQuery()) {

						while (rs.next()) {

							SecurityGroup c = new SecurityGroup();
							c.setId(new OracleString(rs.getString("ID")));
							c.setSecurityGroupName(rs.getString("SECURITYGROUPNAME"));
							securitygroups.add(c);

						}

					}

				}

			} catch (SQLException sqle) {
				logger.error("Unable to get Campuses ", sqle);
			} finally {
				Pools.releaseConnection(Pools.Names.PROJEX, conn);
			}

		}

		return securitygroups;

	}

	public static String getSecurityGroupID(String groupName) {

		Connection conn = null;
		String id = null;
		try {
			conn = Pools.getConnection(Pools.Names.PROJEX);

			try (PreparedStatement stmt = conn.prepareStatement("select id from securitygroups where SECURITYGROUPNAME = ?")) {
				stmt.setString(1, groupName);
				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {
						id = rs.getString("ID");
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}
		return id;

	}

	public static boolean canAccess(ProjexViewProvider.Views view) {
		
		return true;

	}

	public static boolean canDo(ProjexViewProvider.Views view, String right) {
		
		return true;

	}

	public static boolean canDo(String userId, String applicationName, String right) {
		
		return true;

	}
}
