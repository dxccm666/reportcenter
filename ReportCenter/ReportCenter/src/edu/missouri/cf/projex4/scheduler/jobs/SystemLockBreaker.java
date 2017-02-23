package edu.missouri.cf.projex4.scheduler.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SystemLockBreaker extends ProjexDefaultJob {

	@Override
	public void execute(JobExecutionContext context, Connection conn) throws JobExecutionException, SQLException {

		try (PreparedStatement stmt = conn.prepareStatement("delete from systemlocks where locked < sysdate - 1")) {
			int deletedRows = stmt.executeUpdate();
			setLogMessage(deletedRows + " rows deleted.");
		}
	}
}
