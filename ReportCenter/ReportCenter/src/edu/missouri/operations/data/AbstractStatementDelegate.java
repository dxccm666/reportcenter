package edu.missouri.operations.data;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

@SuppressWarnings("serial")
public abstract class AbstractStatementDelegate implements FreeformStatementDelegate {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected String countStatement = null;

	protected void setCountStatement(String countStatement) {
		this.countStatement = countStatement + " ";
	}
	
	private String queryStatement = null;

	protected void setQueryStatement(String queryStatement) {
		this.queryStatement = queryStatement + " ";
	}

	private String rowQueryStatement = null;

	protected void setRowQueryStatement(String rowQueryStatement) {
		this.rowQueryStatement = rowQueryStatement;
	}

	protected List<Filter> filters;

	@Override
	public void setFilters(List<Filter> filters) {
		if (filters != null && filters.size() > 0) {
			logger.debug("Filters being set " + filters.size());
			this.filters = filters;
		} else {
			logger.debug("Filters is null or empty");
		}
	}

	protected List<OrderBy> orderBys;

	@Override
	public void setOrderBy(List<OrderBy> orderBys) throws UnsupportedOperationException {
		this.orderBys = orderBys;
	}

	@Override
	public int storeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {
		throw new UnsupportedOperationException("Cannot Insert Rows");
	}

	@Override
	public boolean removeRow(Connection conn, Item row) throws UnsupportedOperationException, SQLException {
		throw new UnsupportedOperationException("Cannot Delete Rows");
	}

	@Override
	public StatementHelper getQueryStatement(int offset, int limit) {
		StatementHelper sh = new StatementHelper();
		StringBuffer query = new StringBuffer(queryStatement);

		if (filters != null && !filters.isEmpty()) {

			query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
			System.err.println("After Filter processing " + query.toString());

		} else {

			System.err.println("Filters not found");
		}

		if (offset != 0 || limit != 0) {

			if (offset == 0 && limit == 1) {
				limit = 200;
			}

			/*
			 * query.insert(0, "select * from ( select a.*, rownum r from ( ");
			 * query.append(" ) a  where rownum < " + (offset + limit - 1) +
			 * " ) where r > " + offset);
			 */

			query.insert(0, "select * from ( select a.*, rownum r from ( ");
			query.append(" ) a )  where r between " + offset + " and " + (offset + limit - 1));
		}

		if (orderBys != null && !orderBys.isEmpty()) {
			query.append(" ORDER BY ");
			OrderBy lastOrderBy = orderBys.get(orderBys.size() - 1);
			for (OrderBy orderBy : orderBys) {
				query.append(SQLUtil.escapeSQL(orderBy.getColumn()));
				if (orderBy.isAscending()) {
					query.append(" ASC");
				} else {
					query.append(" DESC");
				}
				if (orderBy != lastOrderBy) {
					query.append(", ");
				}
			}
		}

		System.err.println(query.toString());
		sh.setQueryString(query.toString());
		return sh;
	}


	@Override
	public StatementHelper getCountStatement() throws UnsupportedOperationException {
		StatementHelper sh = new StatementHelper();
		if (countStatement != null) {
			StringBuffer query = new StringBuffer(countStatement);
			if (filters != null) {
				try {
					query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
				} catch (Exception e) {
					System.err.println("Exception occurred in getCountStatement");
					e.printStackTrace();
				}
			}
			sh.setQueryString(query.toString());
		}
		return sh;

	}

	@Override
	public StatementHelper getContainsRowQueryStatement(Object... keys) throws UnsupportedOperationException {
		StatementHelper sh = new StatementHelper();
		StringBuffer query = new StringBuffer(rowQueryStatement);
		sh.addParameterValue(keys[0]);
		sh.setQueryString(query.toString());
		return sh;
	}

	public BigDecimal toOracleBoolean(Object o) {
		if (null == o) {
			return new BigDecimal(0);
		} else {
			return (BigDecimal) o;
		}
	}

	public void setOracleBoolean(CallableStatement call, RowItem row, String item) throws SQLException {

		Object o = row.getItemProperty(item).getValue();
		if (null == o) {
			call.setBoolean(8, Boolean.FALSE);
		} else {
			call.setBigDecimal(8, (BigDecimal) o);
		}

	}

}
