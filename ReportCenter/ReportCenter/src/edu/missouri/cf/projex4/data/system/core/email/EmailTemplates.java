/**
 * 
 */
package edu.missouri.cf.projex4.data.system.core.email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.data.OracleTimestamp;
import edu.missouri.operations.data.User;
import edu.missouri.operations.reportcenter.Pools;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class EmailTemplates extends OracleQuery {

	static Logger logger = LoggerFactory.getLogger(EmailTemplates.class);

	public EmailTemplates() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from emailtemplates");
		setRowQueryString("select * from emailtemplates where id = ?");
		setPrimaryKeyColumns("ID");
	}

	/**
	 * Stores a row in the database.
	 *
	 * @param Item
	 *            A Vaadin Data Item containing values to be updated.
	 * @return the number of affected rows in the database table
	 *
	 * @throws UnsupportedOperationException
	 *             if the implementation is read only.
	 *
	 * @see com.vaadin.data.util.sqlcontainer.query.OracleDelegate#storeRow(Connection , Item)
	*/
	@Override
	public int storeRow(java.sql.Connection conn, com.vaadin.data.Item row) throws UnsupportedOperationException, java.sql.SQLException { 
	    String newid = null;
	    int retval = 0;
	    try ( java.sql.CallableStatement call = conn.prepareCall("{ ? = call EMAIL.EMAILTEMPLATE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }")) {

	        int i = 1;
	        call.registerOutParameter(i++, java.sql.Types.VARCHAR); 
	        
	        		
	        setString(call, i++, getString(row,"ID"));
	        setString(call, i++, getString(row,"ROWSTAMP"));

	        setString(call, i++, getString(row, "EMAILNAME"));
	        setString(call, i++, getString(row, "EMAILSUBJECT"));
	        setString(call, i++, getString(row, "CONTENT"));
	        setString(call, i++, getString(row, "HELPTEXT"));
	        setBoolean(call, i++, getOracleBoolean(row, "ISACTIVE"));
	        setString(call, i++, getString(row, "CREATEDBY"));
	        setTimestamp(call, i++, getOracleTimestamp(row, "CREATED"));
	        setString(call, i++, User.getUser().getUserId());
	        setTimestamp(call, i++, OracleTimestamp.now());
	        
	        retval = call.executeUpdate();
	        newid = call.getString(1);
	        if(logger.isDebugEnabled()) {
	            logger.debug("newid = {} retval = {}",newid, retval);
	        }
	    }
	    setLastId(newid);
	    return retval;
	}
	
	public static String getTemplate(String emailName) {
		
		Connection conn = null;
		String template = null;
		try {
			conn = Pools.getConnection(Pools.Names.REPORTCENTER);
			try (PreparedStatement stmt = conn.prepareStatement("select content from emailtemplates where emailname = ?")) {
				setString(stmt, 1, emailName);
				try(ResultSet rs = stmt.executeQuery()) {
					if(rs.next()) {
						template = rs.getString(1);
					}
				}
			}
			
		} catch (SQLException sqle) {
			if(logger.isErrorEnabled()) {
				logger.error("Could not retrieve template",sqle);
			}
			
		} finally {
			Pools.releaseConnection(Pools.Names.REPORTCENTER, conn);
		}
		return template;
		
	}



}
