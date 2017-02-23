package edu.missouri.operations.data;


public class OracleUppercaseString {
	
	String value;

	public OracleUppercaseString() { }
	
	public OracleUppercaseString(String value) {
		if(value!=null) {
			this.value = Purifier.purify(value).toUpperCase();
		} else {
			this.value = null;
		}
	}
	
	public String toString() {
		return value;
	}

}
