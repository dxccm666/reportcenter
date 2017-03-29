package edu.missouri.cf.projex4.scheduler.jobs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.missouri.operations.data.OracleHelper;
import edu.missouri.operations.data.OracleTimestamp;
import edu.missouri.operations.reportcenter.Pools;

public abstract class ProjexDefaultJob implements Job, ProjexJob {

	protected transient Logger logger = LoggerFactory.getLogger(ProjexDefaultJob.class);

	protected DataSource dataSource;
	protected OracleTimestamp start;

	Connection conn = null;

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	public void start() {
		if (logger.isDebugEnabled()) {
			logger.debug("{} executing.", this.getClass().getCanonicalName());
		}
		start = OracleTimestamp.now();
	}

	String logMessage;

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public ProjexDefaultJob() {

		try {

			OracleDataSource odataSource = new OracleDataSource();
			odataSource.setURL(Pools.dbConnectionString);
			odataSource.setUser("projex4");
			odataSource.setPassword("prj4_user");
			dataSource = odataSource;

		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	@Override
	public void executeNow() {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				try {
					execute(null);
				} catch (JobExecutionException e) {
					if(logger.isErrorEnabled()) {
						logger.error("Unable to run cron task manually", e);
					}

				}
			}
		};
		
		new Thread(runnable).start();

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		start();

		try (Connection conn = dataSource.getConnection()) {

			try {

				setConnection(conn);

				conn.setAutoCommit(false);
				execute(context, conn);
				conn.commit();

				if (logMessage != null) {
					success(logMessage);
				} else {
					success();
				}

			} catch (Exception e) {

				e.printStackTrace();

				failure(e.getMessage());
				throw new JobExecutionException(e.getMessage());

			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	public abstract void execute(JobExecutionContext context, Connection conn) throws JobExecutionException, SQLException;

}
