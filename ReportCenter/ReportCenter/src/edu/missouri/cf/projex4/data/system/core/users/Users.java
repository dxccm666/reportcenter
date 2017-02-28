/**
 * 
 */
package edu.missouri.cf.projex4.data.system.core.users;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class Users extends OracleQuery {

	static Logger logger = LoggerFactory.getLogger(Users.class);

	public Users() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
		setQueryString("select * from userdetails");
		setRowQueryString("select * from userdetails where id = ?");
		setPrimaryKeyColumns("ID");
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		java.util.Date start = new java.util.Date();

		int retval = 0;

		try (CallableStatement call = conn.prepareCall("{ ? = call core.user(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }")) {

			int x = 1;
			call.registerOutParameter(x++, Types.VARCHAR);

			setString(call, x++, getString(row,"ID"));
			setString(call, x++, getString(row, "ROWSTAMP"));
			setString(call, x++, getString(row, "USERLOGIN"));
			setString(call, x++, getString(row, "PERSONID"));
			setString(call, x++, getString(row, "INVITATIONCODE"));
			setString(call, x++, getString(row, "INVITATIONEMAIL"));
			setTimestamp(call, x++, getOracleTimestamp(row, "INVITED"));
			setTimestamp(call, x++, getOracleTimestamp(row, "INVITATIONEMAILED"));
			setBoolean(call, x++, getOracleBoolean(row, "INITIALIZED"));
			setString(call, x++, getString(row, "PASSWORD"));
			setTimestamp(call, x++, getOracleTimestamp(row, "PASSWORDEXPIRATION"));
			setBoolean(call, x++, getOracleBoolean(row, "FORCEEXPIRATION"));
			setString(call, x++, getString(row, "USERTYPE"));
			setString(call, x++, getString(row, "REGISTRATIONMETHOD"));
			setBoolean(call, x++, getOracleBoolean(row, "ISACTIVE"));
			setString(call, x++, getString(row, "SECRETKEY"));
			setString(call, x++, getString(row, "SALT"));
			setString(call, x++, getString(row, "EMAILSCHEDULE"));
			setBoolean(call, x++, getOracleBoolean(row, "NOTIFYBYEMAIL"));
			setString(call, x++, getString(row, "VERIFIER"));

			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Actual User save took {} ms", new java.util.Date().getTime() - start.getTime());
		}
		return retval;
	}

	/**
	 * Called by WorkflowScriptHelper.
	 * 
	 * @param userLogin
	 * @return
	 */
	
	

	public static String getUser(String userLogin) {

		// Projex4 does not currently have a person record.

		if ("PROJEX4".equals(userLogin)) {
			return "1";
		}

		Connection conn = null;
		String userId = null;

		try {
			conn = Pools.getConnection(Pools.Names.PROJEX);

			try (PreparedStatement stmt = conn.prepareStatement(
					"SELECT U.ID FROM USERDETAILS U WHERE UPPER(U.USERLOGIN) = UPPER(?) OR UPPER(U.FULLNAME) = UPPER(?) OR UPPER(U.DISPLAYNAME) = UPPER(?) ")) {

				setString(stmt, 1, userLogin);
				setString(stmt, 2, userLogin);
				setString(stmt, 3, userLogin);

				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {
						userId = rs.getString(1);
					}

				}

			}
		} catch (SQLException sqle) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not retrieve userid from userLogin {}", userLogin, sqle);
			}
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}

		return userId;

	}

	public static String getUserID(String invitationCode) {

		// Projex4 does not currently have a person record.

		Connection conn = null;
		String userId = null;

		try {
			conn = Pools.getConnection(Pools.Names.PROJEX);

			try (PreparedStatement stmt = conn.prepareStatement("SELECT personid FROM USERS WHERE invitationcode = ?")) {

				setString(stmt, 1, invitationCode);

				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {
						userId = rs.getString(1);
					}

				}

			}
		} catch (SQLException sqle) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not retrieve userid from userLogin {}", invitationCode, sqle);
			}
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}

		return userId;

	}

}
