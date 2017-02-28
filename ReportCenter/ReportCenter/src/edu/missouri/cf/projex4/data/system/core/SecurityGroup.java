package edu.missouri.cf.projex4.data.system.core;

import edu.missouri.operations.data.OracleString;

public class SecurityGroup {
	
	private OracleString id;
	private String securityGroupName;
	
	public String toString() {
		return securityGroupName;
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
	public String getSecurityGroupName() {
		return securityGroupName;
	}

	/**
	 * @param campusName the campusName to set
	 */
	public void setSecurityGroupName(String securityGroupName) {
		this.securityGroupName = securityGroupName;
	}
	
}
