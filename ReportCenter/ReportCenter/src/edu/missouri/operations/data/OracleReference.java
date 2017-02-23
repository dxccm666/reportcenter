package edu.missouri.operations.data;


public class OracleReference {

	String value;

	public OracleReference() {
	}

	public OracleReference(String value) {
		if (!(null == value || value.length() == 0 || value.length() > 10)) {
			this.value = Purifier.purify(value);
		} else {
			this.value = null;
		}
	}

	public String toString() {
		return value;
	}

}
