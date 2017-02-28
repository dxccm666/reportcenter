package edu.missouri.cf.projex4.data.system.core;

import edu.missouri.operations.data.OracleString;


public class Campus {
	
	private OracleString id;
	private String campusName;
	
	public String toString() {
		return campusName;
	}

	/**
	 * @return the id
	 */
	public OracleString getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(OracleString id) {
		this.id = id;
	}

	/**
	 * @return the campusName
	 */
	public String getCampusName() {
		return campusName;
	}

	/**
	 * @param campusName the campusName to set
	 */
	public void setCampusName(String campusName) {
		this.campusName = campusName;
	}
	
}
