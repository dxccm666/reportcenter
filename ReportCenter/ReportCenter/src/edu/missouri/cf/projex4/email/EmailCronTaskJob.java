package edu.missouri.cf.projex4.email;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class EmailCronTaskJob implements Job{
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt_insert = null;
		PreparedStatement pstmt_update_email = null;
		
		ResultSet rs = null;
		String sql = "select distinct a.id, a.imnvitationcode,a.emailaddress, a.issend, a.senddate," +
				"b.emailname, b.emailtype,b.content " +
				"from emaildetails a inner join emailcontent b on a.emailid = b.emailid " +
				"where issend = 0 or senddate is null";
		
		String sql_insert = "insert into schedulerlog (id, timestamp, jobid,jobname,isscheduled,scheduleddate, iscompleted," +
				"completeddate) values (SCHEDULERLOGGERSSEQ.nextval,'AAAA',?,?,1,sysdate,?,sysdate))";
		
		String sql_update_email = "update emaildetails set issend = 1, senddate = sysdate where id = ?";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			conn = DriverManager.getConnection(
							"jdbc:oracle:thin:@//128.206.191.134:1521/projex4XDB.devdb.cf.missouri.edu",
							"projex4", "prj4_user");

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
								
				String emailAddress = rs.getString("EMAILADDRESS");
				String emailname = rs.getString("EMAILNAME");
				String emailtype = rs.getString("EMAILTYPE");
				String emailDetail_id = rs.getString("ID");
				
				System.err.println("Email is = " + emailAddress);
				
					try {
						ReadTemplate rt = new ReadTemplate();
						String emailContent = rt.readTemplate("/home/hrapp/autoemail/index.html");						

						Map<String, String> map = new HashMap<String, String>();
						map.put("smtp", "smtpinternal.missouri.edu");
						map.put("protocol", "smtp");
						map.put("username", "mucfsdsfw@missouri.edu");
						map.put("password", "crmc!94PGE");
						map.put("from", "muhrsautomation@missouri.edu");
						map.put("to", emailAddress);
						map.put("subject",emailname);
						map.put("body", emailContent);

						Map<String, String> image = new HashMap<String, String>();						

						List<String> list = new ArrayList<String>();
						SendMail sm = new SendMail(map, list, image);
						sm.send();
						
						pstmt_insert = conn.prepareStatement(sql_insert);
						pstmt_insert.setString(1, " ");
						pstmt_insert.setString(2, emailtype);
						pstmt_insert.setInt(3, 1);										
															
						pstmt_insert.executeUpdate();	
						pstmt_insert.close();
						
						pstmt_update_email = conn.prepareStatement(sql_update_email);
						pstmt_update_email.setString(1, emailDetail_id);
						pstmt_update_email.executeUpdate();
						pstmt_update_email.close();
						
					} catch (Exception e) {
						e.printStackTrace();
						//sendErrorRecept();
					}
				
				
			}				
								
			logger.error("Send email successfully at:  " + new Date());
			
			rs.close();
			pstmt.close();
			conn.commit();
			conn.close();
			logger.error("Close connection successfully");			
			
		} catch (UnsupportedOperationException e) {
			
		} catch (SQLException e) {
			
		} catch (InstantiationException e) {
			
		} catch (IllegalAccessException e) {
			
		} catch (ClassNotFoundException e) {
			
		}

	
		
	}

}
