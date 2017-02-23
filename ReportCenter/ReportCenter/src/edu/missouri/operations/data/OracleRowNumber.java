package edu.missouri.operations.data;

import java.math.BigDecimal;
import java.math.BigInteger;

@SuppressWarnings("serial")
public class OracleRowNumber extends OracleDecimal {

	public OracleRowNumber(char[] in) {
		super(in);
	}

	public OracleRowNumber(String val) {
		super(val);
	}

	public OracleRowNumber(double val) {
		super(val);
	}

	public OracleRowNumber(BigInteger val) {
		super(val);
	}
	
	public OracleRowNumber(BigDecimal val) {
		super(val);
	}

	public OracleRowNumber(int val) {
		super(val);
	}

	public OracleRowNumber(long val) {
		super(val);
	}
	
	public String toString() {
		return Formatter.getIntegerFormat().format(doubleValue());
	}
	
	public static OracleRowNumber valueOf(String val) {
		return new OracleRowNumber(val);
	}
	
	public static OracleRowNumber valueOf(long val) {
		return new OracleRowNumber(val);
	}

}
