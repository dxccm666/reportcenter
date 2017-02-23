package edu.missouri.cf.projex4.scheduler.jobs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.missouri.operations.data.OracleHelper;
import edu.missouri.operations.data.OracleTimestamp;

public class CronTaskStatusMessage {
	
	protected transient Logger logger = LoggerFactory.getLogger(CronTaskStatusMessage.class);
	
	protected OracleTimestamp start;
	
	protected Connection conn;
	
	public void setConnection(Connection conn) {
		this.conn = conn;
	}
	
	public void setStart(OracleTimestamp start) {
		this.start = start;
	}
	
	public enum Status {
		SUCCESS, FAILURE
	}

	public void success(String message) {
		if (logger.isDebugEnabled()) {
			logger.debug("{} execution successful - {}", this.getClass().getCanonicalName(), message);
		}
		addRecord(Status.SUCCESS, message);
	}

	public void success() {
		if (logger.isDebugEnabled()) {
			logger.debug("{} execution successful", this.getClass().getCanonicalName());
		}
		addRecord(Status.SUCCESS, "crontask executed successfully.");
	}

	public void failure(String message) {
		if (logger.isDebugEnabled()) {
			logger.debug("{} execution unsuccessful - {} ", this.getClass().getCanonicalName(), message);
		}
		addRecord(Status.FAILURE, message);
	}

	private void addRecord(Status status, String message) {

		if (conn != null) {

			try (CallableStatement call = conn.prepareCall("{ ? = call core.crontaskrun(?,?,?,?,?,?,?) }")) {

				int i = 1;
				call.registerOutParameter(i++, Types.VARCHAR);

				OracleHelper.setString(call, i++, null);
				OracleHelper.setString(call, i++, this.getClass().getCanonicalName());

				try {
					OracleHelper.setString(call, i++, InetAddress.getLocalHost().getHostAddress());
				} catch (UnknownHostException e) {
					OracleHelper.setString(call, i++, "Unknown Host");
				}

				OracleHelper.setTimestamp(call, i++, start);
				OracleHelper.setTimestamp(call, i++, OracleTimestamp.now());
				OracleHelper.setString(call, i++, status.name());
				OracleHelper.setString(call, i++, message);
				call.executeUpdate();

			} catch (SQLException sqle) {

				sqle.printStackTrace();

				if (logger.isErrorEnabled()) {
					logger.error("Unable to add CronTaskRun", sqle);
				}

			}

		}

	}

}
