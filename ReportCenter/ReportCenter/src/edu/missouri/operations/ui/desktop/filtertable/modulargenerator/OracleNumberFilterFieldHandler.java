package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import edu.missouri.operations.data.OracleDecimal;
import edu.missouri.operations.data.OracleInteger;
import edu.missouri.operations.data.OracleCurrency;

public class OracleNumberFilterFieldHandler extends NumberFilterFieldHandler {

	@Override
	public boolean handlesType(Class<?> clazz) {
		return ( clazz == OracleCurrency.class || clazz == OracleDecimal.class || clazz == OracleInteger.class );
	}
	
}