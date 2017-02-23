package edu.missouri.operations.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unboundid.ldap.sdk.SearchResultEntry;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.Page;
import com.vaadin.ui.UI;

import edu.missouri.operations.data.OracleHelper;
import edu.missouri.operations.data.OracleTimestamp;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.system.core.users.UserLoginHistory;

@SuppressWarnings("serial")
public class User extends HashMap<User.UserAttribute, String> {

	private static transient final Logger logger = LoggerFactory.getLogger(User.class);

	public static User getUser() {
		if (UI.getCurrent() != null && UI.getCurrent().getSession() != null) {
			return UI.getCurrent().getSession().getAttribute(User.class);
		} else {
			return null;
		}
	}

	public static void setUser(User value) {
		UI.getCurrent().getSession().setAttribute(User.class, value);
	}

	public enum UserAttribute {
		USERID, LOGINNAME, DISPLAYNAME, FULLNAME, SORTNAME, EMAILADDRESS, GENDER, WORKPHONENUMBER, CELLPHONENUMBER, WORKADDRESS1, WORKADDRESS2, WORKADDRESS3, CITY, STATE, COUNTRY, POSTALCODE, TIMEZONE, PEOPLESOFTID, AUTHPROVIDERID, AUTHPROVIDERLOGIN, INVITATIONEMAIL;
	}

	public enum UserType {
		SYSTEM, INTERNAL, ENTERPRISE, EXTERNAL, FACILITIES;
	}

	private boolean newUser = true;
	private UserType userType;
	private String verifiedID;
	private String verifiedBy;

	public void setValuesFromDummy(String id, String provider, ResultSet rs) throws SQLException {

		setVerifiedID(id);
		setUserType(UserType.ENTERPRISE);
		setVerifiedBy(provider);

		put(UserAttribute.USERID, rs.getString("ID"));
		put(UserAttribute.DISPLAYNAME, rs.getString("USERLOGIN"));
		put(UserAttribute.FULLNAME, rs.getString("USERLOGIN"));

	}

	public void setValuesFromPeopleSoft(String id, String provider, ResultSet rs) throws SQLException {

		setVerifiedID(id);
		setUserType(UserType.ENTERPRISE);
		setVerifiedBy(provider);

		String name = rs.getString("NAME");
		String lastname = name.substring(0, name.indexOf(","));
		String firstname = name.substring(name.indexOf(",") + 1);
		String displayname = firstname + " " + lastname;

		logger.debug("PeopleSoft display name = {}", displayname);

		put(UserAttribute.PEOPLESOFTID, rs.getString("EMPLID"));
		put(UserAttribute.DISPLAYNAME, displayname);
		put(UserAttribute.FULLNAME, displayname);
		put(UserAttribute.SORTNAME, name);
		put(UserAttribute.EMAILADDRESS, rs.getString("EMAILID"));
		// put(UserAttribute.GENDER,rs.getString());
		put(UserAttribute.WORKPHONENUMBER, rs.getString("WORK_PHONE").replaceAll("[^\\d]", ""));
		put(UserAttribute.WORKADDRESS1, rs.getString("WORK_ADDRESS1"));
		put(UserAttribute.WORKADDRESS2, rs.getString("WORK_ADDRESS2"));
		put(UserAttribute.WORKADDRESS3, rs.getString("WORK_ADDRESS3"));
		put(UserAttribute.CITY, rs.getString("WORK_CITY"));
		put(UserAttribute.STATE, rs.getString("WORK_STATE"));
		put(UserAttribute.POSTALCODE, rs.getString("WORK_POSTAL"));
		// put(UserAttribute.COUNTRY, rs.getString());
		// put(UserAttribute.TIMEZONE, rs.getString());

	}

	public void setValuesFromInternalDB(String id, String provider, ResultSet rs) throws SQLException {

		setVerifiedID(id);
		setUserType(UserType.INTERNAL);
		/* System Users do not have PERSON Records */
		setUserType(UserType.SYSTEM);
		setVerifiedBy(provider);

	}

	public void setValuesFromLDAP(String id, String provider, SearchResultEntry ldap) {

		setVerifiedID(id);
		setUserType(UserType.EXTERNAL);
		setVerifiedBy(provider);

		/*
		 * UserAttribute.GENDER does not exist in LDAP
		 * UserAttribute.WORKADDRESS2 does not exist in LDAP
		 * UserAttribute.WORKADDRESS3 does not exist in LDAP
		 */

		put(UserAttribute.LOGINNAME, ldap.getAttributeValue("sAMAccountName"));
		put(UserAttribute.DISPLAYNAME, ldap.getAttributeValue("displayNamePrintable"));
		put(UserAttribute.FULLNAME, ldap.getAttributeValue("displayNamePrintable"));
		put(UserAttribute.SORTNAME, ldap.getAttributeValue("CN"));
		put(UserAttribute.EMAILADDRESS, ldap.getAttributeValue("mail"));
		put(UserAttribute.WORKPHONENUMBER, ldap.getAttributeValue("telephoneNumber"));
		put(UserAttribute.WORKADDRESS1, ldap.getAttributeValue("streetAddress"));
		put(UserAttribute.CITY, ldap.getAttributeValue("l"));
		put(UserAttribute.STATE, ldap.getAttributeValue("ST"));
		put(UserAttribute.POSTALCODE, ldap.getAttributeValue("postalCode"));

	}

	public void setValuesFromDatabase(String id, String provider, ResultSet rs) throws SQLException {

		setVerifiedID(id);
		setVerifiedBy(provider);
		put(UserAttribute.USERID, rs.getString("ID"));

		setUserType(UserType.valueOf(rs.getString("USERTYPE")));

		put(UserAttribute.DISPLAYNAME, rs.getString("DISPLAYNAME"));
		put(UserAttribute.FULLNAME, rs.getString("FULLNAME"));
		put(UserAttribute.SORTNAME, rs.getString("SORTNAME"));
		put(UserAttribute.EMAILADDRESS, rs.getString("EMAILADDRESS"));
		put(UserAttribute.INVITATIONEMAIL, rs.getString("INVITATIONEMAIL"));

	}

	public void setValuesFromDatabaseForPersonId(String id, ResultSet rs) throws SQLException {

		setVerifiedID(id);
		put(UserAttribute.USERID, rs.getString("USERID"));
		setUserType(UserType.valueOf(rs.getString("USERTYPE")));
		put(UserAttribute.DISPLAYNAME, rs.getString("DISPLAYNAME"));
		put(UserAttribute.FULLNAME, rs.getString("FULLNAME"));
		put(UserAttribute.SORTNAME, rs.getString("SORTNAME"));
		put(UserAttribute.EMAILADDRESS, rs.getString("EMAILADDRESS"));
		put(UserAttribute.INVITATIONEMAIL, rs.getString("INVITATIONEMAIL"));

	}

	private String sessionId;

	public User() {
		super();
		setSessionId();
	}

	public String getSessionId() {
		return sessionId;
	}

	private void setSessionId() {
		Random r = new Random();
		sessionId = Long.toString(r.nextLong(), 36).replace('-', '0');
	}

	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getVerifiedID() {
		return verifiedID;
	}

	public void setVerifiedID(String verifiedID) {
		this.verifiedID = verifiedID;
	}

	public String getVerifiedBy() {
		return verifiedBy;
	}

	public void setVerifiedBy(String verifiedBy) {
		this.verifiedBy = verifiedBy;
	}

	public String getUserId() {
		return get(UserAttribute.USERID);
	}

	public Locale getUserLocale() {
		// TODO Eventually use value from users/person table;
		return Locale.ENGLISH;
	}

	public Locale getUserCountryLocale() {

		// To properly format numbers and dates.
		// Distinguished from C10N supported locales

		return Locale.US;
	}

	HashMap<String, Collection<Filter>> lastFilters = new HashMap<String, Collection<Filter>>();

	public void setLastFilters(Collection<Filter> filters) {
		lastFilters.put(Page.getCurrent().getLocation().toString(), filters);
	}

	public Collection<Filter> getLastFilters() {
		return lastFilters.get(Page.getCurrent().getLocation().toString());
	}

	HashMap<String, Object> lastSelectedItems = new HashMap<String, Object>();

	public void setLastSelectedItem(Object object) {
		lastSelectedItems.put(Page.getCurrent().getLocation().toString(), object);
	}

	public Object getLastSelectedItem() {
		return lastSelectedItems.get(Page.getCurrent().getLocation().toString());
	}

	public static boolean overrideSecurity = false;

	private static boolean useCanDoCache = false;

	static class CanDoCache extends HashMap<String, Boolean> {

		public CanDoCache() {
			super();
		}

	}

	public static void cacheCanDo(String userId, String applicationName, String objectId, String right, Boolean allow) {

		if (UI.getCurrent() != null && UI.getCurrent().getSession() != null) {

			CanDoCache cache = UI.getCurrent().getSession().getAttribute(CanDoCache.class);
			if (cache == null) {
				cache = new CanDoCache();
				UI.getCurrent().getSession().setAttribute(CanDoCache.class, cache);
			}
			cache.put(userId + "_" + applicationName + "_" + objectId + "_" + right, allow);
		}

	}

	public static Boolean cachedCanDo(String userId, String applicationName, String objectId, String right) {

		if (UI.getCurrent() != null && UI.getCurrent().getSession() != null) {

			CanDoCache cache = UI.getCurrent().getSession().getAttribute(CanDoCache.class);
			if (cache != null) {
				return cache.get(userId + "_" + applicationName + "_" + objectId + "_" + right);
			}

		}

		return null;
	}

	public static HashMap<Enum<?>, Boolean> canAccess(ArrayList<Enum<?>> applications, String objectId) {

		String userId = User.getUser().getUserId();

		HashMap<Enum<?>, Boolean> readResults = new HashMap<Enum<?>, Boolean>();

		if (overrideSecurity || User.UserType.FACILITIES == User.getUser().getUserType()) {

			for (Enum<?> application : applications) {
				readResults.put(application, Boolean.TRUE);
			}

			return readResults;
		}

		try (Connection conn = Pools.getConnection(Pools.Names.PROJEX)) {

			try (CallableStatement canDoCall = conn.prepareCall("{ ? = call ROLEBASEDSECURITY.CANDO(?,?,?,?) } ")) {

				canDoCall.registerOutParameter(1, java.sql.Types.INTEGER);
				OracleTimestamp start = OracleTimestamp.now();
				for (Enum<?> application : applications) {

					boolean needSQL = true;

					if (useCanDoCache) {
						Boolean canDo = cachedCanDo(userId, application.name(), objectId, "READ");
						if (canDo != null) {
							readResults.put(application, canDo);
							needSQL = false;
						}
					}

					if (needSQL) {

						OracleHelper.setString(canDoCall, 2, userId);
						OracleHelper.setString(canDoCall, 3, application.name());
						OracleHelper.setString(canDoCall, 4, objectId);
						OracleHelper.setString(canDoCall, 5, "READ");
						canDoCall.execute();
						int retval = canDoCall.getInt(1);

						if (logger.isDebugEnabled()) {
							logger.debug("rolebasedsecurity.cando({},{},{},{}) return {}", userId, application.name(), objectId, "READ", retval);
						}

						readResults.put(application, new Boolean(retval == 1 || retval == 3));

						if (useCanDoCache) {
							cacheCanDo(userId, application.name(), objectId, "READ", new Boolean(retval == 1 || retval == 3));
						}
					}

				}

				OracleTimestamp end = OracleTimestamp.now();
				if (logger.isDebugEnabled()) {
					logger.debug("rolebasedsecurity.canRead() took {} ms", end.getTime() - start.getTime());
				}

			}

		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Unable to execute read function", e);
			}
		}

		return readResults;

	}

	public static void preComputeAccessRights(final Connection conn, final String userId, final String objectId, final String... applicationNames) {

		OracleTimestamp start = OracleTimestamp.now();

		try (PreparedStatement stmt = conn.prepareStatement("select right from applicationrights a inner join applications b on b.id = a.applicationid where b.name = ? and a.right = 'READ' ");
				CallableStatement canDoCall = conn.prepareCall("{ ? = call ROLEBASEDSECURITY.CANDO(?,?,?,?) } ")) {

			for (String applicationName : applicationNames) {

				stmt.setString(1, applicationName);
				canDoCall.registerOutParameter(1, java.sql.Types.INTEGER);

				try (ResultSet rs = stmt.executeQuery()) {

					while (rs.next()) {

						OracleHelper.setString(canDoCall, 2, userId);
						OracleHelper.setString(canDoCall, 3, applicationName);
						OracleHelper.setString(canDoCall, 4, objectId);
						String right = rs.getString("RIGHT");
						OracleHelper.setString(canDoCall, 5, right);
						canDoCall.execute();
						int retval = canDoCall.getInt(1);

						if (useCanDoCache) {
							cacheCanDo(userId, applicationName, objectId, right, new Boolean(retval == 1 || retval == 3));
						}

					}

				}
			}

		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("preCompute Access rights failed", e);
			}
		}

		OracleTimestamp end = OracleTimestamp.now();
		if (logger.isDebugEnabled()) {
			logger.debug("User.preComputeAccessRights('{}','{}','{}') took {} ms", userId, objectId, applicationNames, end.getTime() - start.getTime());
		}

	}

	public static void preComputeAccessRights(final String userId, final ArrayList<String> applications, final ArrayList<String> objectIds) {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				java.util.Date start = new java.util.Date();

				try (Connection conn = Pools.getConnection(Pools.Names.PROJEX)) {

					try (CallableStatement canDoCall = conn.prepareCall("{ ? = call ROLEBASEDSECURITY.CANDO(?,?,?,?) } ")) {

						canDoCall.registerOutParameter(1, java.sql.Types.INTEGER);

						for (String application : applications) {

							for (String objectId : objectIds) {

								Boolean canDo = cachedCanDo(userId, application, objectId, "READ");
								if (canDo != null) {

									OracleHelper.setString(canDoCall, 2, userId);
									OracleHelper.setString(canDoCall, 3, application);
									OracleHelper.setString(canDoCall, 4, objectId);
									OracleHelper.setString(canDoCall, 5, "READ");
									canDoCall.execute();
									int retval = canDoCall.getInt(1);

									if (logger.isDebugEnabled()) {
										logger.debug("precompute of rolebasedsecurity.cando({},{},{},{}) return {}", userId, application, objectId, "READ", retval);
									}

									cacheCanDo(userId, application, objectId, "READ", new Boolean(retval == 1 || retval == 3));
								} else {
									if (logger.isDebugEnabled()) {
										logger.debug("cachedCanDo({},{},{},{}) was already in cache", userId, application, objectId, "READ");
									}
								}
							}
						}
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				if (logger.isDebugEnabled()) {
					logger.debug("User.preComputeAccessRights('{}','{}','{}') took {} ms", userId, applications, objectIds, new java.util.Date().getTime() - start.getTime());
				}

			}

		};

		if (useCanDoCache) {

			new Thread(runnable).start();
		}

	}

	public static void preComputeRights(final String userId, final String applicationName, final String objectId) {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				OracleTimestamp start = OracleTimestamp.now();
				
				Connection conn =null;

				try {
					
					conn = Pools.getConnection(Pools.Names.PROJEX);

					try (PreparedStatement stmt = conn.prepareStatement("select right from applicationrights a inner join applications b on b.id = a.applicationid where b.name = ?");
							CallableStatement canDoCall = conn.prepareCall("{ ? = call ROLEBASEDSECURITY.CANDO(?,?,?,?) } ")) {

						stmt.setString(1, applicationName);
						canDoCall.registerOutParameter(1, java.sql.Types.INTEGER);

						try (ResultSet rs = stmt.executeQuery()) {

							while (rs.next()) {

								String right = rs.getString("RIGHT");

								OracleHelper.setString(canDoCall, 2, userId);
								OracleHelper.setString(canDoCall, 3, applicationName);
								OracleHelper.setString(canDoCall, 4, objectId);
								OracleHelper.setString(canDoCall, 5, right);
								
								if(logger.isDebugEnabled()) {
									logger.debug("Prechecking rights ?,?,?,?", userId, applicationName, objectId, right);
								}
								
								canDoCall.execute();
								int retval = canDoCall.getInt(1);

								if (useCanDoCache) {
									cacheCanDo(userId, applicationName, objectId, right, new Boolean(retval == 1 || retval == 3));
								}

							}

						}

					}

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Pools.releaseConnection(Pools.Names.PROJEX, conn);
				}

				OracleTimestamp end = OracleTimestamp.now();
				if (logger.isDebugEnabled()) {
					logger.debug("User.preComputeRights('{}','{}','{}') took {} ms", userId, applicationName, objectId, end.getTime() - start.getTime());
				}

			}

		};

		new Thread(runnable).start();

	}

	/*
	 * Once all references have been changed, undeprecate and change scope to
	 * protected
	 */
	@Deprecated
	public static boolean canDo(String userId, String applicationName, String objectId, String right) {

		if (overrideSecurity) {
			return true;
		}

		if ("READ".equals(right)) {
			switch (User.getUser().getUserType()) {
			case FACILITIES:
				return true;
			default:
				break;
			}
		}

		try {

			if (right.matches("CHANGESTATUS")) {
				throw new Exception("Potentially Obsolete Changestatus check");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (useCanDoCache) {
			Boolean canDo = cachedCanDo(userId, applicationName, objectId, right);
			if (canDo != null) {
				return canDo.booleanValue();
			}
		}

		try (Connection conn = Pools.getConnection(Pools.Names.PROJEX)) {

			try (CallableStatement canDoCall = conn.prepareCall("{ ? = call ROLEBASEDSECURITY.CANDO(?,?,?,?) } ")) {

				int i = 1;
				canDoCall.registerOutParameter(i++, java.sql.Types.INTEGER);
				OracleHelper.setString(canDoCall, i++, userId);
				OracleHelper.setString(canDoCall, i++, applicationName);
				OracleHelper.setString(canDoCall, i++, objectId);
				OracleHelper.setString(canDoCall, i++, right);
				OracleTimestamp start = OracleTimestamp.now();
				canDoCall.execute();
				int retval = canDoCall.getInt(1);

				OracleTimestamp end = OracleTimestamp.now();

				if (logger.isDebugEnabled()) {
					logger.debug("rolebasedsecurity.canDo('{}','{}','{}','{}') returned {} took {} ms", userId, applicationName, objectId, right, retval, end.getTime() - start.getTime());
				}

				System.err.println("rolebasedsecurity.canDo('" + userId + "','" + applicationName + "','" + objectId + "','" + right + "') returned " + retval + " took " + (end.getTime() - start
						.getTime()) + " ms");

				if (useCanDoCache) {
					cacheCanDo(userId, applicationName, objectId, right, new Boolean(retval == 1 || retval == 3));
				}
				return (retval == 1 || retval == 3);
			}

		} catch (SQLRecoverableException sqlre) {

			if (logger.isErrorEnabled()) {
				logger.error("Recoverable Exception", sqlre);
			}

			// TODO This is temporary fix for security query failure.
			return (User.getUser().getUserType() == User.UserType.FACILITIES);

		} catch (SQLException sqle) {

			System.err.println("Error calling permission statement");
			sqle.printStackTrace();

		}

		return false;

	}

	public static boolean canDo(ProjexViewProvider.Views view, String right) {

		if ("READ".equals(right)) {
			switch (User.getUser().getUserType()) {
			case FACILITIES:
				return true;
			default:
				return canDo(User.getUser().getUserId(), view.name().toUpperCase(), null, right);
			}

		} else {
			return canDo(User.getUser().getUserId(), view.name().toUpperCase(), null, right);
		}
	}

	public static boolean canDo(ProjexViewProvider.Views view, String objectId, String right) {

		if ("READ".equals(right)) {

			switch (User.getUser().getUserType()) {
			case FACILITIES:
				return true;
			default:
				return canDo(User.getUser().getUserId(), view.name().toUpperCase(), objectId, right);
			}

		} else {
			return canDo(User.getUser().getUserId(), view.name().toUpperCase(), objectId, right);
		}
	}

	public static boolean canAccess(ProjexViewProvider.Views view) {
		String applicationName = view.name().toUpperCase();
		String right = "READ";

		switch (User.getUser().getUserType()) {
		case FACILITIES:
			return true;
		default:
			return canDo(User.getUser().getUserId(), applicationName, null, right);
		}

	}

	public static boolean canAccess(ProjexViewProvider.Views view, String objectId) {
		String applicationName = view.name().toUpperCase();
		String right = "READ";
		switch (User.getUser().getUserType()) {
		case FACILITIES:
			return true;
		default:
			return canDo(User.getUser().getUserId(), applicationName, objectId, right);
		}
	}

	enum ContractParticipantType {
		MAIN, SUBORDINATE, BLANKET
	}

	private static boolean checkContractorRole(String userId, String objectId, boolean primary, ContractParticipantType participantType, boolean projectLevel) {

		String subconsultantrole = "34-35 15 14";

		String sql = null;

		if (projectLevel) {

			// TODO May need to rethink. Should contractors on the primary
			// contract have special rights.

			switch (participantType) {
			case BLANKET:
				break;
			default:
			case MAIN:
				sql = "select 1 from contracts a inner join participantpersonsmview b on b.refobjectid = a.id " + "inner join projects c on c.id = a.refobjectid "
						+ "where b.userid = ? and c.id = ? and a.isprimary = ? and not b.classification = ?";
				break;
			case SUBORDINATE:
				sql = "select 1 from contracts a inner join participantpersonsmview b on b.refobjectid = a.id " + "inner join projects c on c.id = a.refobjectid "
						+ "where b.userid = ? and c.id = ? and a.isprimary = ? and b.classification = ?";
				break;

			}

			try (Connection conn = Pools.getConnection(Pools.Names.PROJEX)) {

				try (PreparedStatement stmt = conn.prepareStatement(sql)) {

					stmt.setString(1, userId);
					stmt.setString(2, objectId);
					stmt.setInt(3, (primary) ? 1 : 0);
					stmt.setString(4, subconsultantrole);

					try (ResultSet rs = stmt.executeQuery()) {
						return rs.next();
					}
				}

			} catch (SQLException sqle) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not execute consultant security cheat", sqle);
				}

			}

		} else {

			switch (participantType) {
			case BLANKET:
				break;
			default:
			case MAIN:
				sql = "select 1 from contracts a inner join participantpersonsmview b on b.refobjectid = a.id " + "inner join contractobjecthierarchy c on c.refobjectid = a.id "
						+ "where b.userid = ? and c.id = ? and not b.classification = ?";
				break;
			case SUBORDINATE:
				sql = "select 1 from contracts a inner join participantpersonsmview b on b.refobjectid = a.id " + "inner join contractobjecthierarchy c on c.refobjectid = a.id "
						+ "where b.userid = ? and c.id = ? and b.classification = ?";
				break;

			}

			try (Connection conn = Pools.getConnection(Pools.Names.PROJEX)) {

				try (PreparedStatement stmt = conn.prepareStatement(sql)) {

					stmt.setString(1, userId);
					stmt.setString(2, objectId);
					stmt.setString(3, subconsultantrole);

					try (ResultSet rs = stmt.executeQuery()) {
						return rs.next();
					}
				}

			} catch (SQLException sqle) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not execute consultant security cheat", sqle);
				}

			}

		}

		return false;

	}

	private static boolean checkConsultantRole(String userId, String objectId, boolean primary, ContractParticipantType participantType, boolean projectLevel) {

		String subconsultantrole = "34-55 14 12";

		String sql = null;

		if (projectLevel) {
			
			try (Connection conn = Pools.getConnection(Pools.Names.PROJEX)) { // This code will not work with SimpleJDBCConnectionPool only HikariPool.

				switch (participantType) {
				case BLANKET:
					
					// We don't currently care if a blanket consultant is set as a consultant or subconsultant role.
					
					sql = "select 1 from agreements a inner join participantpersonsmview b on b.refobjectid = a.id " 
							+ "inner join workauthorizations w on w.refobjectid = a.id "
							+ "inner join pcsprojectrow p on p.refobjectid = w.id "
							+ "where b.userid = ? and p.pcsprojectid = ? " ;
					
					try (PreparedStatement stmt = conn.prepareStatement(sql)) {
						stmt.setString(1, userId);
						stmt.setString(2,objectId);
						
						try (ResultSet rs = stmt.executeQuery()) {
							return rs.next();
						}
					}

				default:
				case MAIN:
					sql = "select 1 from agreements a inner join participantpersonsmview b on b.refobjectid = a.id " 
							+ "inner join agreementprojects c on c.id = a.id " + "where b.userid = ? and "
							+ "(c.refobjectid = ? or c.altrefobjectid1 = ? or c.altrefobjectid2 = ?) " 
							+ "and a.isprimary = ? and not b.classification = ?";
					
					try (PreparedStatement stmt = conn.prepareStatement(sql)) {

						stmt.setString(1, userId);
						stmt.setString(2, objectId);
						stmt.setString(3, objectId);
						stmt.setString(4, objectId);
						stmt.setInt(5, (primary) ? 1 : 0);
						stmt.setString(6, subconsultantrole);

						try (ResultSet rs = stmt.executeQuery()) {
							return rs.next();
						}
					}
					

				case SUBORDINATE:
					
					sql = "select 1 from agreements a inner join participantpersonsmview b on b.refobjectid = a.id " + "inner join agreementprojects c on c.id = a.id " + "where b.userid = ? and "
							+ "(c.refobjectid = ? or c.altrefobjectid1 = ? or c.altrefobjectid2 = ?) " + " and a.isprimary = ? and b.classification = ?";
					try (PreparedStatement stmt = conn.prepareStatement(sql)) {

						stmt.setString(1, userId);
						stmt.setString(2, objectId);
						stmt.setString(3, objectId);
						stmt.setString(4, objectId);
						stmt.setInt(5, (primary) ? 1 : 0);
						stmt.setString(6, subconsultantrole);

						try (ResultSet rs = stmt.executeQuery()) {
							return rs.next();
						}
					}
					
				}

			} catch (SQLException sqle) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not execute consultant security cheat", sqle);
				}

			}

		} else {

			switch (participantType) {
			case BLANKET:
				return false;

			default:
			case MAIN:
				sql = "select 1 from agreements a inner join participantpersonsmview b on b.refobjectid = a.id " + "inner join agreementobjecthierarchy c on c.refobjectid = a.id "
						+ "where b.userid = ? and c.id = ? and not b.classification = ?";
				break;
			case SUBORDINATE:
				sql = "select 1 from agreements a inner join participantpersonsmview b on b.refobjectid = a.id " + "inner join agreementobjecthierarchy c on c.refobjectid = a.id "
						+ "where b.userid = ? and c.id = ? and b.classification = ?";

				break;

			}

			try (Connection conn = Pools.getConnection(Pools.Names.PROJEX)) {

				try (PreparedStatement stmt = conn.prepareStatement(sql)) {

					stmt.setString(1, userId);
					stmt.setString(2, objectId);
					stmt.setString(3, subconsultantrole);

					try (ResultSet rs = stmt.executeQuery()) {
						return rs.next();
					}
				}

			} catch (SQLException sqle) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not execute consultant security cheat", sqle);
				}

			}

		}

		return false;

	}

	// Put in to cheat security...

	public static boolean isPrimaryConsultantOnAgreement(String objectId) {
		return checkConsultantRole(getUser().getUserId(), objectId, true, ContractParticipantType.MAIN, false);
	}

	public static boolean isPrimarySubconsultantOnAgreement(String objectId) {
		return checkConsultantRole(getUser().getUserId(), objectId, true, ContractParticipantType.SUBORDINATE, false);
	}

	public static boolean isSecondaryConsultantOnAgreement(String objectId) {
		return checkConsultantRole(getUser().getUserId(), objectId, false, ContractParticipantType.MAIN, false);
	}

	public static boolean isSecondarySubconsultantOnAgreement(String objectId) {
		return checkConsultantRole(getUser().getUserId(), objectId, false, ContractParticipantType.SUBORDINATE, false);
	}

	public static boolean isPrimaryConsultantOnProject(String objectId) {
		return checkConsultantRole(getUser().getUserId(), objectId, true, ContractParticipantType.MAIN, true);
	}

	public static boolean isPrimarySubconsultantOnProject(String objectId) {
		return checkConsultantRole(getUser().getUserId(), objectId, true, ContractParticipantType.SUBORDINATE, true);
	}

	public static boolean isSecondaryConsultantOnProject(String objectId) {
		return checkConsultantRole(getUser().getUserId(), objectId, false, ContractParticipantType.MAIN, true);
	}

	public static boolean isSecondarySubconsultantOnProject(String objectId) {
		return checkConsultantRole(getUser().getUserId(), objectId, false, ContractParticipantType.SUBORDINATE, true);
	}

	public static boolean isBlanketConsultantOnProject(String objectId) {
		return checkConsultantRole(getUser().getUserId(), objectId, true, ContractParticipantType.BLANKET, true);
	}

	public static boolean isPrimaryContractorOnContract(String objectId) {
		return checkContractorRole(getUser().getUserId(), objectId, true, ContractParticipantType.MAIN, false);
	}

	public static boolean isPrimarySubcontractorOnContract(String objectId) {
		return checkContractorRole(getUser().getUserId(), objectId, true, ContractParticipantType.SUBORDINATE, false);
	}

	public static boolean isSecondaryContractorOnContract(String objectId) {
		return checkContractorRole(getUser().getUserId(), objectId, false, ContractParticipantType.MAIN, false);
	}

	public static boolean isSecondarySubcontractorOnContract(String objectId) {
		return checkContractorRole(getUser().getUserId(), objectId, false, ContractParticipantType.SUBORDINATE, false);
	}

	public static boolean isPrimaryContractorOnProject(String objectId) {
		return checkContractorRole(getUser().getUserId(), objectId, true, ContractParticipantType.MAIN, true);
	}

	public static boolean isPrimarySubcontractorOnProject(String objectId) {
		return checkContractorRole(getUser().getUserId(), objectId, true, ContractParticipantType.SUBORDINATE, true);
	}

	public static boolean isSecondaryContractorOnProject(String objectId) {
		return checkContractorRole(getUser().getUserId(), objectId, false, ContractParticipantType.MAIN, true);
	}

	public static boolean isSecondarySubcontractorOnProject(String objectId) {
		return checkContractorRole(getUser().getUserId(), objectId, false, ContractParticipantType.SUBORDINATE, true);
	}

	public void registerLogin() {

		if (logger.isDebugEnabled()) {
			logger.debug("registerLogin");
		}

		Item item = new PropertysetItem();
		item.addItemProperty("USERID", new ObjectProperty<String>(getUserId()));
		item.addItemProperty("LOGGEDIN", new ObjectProperty<OracleTimestamp>(OracleTimestamp.now()));
		item.addItemProperty("IPADDRESS", new ObjectProperty<String>(UI.getCurrent().getPage().getWebBrowser().getAddress()));

		UserLoginHistory.storeItem(item);

	}

}
