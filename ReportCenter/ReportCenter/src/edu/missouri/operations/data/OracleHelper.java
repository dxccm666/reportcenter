package edu.missouri.operations.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

public class OracleHelper {

	protected static Logger logger = LoggerFactory.getLogger(OracleHelper.class);
	
	static boolean useLogger = false;

	public static void setString(PreparedStatement call, int pos, Object value) throws SQLException {
		if (value == null) {

			if (useLogger && logger.isTraceEnabled()) {
				logger.trace("Setting parameter {} to null", pos);
			}
			call.setNull(pos, Types.VARCHAR);
		} else {

			if(useLogger && logger.isTraceEnabled()) {
				logger.trace("Setting parameter {} to {}", pos, value.toString());
			}

			call.setString(pos, Purifier.purify(value.toString()));
		}

	}
	
	public static Object getValue(Item row, Object id) {
		if (row.getItemProperty(id) != null) {
			return row.getItemProperty(id).getValue();
		} else {
			return null;
		}
	}
	
	public static OracleDecimal getOracleDecimal(Item row, Object id) {
		Object o = getValue(row, id);
		if (o == null) {
			return null;
		}
		if (o instanceof OracleDecimal) {
			return (OracleDecimal) o;
		} else if (o instanceof BigDecimal) {
			// Handle downcast from BigDecimal without loss of precision.
			return new OracleDecimal((BigDecimal) o);
		} else if (o instanceof BigInteger) {
			// Handle conversion from BigInteger without loss of precision.
			return new OracleDecimal((BigInteger) o);
		} else if (o instanceof Integer) {
			return new OracleDecimal((Integer) o);
		} else {
			return new OracleDecimal(((Number) o).doubleValue());
		}
	}
	
	public static String getString(Item item, String propertyId) {
		return item.getItemProperty(propertyId).getValue().toString();
	}

	public static void setBigDecimal(PreparedStatement call, int pos, Object value) throws SQLException {
		if (value == null) {
			// WARNING - We always set a BigDecimal to 0 if it is null.

			if (useLogger && logger.isTraceEnabled()) {
				logger.trace("Setting parameter {} to BigDecimal 0 as null default", pos);

			}
			call.setBigDecimal(pos, new OracleDecimal(0));
		} else {
			if (value instanceof OracleBoolean) {

				if (useLogger && logger.isTraceEnabled()) {
					logger.trace("Setting parameter {} to {}", pos, ((OracleBoolean) value).toBigDecimal());

				}
				call.setBigDecimal(pos, ((OracleBoolean) value).toBigDecimal());
			} else {

				if (useLogger && logger.isTraceEnabled()) {
					logger.trace("Setting parameter {} to {}", pos, (BigDecimal) value);

				}
				call.setBigDecimal(pos, (BigDecimal) value);
			}
		}
	}

	public static void setTimestamp(PreparedStatement call, int pos, Object value) throws SQLException {
		if (value == null) {

			if (useLogger && logger.isTraceEnabled()) {
				logger.trace("Setting parameter {} to null timestamp", pos);

			}
			call.setNull(pos, Types.TIMESTAMP);
		} else if (value instanceof Timestamp) {

			if (useLogger && logger.isTraceEnabled()) {
				logger.trace("Setting parameter {} to {}", pos, (Timestamp) value);

			}
			call.setTimestamp(pos, (Timestamp) value);
		} else if (value instanceof java.sql.Date) {

			if (useLogger && logger.isTraceEnabled()) {
				logger.trace("Setting parameter {} to {}", pos, (java.sql.Date) value);

			}
			call.setDate(pos, (java.sql.Date) value);
		} else if (value instanceof java.util.Date) {

			if (useLogger && logger.isTraceEnabled()) {
				logger.trace("Setting parameter {} to {}", pos, (java.util.Date) value);

			}
			call.setTimestamp(pos, new java.sql.Timestamp(((java.util.Date) value).getTime()));
		}
	}

	public static void setBoolean(PreparedStatement call, int pos, Object value) throws SQLException {

		if (value == null) {

			if (useLogger && logger.isTraceEnabled()) {
				logger.trace("Setting parameter {} to Boolean Zero as null default", pos);

			}
			setBigDecimal(call, pos, BigDecimal.ZERO);
		} else if (value instanceof OracleBoolean) {
			if (OracleBoolean.TRUE.equals((OracleBoolean) value)) {


				if (useLogger && logger.isTraceEnabled()) {
					logger.trace("Setting parameter {} to Boolean One", pos);

				}

				setBigDecimal(call, pos, BigDecimal.ONE);
			} else {


				if (useLogger && logger.isTraceEnabled()) {
					logger.trace("Setting parameter {} to Boolean Zero", pos);

				}
				setBigDecimal(call, pos, BigDecimal.ZERO);
			}

		} else {


			if (useLogger && logger.isTraceEnabled()) {
				logger.trace("Setting parameter {} to {}", pos, ((Boolean) value).booleanValue());

			}

			call.setBoolean(1, ((Boolean) value).booleanValue());
		}

	}

}
