/**
 * 
 */
package edu.missouri.cf.projex4.data.system.core.email;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.data.OracleTimestamp;
import edu.missouri.operations.reportcenter.Pools;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class EmailLogs extends OracleQuery {

	static Logger logger = LoggerFactory.getLogger(EmailLogs.class);

	public EmailLogs() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
		setQueryString("select * from emaillogs");
		setRowQueryString("select * from emaillogs where id = ?");
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
		
		System.err.println("EmailLog storeRow");
		
		try {
		
	    String newid = null;
	    int retval = 0;
	    try ( java.sql.CallableStatement call = conn.prepareCall("{ ? = call EMAIL.EMAILLOG (?, ?, ?, ?) }")) {

	        int i = 1;
	        call.registerOutParameter(i++, java.sql.Types.VARCHAR); 
	        setString(call, i++, getString(row,"ID"));
	        setString(call, i++, getString(row, "EMAILADDRESS"));
	        setTimestamp(call, i++, OracleTimestamp.now());
	        setString(call, i++, getString(row,"STATUSMESSAGE"));
	        
	        retval = call.executeUpdate();
	        newid = call.getString(1);
	        if(logger.isDebugEnabled()) {
	            logger.debug("newid = {} retval = {}",newid, retval);
	        }
	        
	    }
	    setLastId(newid);
	    return retval;
	    
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
}
