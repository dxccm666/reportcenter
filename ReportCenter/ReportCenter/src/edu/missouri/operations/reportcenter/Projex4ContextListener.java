package edu.missouri.operations.reportcenter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.missouri.cf.projex4.scheduler.jobs.ReportJob;
import edu.missouri.operations.data.OracleBoolean;

@WebListener("Projex4 Context Listener")
public class Projex4ContextListener implements ServletContextListener {

	protected static Logger logger = LoggerFactory.getLogger(Projex4ContextListener.class);

	// public static Pools pools;

	public Projex4ContextListener() {
	}

	private void initReport(ServletContext context) {

		if (logger.isDebugEnabled()) {
			logger.debug("ReportEngine is being started.");
		}

		EngineConfig config = new EngineConfig();
		config.setLogConfig("/home/projex4/reports.log", java.util.logging.Level.SEVERE);

		try {

			Platform.startup(config);
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);

			IReportEngine engine = factory.createReportEngine(config);
			context.setAttribute("REPORTENGINE", engine);

		} catch (Exception e) {

			if (logger.isErrorEnabled()) {
				logger.error("Error in initializing ReportEngine", e);
			}

		}

	}

	private void scheduleReportJobs(Scheduler sched) throws SchedulerException {
		
		String sql = "select * from reportcrontaskdetails where isactive = 1";	
		Connection conn = null;
		try {
			conn = Pools.getConnection(Pools.Names.PROJEX);
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				try(ResultSet rs = stmt.executeQuery()) {
					
					while(rs.next()) {
						
						if (logger.isDebugEnabled()) {

							logger.debug("Scheduling report crontask {} {} for {}", rs.getString("ID"),
									rs.getString("REPORTID"), rs.getString("CRONEXPRESSION"));
						}
						
						try {

						JobDetail jobDetail = JobBuilder.newJob(ReportJob.class)
								.withIdentity("report" + rs.getString("ID"), "reportgroup")
								.usingJobData("ReportCronTaskId", rs.getString("ID").toString())
								.usingJobData("ReportId", rs.getString("REPORTID"))
								.usingJobData("ScheduledBy", rs.getString("SCHEDULEDBY"))
								.usingJobData("FileFormat", rs.getString("FILEFORMAT"))
								.usingJobData("OneTime", OracleBoolean.toBoolean(rs.getBigDecimal("ISONETIME")))
								.build();
						
						CronTrigger trigger = TriggerBuilder.newTrigger()
								.withIdentity("trigger" + rs.getString("ID"), "reportgroup")
								.withSchedule(
										CronScheduleBuilder.cronSchedule(rs.getString("CRONEXPRESSION")))
								.build();

						sched.scheduleJob(jobDetail, trigger);
						
						} catch (org.quartz.SchedulerException se) {
							
							if(logger.isErrorEnabled()) {
								logger.error("Unable to reschedule reportcontask {} ", rs.getString("ID"),se);
							}
							
							try(PreparedStatement stmt1 = conn.prepareStatement("update reportcrontasks set isactive = 0 where id = ?")) {
								
								stmt1.setString(1, rs.getString("ID"));
								stmt1.executeUpdate();
								
							}
						}
					}
				}
			}
			conn.commit();
			
		} catch (SQLException sqle) {
			
			if(logger.isErrorEnabled()) {
				logger.error("Could not initialized report jobs", sqle);
			}
			
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}
		
	}

	private void scheduleSystemJobs(Scheduler sched) throws SchedulerException {
		
		String sql = "select * from crontasks where isactive = 1";
		
		Connection conn = null;
		try {
			conn = Pools.getConnection(Pools.Names.PROJEX);
			
			System.err.println("SCHEDULESYSTEMJOBS");
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				try(ResultSet rs = stmt.executeQuery()) {
					
					while(rs.next()) {
						
						if (logger.isDebugEnabled()) {
							logger.debug("Scheduling System crontask {} {} for {}", rs.getString("ID"), rs.getString("JAVACLASS"), rs.getString("CRONEXPRESSION"));
						}

						try {

							@SuppressWarnings("unchecked")
							Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(
									rs.getString("JAVACLASS"), false,
									Thread.currentThread().getContextClassLoader());

							JobDetail jobDetail = JobBuilder.newJob(jobClass)
									.withIdentity("job" + rs.getString("ID"), "systemgroup").build();

							CronTrigger trigger = TriggerBuilder.newTrigger()
									.withIdentity("trigger" + rs.getString("ID"), "systemgroup")
									.withSchedule(CronScheduleBuilder.cronSchedule(rs.getString("CRONEXPRESSION")))
									.build();

							sched.scheduleJob(jobDetail, trigger);

						} catch (ClassNotFoundException e) {
							
							if(logger.isErrorEnabled()) {
								logger.error("Could not load class", e);
							}
							
						}

					}
				}
			}
			
		} catch(SQLException sqle) {
			
			if(logger.isErrorEnabled()) {
				logger.error("Unable to execute crontask initialization sql", sqle);
			}
			
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}
		
	}

	private void initScheduler(ServletContext context) {

		SchedulerFactory sf = new StdSchedulerFactory();
		try {

			Scheduler sched = sf.getScheduler();

			if (logger.isDebugEnabled()) {
				logger.debug("Starting Quartz scheduler");
			}

			context.setAttribute("SCHEDULER", sched);
			sched.start();
			
			/*

			if ("1".equals(SystemProperties.get("crontasks.enabled"))) {

				InetAddress serverHost = InetAddress.getLocalHost();
				InetAddress crontaskHost = InetAddress.getByName(SystemProperties.get("crontasks.server"));

				if (serverHost.equals(crontaskHost)) {

					scheduleSystemJobs(sched);
					scheduleReportJobs(sched);

				}

			}
			*/

		// } catch (UnknownHostException e) {

			// if (logger.isErrorEnabled()) {
				// logger.error("Unable to get host", e);
		// 	}

		} catch (SchedulerException e) {

			if (logger.isErrorEnabled()) {
				logger.error("Quartz Schedular error", e);
			}

		} catch (Exception e) {

			if (logger.isErrorEnabled()) {
				logger.error("Unexpected Exception in initScheduler", e);
			}

		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {

		if (logger.isDebugEnabled()) {
			logger.debug("Context Listener contextDestroyed Called.");
		}

		IReportEngine engine = (IReportEngine) event.getServletContext().getAttribute("REPORTENGINE");
		if (engine != null) {
			engine.destroy();
		}

		Scheduler sched = (Scheduler) event.getServletContext().getAttribute("SCHEDULER");
		try {
			if (sched != null) {
				sched.shutdown();
			}
		} catch (SchedulerException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not shutdown scheduler");
			}
		}
		
		Pools pools = (Pools) event.getServletContext().getAttribute("POOLS");
		pools.shutdown();

	}
	
	final static Pools pools = new Pools();

	@Override
	public void contextInitialized(ServletContextEvent event) {

		if (logger.isDebugEnabled()) {
			logger.debug("Context Listener contextInitialized Called.");
		}
		
		event.getServletContext().setAttribute("POOLS", pools);

		initReport(event.getServletContext());
		initScheduler(event.getServletContext());

	}

}
