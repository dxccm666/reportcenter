package edu.missouri.operations.security;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.net.ssl.SSLSocketFactory;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.LDAPSDKUsageException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import com.vaadin.ui.Notification;

import edu.missouri.operations.reportcenter.Pools;
import edu.missouri.operations.reportcenter.data.SystemProperties;
import edu.missouri.operations.data.User;
import edu.missouri.operations.data.User.UserAttribute;

public class EnterpriseAuthenticator implements Authenticator {

	final boolean enableUserSubstitution = true;

	/* Parameters for initial global catalog query */

	private String gcBindDN;

	/**
	 * @return the gcBindDN
	 */
	public String getGcBindDN() {
		return gcBindDN;
	}

	/**
	 * @param gcBindDN
	 *            the gcBindDN to set
	 */
	public void setGcBindDN(String gcBindDN) {
		this.gcBindDN = gcBindDN;
	}

	/**
	 * @return the gcPassword
	 */
	public String getGcPassword() {
		return gcPassword;
	}

	/**
	 * @param gcPassword
	 *            the gcPassword to set
	 */
	public void setGcPassword(String gcPassword) {
		this.gcPassword = gcPassword;
	}

	/**
	 * @return the gcServer
	 */
	public String getGcServer() {
		return gcServer;
	}

	/**
	 * @param gcServer
	 *            the gcServer to set
	 */
	public void setGcServer(String gcServer) {
		this.gcServer = gcServer;
	}

	private String gcPassword;
	private String gcServer;
	private int gcPort;
	private int ldapPort;

	private final static Logger logger = LoggerFactory.getLogger(EnterpriseAuthenticator.class);

	private final static boolean useSSL = false;

	public EnterpriseAuthenticator() {

		this.gcBindDN = SystemProperties.get("globalcatalog.binddn");
		this.gcPassword = SystemProperties.get("globalcatalog.password");
		this.gcServer = SystemProperties.get("globalcatalog.server");

		if (useSSL) {
			this.gcPort = 3269;
			this.ldapPort = 636;
		} else {
			this.gcPort = 3268;
			this.ldapPort = 389;
		}

	}

	private String getServer(String dn) {
		return dn.substring(dn.indexOf("DC=")).replaceAll("DC=", "").replaceAll(",", ".");
	}

	@SuppressWarnings("unused")
	public boolean newUserAuthenticate(String username, String password, User u) {

		if (null == u) {
			u = new User();
		}
		String sso;

		if (username.indexOf("@") != -1) {
			sso = username.substring(0, username.indexOf("@") - 1);
		} else if (username.indexOf("\\") != -1) {
			sso = username.substring(username.indexOf("\\") + 1).toUpperCase();
		} else {
			sso = username;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("sso = {}", sso);
		}

		LDAPConnection gcConnection = null;
		LDAPConnection connection = null;

		String dn = null;
		SearchResult searchResult = null;
		String newserver = null;

		if (gcServer == null || gcBindDN == null || gcPassword == null) {

			if (logger.isDebugEnabled()) {
				logger.debug("Unable to read parameters for connection to LDAP server");
			}

			throw new RuntimeException("Unable to read parameters for connection to LDAP server");

		}

		try {

			if (useSSL) {

				if (logger.isDebugEnabled()) {
					logger.debug("Enabled SSL Protocols = {}", SSLUtil.getEnabledSSLProtocols());
					logger.debug("Default SSL Protocols = {}", SSLUtil.getDefaultSSLProtocol());
				}

				SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
				SSLSocketFactory sslSocketFactory = sslUtil.createSSLSocketFactory();

				if (sslSocketFactory == null) {
					Notification.show("Could not connect to Campus LDAP server");
					return false;
				}

				// Establish a secure connection using the socket
				// factory.
				gcConnection = new LDAPConnection(sslSocketFactory, gcServer, gcPort, gcBindDN, gcPassword);

			} else {

				gcConnection = new LDAPConnection(gcServer, gcPort, gcBindDN, gcPassword);

			}

			if (gcConnection == null) {
				return false;
			}

			SearchResult gcSearchResult = gcConnection.search("dc=edu", SearchScope.SUB, "(sAMAccountName=" + sso + ")");

			if (gcSearchResult.getEntryCount() == 1) {

				SearchResultEntry e = gcSearchResult.getSearchEntries().get(0);
				dn = e.getDN();
				u.setValuesFromLDAP(dn, "University of Missouri", e);
				newserver = getServer(dn);

				if (logger.isDebugEnabled()) {
					logger.debug("Global catalog server says to try {} for  user {} {}", newserver, sso, dn);
				}

			}

			gcConnection.close();

			connection = null;

			if (useSSL) {

				SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
				SSLSocketFactory sslSocketFactory = sslUtil.createSSLSocketFactory();
				connection = new LDAPConnection(sslSocketFactory, gcServer, gcPort, dn, password);

			} else {
				connection = new LDAPConnection(gcServer, ldapPort, dn, password);
			}

			if (connection != null) {
				u.setVerifiedID(dn);
				u.setVerifiedBy("University of Missouri");

				u.put(UserAttribute.LOGINNAME, sso);
				u.put(UserAttribute.USERID, sso);
				System.out.println(" use information is " + u.getUserId() + "/ " + u.get(UserAttribute.USERID));
				return true;

			} else {

				return false;

			}

		} catch (LDAPException e) {

			if (logger.isErrorEnabled()) {
				logger.error("LDAP Exception occurred ", e);
			}
			return false;

		} catch (GeneralSecurityException e) {

			if (logger.isErrorEnabled()) {
				logger.error("LDAP SSL Exception occurred ", e);
			}
			return false;

		} finally {

			if (connection != null) {
				connection.close();
			}

			if (gcConnection != null) {
				gcConnection.close();
			}

		}

	}

	String sso;

	@SuppressWarnings("unused")
	public boolean authenticate(String username, String password, User u) {

		// For some reason the User is coming in as null. This may have
		// something to do with the startup routine.

		java.util.Date start = new java.util.Date();
		int i = 1;

		if (null == u) {
			u = new User();
		}

		boolean outsideUser = false;

		if (username.indexOf("@missouri.edu") != -1 || username.indexOf("@mail.missouri.edu") != -1
				|| username.indexOf("@umsystem.edu") != -1 || username.indexOf("@umkc.edu") != -1
				|| username.indexOf("@umsl.edu") != -1 || username.indexOf("@mst.edu") != -1) {
			sso = username.substring(0, username.indexOf("@") - 1);

		} else if (username.indexOf("@") != -1) {
			outsideUser = true;
			sso = username;
		} else if (username.indexOf("\\") != -1) {
			sso = username.substring(username.indexOf("\\") + 1).toUpperCase();
		} else {
			sso = username;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("sso = {}", sso);
		}

		Connection c = null;
		String dn = null;
		StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
		boolean storedPasswordMatches = false;
		boolean userFound = false;

		try {

			c = Pools.getConnection(Pools.Names.REPORTCENTER);

			try (PreparedStatement stmt = c
					.prepareStatement("select * from userdetails where upper(userlogin) = upper(?) and isactive = 1")) {

				stmt.setString(1, sso);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						userFound = true;
						storedPasswordMatches = encryptor.checkPassword(password, rs.getString("PASSWORD"));

						if (logger.isDebugEnabled()) {
							logger.debug("password matches storedPassword {}", storedPasswordMatches);
						}

					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("Could not retrieve user information");
						}
					}
				}

			}

			logger.debug("Timing {}, {} ms", i++, new java.util.Date().getTime() - start.getTime());

			if (userFound) {

				if (!storedPasswordMatches && !outsideUser) {

					LDAPConnection gcConnection = null;
					LDAPConnection connection = null;

					SearchResult searchResult = null;

					String newserver = null;

					if (gcServer == null || gcBindDN == null || gcPassword == null) {

						if (logger.isDebugEnabled()) {
							logger.debug("Unable to read parameters for connection to LDAP server");
						}

						throw new RuntimeException("Unable to read parameters for connection to LDAP server");

					}

					try {

						if (useSSL) {

							if (logger.isDebugEnabled()) {
								logger.debug("Enabled SSL Protocols = {}", SSLUtil.getEnabledSSLProtocols());
								logger.debug("Default SSL Protocols = {}", SSLUtil.getDefaultSSLProtocol());
							}

							SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
							SSLSocketFactory sslSocketFactory = sslUtil.createSSLSocketFactory();

							if (sslSocketFactory == null) {
								Notification.show("Could not connect to Campus LDAP server");
								return false;
							}

							// Establish a secure connection using the socket
							// factory.
							gcConnection = new LDAPConnection(sslSocketFactory, gcServer, gcPort, gcBindDN, gcPassword);

						} else {

							gcConnection = new LDAPConnection(gcServer, gcPort, gcBindDN, gcPassword);

						}

						if (gcConnection != null) {

							SearchResult gcSearchResult = gcConnection.search("dc=edu", SearchScope.SUB,
									"(sAMAccountName=" + sso + ")");

							if (gcSearchResult.getEntryCount() == 1) {

								SearchResultEntry e = gcSearchResult.getSearchEntries().get(0);
								dn = e.getDN();

								u.setValuesFromLDAP(dn, "University of Missouri", e);
								newserver = getServer(dn);

								if (logger.isDebugEnabled()) {
									logger.debug("Global catalog server says to try {} for  user {} {}", newserver, sso, dn);
								}

							}

							gcConnection.close();

							try {

								if (useSSL) {

									SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
									SSLSocketFactory sslSocketFactory = sslUtil.createSSLSocketFactory();

									// Establish a secure connection using the
									// socket
									// factory.
									// connection = new
									// LDAPConnection(sslSocketFactory,
									// newserver, ldapPort, dn, password);

									connection = new LDAPConnection(sslSocketFactory, gcServer, gcPort, dn, password);

								} else {
									connection = new LDAPConnection(gcServer, ldapPort, dn, password);
									// connection = new
									// LDAPConnection(newserver,
									// ldapPort, dn, password);
								}

								if (connection != null) {

									// Correct Password - User is properly
									// verified
									u.setVerifiedID(dn);
									u.setVerifiedBy("University of Missouri");

									u.put(UserAttribute.LOGINNAME, sso);

									if (logger.isDebugEnabled()) {
										logger.debug("UM SSO = {}", u.get(User.UserAttribute.USERID));
									}

									connection.close();

									final String encryptedPassword = encryptor.encryptPassword(password);

									if (logger.isDebugEnabled()) {
										logger.debug("encrypted password is {} {}", encryptedPassword.length(), encryptedPassword);
									}

									storedPasswordMatches = true;

									Runnable runnable = new Runnable() {

										@Override
										public void run() {
											
											if(logger.isDebugEnabled()) {
												logger.debug("Updating password for {}", sso);
											}

											try (Connection conn = Pools.getConnection(Pools.Names.REPORTCENTER)) {

												try (PreparedStatement stmt = conn.prepareStatement(
														"update users set password = ? where upper(userlogin) = upper(?)")) {

													stmt.setString(1, encryptedPassword);
													stmt.setString(2, sso);
													stmt.executeUpdate();
												}
												conn.commit();

											} catch (SQLException e) {

												if (logger.isErrorEnabled()) {
													logger.error("Unable to set password");
												}

												e.printStackTrace();

											}
										}

									};
									
									new Thread(runnable).start();

								} else {

									if (logger.isErrorEnabled()) {
										logger.error("Unable to make ldap connection to " + newserver);
									}

									return false;

								}

							} catch (LDAPException e) {

								if (logger.isErrorEnabled()) {
									logger.error("LDAP Exception occurred ", e);
								}
								return false;

							} catch (GeneralSecurityException e) {

								if (logger.isErrorEnabled()) {
									logger.error("LDAP SSL Exception occurred ", e);
								}
								return false;

							} catch (LDAPSDKUsageException ldue) {

								if (logger.isErrorEnabled()) {
									logger.error("LDAP Could not connect");
								}
								return false;

							} finally {

								if (connection != null) {
									connection.close();
								}
							}

						} else {

							if (logger.isErrorEnabled()) {
								logger.error("Unable to Make LDAP Connection to global catalog server");
							}

							return false;

						}

					} catch (LDAPException e) {

						if (logger.isErrorEnabled()) {
							logger.error("LDAP Exception occurred ", e);
						}
						return false;

					} catch (GeneralSecurityException e) {

						if (logger.isErrorEnabled()) {
							logger.error("LDAP SSL Exception occurred ", e);
						}
						return false;

					} finally {

						if (gcConnection != null) {
							gcConnection.close();
						}

					}

				}

				if (logger.isDebugEnabled()) {
					logger.debug("Timing {}, {} ms", i++, new java.util.Date().getTime() - start.getTime());
				}

				if (storedPasswordMatches) {

					if (enableUserSubstitution) {

						if (logger.isDebugEnabled()) {
							logger.debug("Attempting user substitution");
						}

						try (PreparedStatement stmt = c.prepareStatement(
								"select * from userdetails u left outer join substituteusers s on s.realuserid = u.id where upper(u.userlogin) = upper(?) and u.isactive = 1 and u.status = 'ACTIVE'")) {

							stmt.setString(1, sso);
							try (ResultSet rs = stmt.executeQuery()) {
								if (rs.next()) {

									if (null != rs.getString("SUBSTITUTEUSERID")) {

										if (logger.isDebugEnabled()) {
											logger.debug("SUBSTITUTEUSERID = {}", rs.getString("SUBSTITUTEUSERID"));
										}

										try (PreparedStatement stmt1 = c
												.prepareStatement("select * from userdetails where id = ?")) {

											stmt1.setString(1, rs.getString("SUBSTITUTEUSERID"));

											try (ResultSet rs1 = stmt1.executeQuery()) {

												if (rs1.next()) {

													if (logger.isDebugEnabled()) {
														logger.debug("Found user record for substitute record");
													}

													u.setValuesFromDatabase(dn, "University of Missouri", rs1);
													u.put(UserAttribute.AUTHPROVIDERLOGIN, rs1.getString("USERLOGIN"));

													logger.debug("Timing {}, {} ms", i++,
															new java.util.Date().getTime() - start.getTime());
													logger.debug("EnterpriseAuthenticator returning true");

													return true;

												} else {

													if (logger.isDebugEnabled()) {
														logger.debug("Could not select from userdetails user");
													}

												}
											}
										}

									} else {

										if (logger.isDebugEnabled()) {
											logger.debug("Did not find substitute userid");
										}

										u.setValuesFromDatabase(dn, "University of Missouri", rs);
										u.put(UserAttribute.AUTHPROVIDERLOGIN, sso);

										logger.debug("Timing {}, {} ms", i++, new java.util.Date().getTime() - start.getTime());
										logger.debug("EnterpriseAuthenticator returning true");

										return true;

									}
								}
							}
						}

					} else {

						try (PreparedStatement stmt = c.prepareStatement(
								"select * from userdetails where upper(userlogin) = upper(?) and isactive = 1 and status = 'ACTIVE'")) {

							stmt.setString(1, sso);
							try (ResultSet rs = stmt.executeQuery()) {

								if (rs.next()) {

									u.setValuesFromDatabase(dn, "University of Missouri", rs);
									u.put(UserAttribute.AUTHPROVIDERLOGIN, sso);

									logger.debug("Timing {}, {} ms", i++, new java.util.Date().getTime() - start.getTime());
									logger.debug("EnterpriseAuthenticator returning true");

									return true;

								} else {

									if (logger.isDebugEnabled()) {
										logger.debug("Could not select from userdetails from sso = {}", sso);
									}

								}

							}
						}
					}

				}
			}

		} catch (SQLException e) {

			if (logger.isDebugEnabled()) {
				logger.debug("Error locating userlogin", e);
			}

		} finally {
			try {

				if (c != null) {
					c.commit();
				}

			} catch (SQLException sqle) {

			}

			Pools.releaseConnection(Pools.Names.REPORTCENTER, c);
		}

		if (logger.isErrorEnabled()) {
			logger.error("End reached - should not happen");
		}

		logger.debug("Timing {}, {} ms", i++, new java.util.Date().getTime() - start.getTime());
		logger.debug("EnterpriseAuthenticator returning false");

		return false;
	}
}
