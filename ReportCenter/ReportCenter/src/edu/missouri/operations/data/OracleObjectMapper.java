package edu.missouri.operations.data;

import com.vaadin.data.util.sqlcontainer.OracleContainer;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleObjectMapper implements OracleContainer.ObjectMapper {

	private static Logger logger = LoggerFactory.getLogger(OracleObjectMapper.class);

	public OracleObjectMapper() {
	}

	/**
	 * This function attempts to guess which OracleData type a particular column
	 * is based on its properties. Be careful to define all database fields in a
	 * standard way.
	 * 
	 * @param columnClass
	 * @param precision
	 * @param scale
	 * @return
	 */
	@Override
	public Class<?> assignType(Class<?> columnClass, int precision, int scale) {
		Class<?> type;
		if (columnClass == BigDecimal.class) {
			switch (precision) {
			case 0:
				switch (scale) {
				case -127:
				// 	type = OracleDecimal.class;
				//	break;
				default:
					type = BigDecimal.class;
					break;
				}
				break;
			case 1:
				type = OracleBoolean.class;
				break;
			case 6:
				switch (scale) {
				case 0:
					// type = OracleInteger.class;
					// break;
				case 3:
					// type = ProjexShortPercent.class;
					// break;
				default:
					// type = OracleDecimal.class;
					type = BigDecimal.class;
					break;
				}
				break;
			case 15:
				switch (scale) {
				case 0:
					// type = OracleInteger.class;
					type = BigDecimal.class;
					break;
				case 2:
					type = OracleCurrency.class;
					break;
				default:
					type = BigDecimal.class;
					// type = OracleDecimal.class;
					break;
				}
				break;
			case 16:
				switch (scale) {
				case 0:
					// type = OracleInteger.class;
					type = BigDecimal.class;
					break;
				case 12:
					// type = ProjexLongPercent.class;
					// break;
				default:
					// type = OracleDecimal.class;
					type = BigDecimal.class;
					break;

				}
			default:
				switch(scale) {
				case 0 : type = OracleInteger.class;
					break;
				default : 
					// type = OracleDecimal.class;
					type = BigDecimal.class;
					break;
				}
				// type = BigDecimal.class;
			}
		} else if (columnClass == java.sql.Date.class) {
			type = OracleDate.class;
		} else if (columnClass == java.sql.Timestamp.class) {
			type = OracleTimestamp.class;
		} else if (columnClass == oracle.sql.TIMESTAMP.class) {
			type = OracleTimestamp.class;
		} else if (columnClass == String.class) {
			type = OracleString.class;
		} else if (columnClass == byte[].class) {
			type = OracleRaw.class;
		} else {
			type = columnClass;
		}
		return type;
	}

	/**
	 * Handles actual object conversion to new types.
	 * 
	 * @param object
	 * @param columnClass
	 * @return
	 */
	@Override
	public Object createObject(Object object, Class<?> columnClass) {

		if (object == null) {
			return null;
		}

		Class<?> objectClass = object.getClass();
		
		if (objectClass == columnClass) {
			return object;
		} else if (objectClass == BigDecimal.class) {

			if (columnClass == OracleDecimal.class) {
				return new OracleDecimal((BigDecimal) object);
			} else if (columnClass == OracleCurrency.class) {
				return new OracleCurrency((BigDecimal) object);
			} else if (columnClass == OracleBoolean.class) {
				return new OracleBoolean((BigDecimal) object);
			} else if (columnClass == OracleInteger.class) {
				return new OracleInteger((BigDecimal) object);
			}

		} else if (objectClass == java.sql.Date.class) {
			return new OracleDate(((java.sql.Date) object).getTime());
		} else if (objectClass == java.sql.Timestamp.class) {
			return new OracleTimestamp(((java.sql.Timestamp) object).getTime());
		} else if (objectClass == oracle.sql.TIMESTAMP.class) {

			try {
				if (columnClass == OracleTimestamp.class) {
					return new OracleTimestamp(((oracle.sql.TIMESTAMP) object)
							.timestampValue().getTime());
				} else if (columnClass == OracleDate.class) {
					return new OracleDate(((oracle.sql.TIMESTAMP) object)
							.timestampValue().getTime());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else if (objectClass == String.class) {
			// custom classes added will have a value of String. But it needs to be of that type.
			// Otherwise the converter will throw a class cast exception because the value class != type class. (Justin)
			// if (columnClass == PersonId.class) {
			// 	return new PersonId((String) object);
			// } else if (columnClass == UserId.class) {
			// 	return new UserId((String) object);
			// } else 
			if (columnClass == OracleString.class) {
				return new OracleString((String) object);
			}
		} else if (object instanceof byte[]) {

			if (columnClass == OracleRaw.class) {
				return new OracleRaw((byte[]) object);
			}
		}

		return object;
	}
	
	public Object createObject(Object object, int precision, int scale) {
		return createObject(object,
				assignType(object.getClass(), precision, scale));
	}

	@Override
	public boolean handlesColumn(String columnName) {
		switch (columnName) {
		case "R":
		case "APPLICATIONID" :
			return true;
		default:
			return false;

		}
	}

	@Override
	public Class<?> assignType(String columnName) {
		switch (columnName) {
		case "R" : return OracleRowNumber.class;
		default : return null;
		}
	}

	@Override
	public Object createObject(String columnName, ResultSet rs) throws SQLException {
		switch (columnName) {
		case "R":
			return new OracleRowNumber(rs.getBigDecimal("R"));
		default:
			return null;
		}
	}

}
