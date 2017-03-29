/**
 * 
 */
package edu.missouri.operations.reportcenter.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.query.OracleQuery;

import edu.missouri.operations.reportcenter.Pools;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class CronTaskRuns extends OracleQuery {

	static Logger logger = LoggerFactory.getLogger(CronTaskRuns.class);

	/**
	 * @param connectionPool
	 */
	public CronTaskRuns() {
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
		setQueryString("select * from crontaskruns");
		setRowQueryString("select * from crontaskruns where id = ?");
		setPrimaryKeyColumns("ID");
	}
	
	public void setJavaClass(String javaClass) {
		removeMandatoryFilters();
		setMandatoryFilters(new Compare.Equal("JAVACLASS", javaClass)); 
	}

}
