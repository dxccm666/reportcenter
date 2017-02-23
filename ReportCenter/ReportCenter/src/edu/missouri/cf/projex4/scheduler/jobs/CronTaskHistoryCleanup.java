package edu.missouri.cf.projex4.scheduler.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CronTaskHistoryCleanup extends ProjexDefaultJob {
	
	public CronTaskHistoryCleanup() { }
	
	final int days = 10;

	@Override
	public void execute(JobExecutionContext context, Connection conn) throws JobExecutionException, SQLException {
		
		start();
		try(PreparedStatement stmt = conn.prepareStatement("delete from crontaskruns where runstart < sysdate - ? ")) {
			stmt.setInt(1, days);
			stmt.executeUpdate();
		}
	}
	
}
