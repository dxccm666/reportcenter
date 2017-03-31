/**
 * 
 */
package edu.missouri.operations.reportcenter.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.reportcenter.Pools;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class EmailLogs extends OracleQuery {

	static final transient Logger logger = LoggerFactory.getLogger(EmailLogs.class);

	public EmailLogs() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from emaillogs");
		setRowQueryString("select * from emaillogs where id = ?");
		setPrimaryKeyColumns("ID");
	}

}
