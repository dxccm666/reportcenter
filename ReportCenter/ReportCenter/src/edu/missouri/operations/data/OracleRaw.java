package edu.missouri.operations.data;

@SuppressWarnings("serial")
public class OracleRaw extends oracle.sql.RAW {

	public OracleRaw(byte[] value) {
		super(value);
	}
	
	public OracleRaw(String value) {
		
	}
	
	@Override 
	public String toString() {
		return stringValue();
	}
}
