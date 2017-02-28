/**
 * 
 */
package edu.missouri.cf.projex4.data.system.core;

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
import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.data.OracleString;
import edu.missouri.operations.reportcenter.Pools;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class Campuses extends OracleQuery {

	static final transient Logger logger = LoggerFactory.getLogger(Campuses.class);

	public Campuses() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
		setQueryString("select * from campuses");
		setRowQueryString("select * from campuses where id = ?");
		setPrimaryKeyColumns("ID");
	}

	protected static CallableStatement getCallableStatement(Connection conn, Item row) throws SQLException {

		CallableStatement call = conn.prepareCall("{ ? = call core.campus(?,?,?,?,?) }");
		call.registerOutParameter(1, Types.VARCHAR);

		setString(call, 2, getId(row));
		setString(call, 3, getRowStamp(row));
		setString(call, 4, getOracleString(row, "CAMPUS"));
		setString(call, 5, getOracleString(row, "DESCRIPTION"));
		setBoolean(call, 6, getOracleBoolean(row, "ISACTIVE"));

		System.err.println(call.toString());
		return call;
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {

		int retval = 0;
		try (CallableStatement call = conn.prepareCall("{ ? = call core.campus(?,?,?,?,?) }")) {

			call.registerOutParameter(1, Types.VARCHAR);

			setString(call, 2, getId(row));
			setString(call, 3, getRowStamp(row));
			setString(call, 4, getOracleString(row, "CAMPUS"));
			setString(call, 5, getOracleString(row, "DESCRIPTION"));
			setBoolean(call, 6, getOracleBoolean(row, "ISACTIVE"));

			retval = call.executeUpdate();
			setLastId(call.getString(1));
		}

		return retval;
	}

	protected static PreparedStatement getPreparedStatementForCampusId(Connection conn, String campusName) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("select id, campus from campuses where campus = ?");
		stmt.setString(1, campusName);
		return stmt;
	}

	static ArrayList<Campus> campuses;

	public static Collection<Campus> getCampuses() {

		if (campuses == null) {
			campuses = new ArrayList<Campus>();

			Connection conn = null;
			try {

				conn = Pools.getConnection(Pools.Names.PROJEX);
				try (PreparedStatement stmt = conn.prepareStatement("select id, campus from campuses")) {

					try (ResultSet rs = stmt.executeQuery()) {

						while (rs.next()) {

							Campus c = new Campus();
							c.setId(new OracleString(rs.getString("ID")));
							c.setCampusName(rs.getString("CAMPUS"));
							campuses.add(c);

						}

					}

				}

			} catch (SQLException sqle) {
				logger.error("Unable to get Campuses ", sqle);
			} finally {
				Pools.releaseConnection(Pools.Names.PROJEX, conn);
			}

		}

		return campuses;

	}

	public static String getCampus(String campusId) {
		
		if (campuses == null ) {
			campuses = (ArrayList<Campus>) getCampuses();
		}

		for (Campus c : campuses) {
			System.err.println("c.getId() = " + c.getId());
			if (c.getId().toString().equals(campusId)) {
				return c.getCampusName().toString();
			}
		}

		return null;

	}

	public static String getCampusId(String campusName) {

		if (campuses == null) {
			campuses = (ArrayList<Campus>) getCampuses();
		}

		for (Campus c : campuses) {
			if (c.getCampusName().toString().equals(campusName)) {
				return c.getId().toString();
			}
		}

		return null;
	}

}
