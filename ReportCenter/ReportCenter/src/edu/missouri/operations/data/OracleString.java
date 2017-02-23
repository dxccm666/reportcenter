package edu.missouri.operations.data;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Justin modified OracleString to implement Comparable and equals, so that it uses the String version of Comparable
 * and equals. This is needed for OracleStatementHelper and Vaadin in memory Filtering. 
 */
public class OracleString implements Serializable, Comparable<OracleString> {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 6624713474225227296L;
	private String value;
	
	public OracleString() { }
	
	public OracleString(Object value) {
		this(value != null ? value.toString() : "");
	}
	
	public OracleString(String value) {
		this.value = Purifier.purify(value);
	}
	
	public OracleString(OracleString s) {
		this(s.toString());
	}
	
	@Override
	public String toString() {
		return value;
	}

	@Override
	public int compareTo(OracleString o) {
		//logger.debug("oracleString compareTo {} = {}", value, o);
		if (o == null) {
			return -1;
		}
		return this.value.compareTo(o.value);
	}
	
	@Override
	public boolean equals(Object o) {
		//logger.debug("oracleString equals, {} = {}", value, o);
		if (o == null || value == null) {
			return false;
		}
		//logger.debug("value = {}, o = {}", value.getClass(), o.getClass());
		return value.equals(o.toString());
	}
	
	@Override 
	public int hashCode() {
		//logger.debug("oracleString hash code");
		if (value != null) {
			return value.hashCode();
		} else {
			return 0;
		}
	}
	
	public String getValue() {
		if(isEmpty()) {
			return null;
		}
		return value;
	}
	
	public void setValue(String value) {
		this.value = Purifier.purify(value);
	}
	
	public void setValue(OracleString value) {
		this.value = Purifier.purify(value.toString());
	}
	
	public boolean isEmpty() {
		return null == value || value.isEmpty();
	}
}
