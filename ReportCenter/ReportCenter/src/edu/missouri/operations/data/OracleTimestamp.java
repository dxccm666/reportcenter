package edu.missouri.operations.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.missouri.operations.reportcenter.Pools;


@SuppressWarnings("serial")
public class OracleTimestamp extends Timestamp {

	public OracleTimestamp(long time) {
		super(time);
	}
	
	public OracleTimestamp(oracle.sql.TIMESTAMP t) throws SQLException {
		super(t.timestampValue().getTime());
	}
	
	public OracleTimestamp(java.sql.Timestamp t) {
		super(t.getTime());
	}
	
	@Override
	public String toString() {
		return Formatter.TIMESTAMP.format(new java.util.Date(getTime()));
	}
	
	public static OracleTimestamp now() {
		
		java.util.Date d = new java.util.Date();
		return new OracleTimestamp(d.getTime());
		
	}
	
	public static OracleTimestamp set(int year, int month, int day, int hourOfDay, int minutes, int seconds) {
	
		// TODO should we use ICU's GregorianCalendar to implement TimeZone and Locale specificity
		
		Calendar c = GregorianCalendar.getInstance();
		c.set(year, month, day, hourOfDay, minutes, seconds);
		return new OracleTimestamp(c.getTime().getTime());
		
	}
	
	public static OracleTimestamp daysFromNow(int days) {
		
		Connection conn = null;
		
		try {
			conn = Pools.getConnection(Pools.Names.REPORTCENTER);
			
			try (PreparedStatement stmt = conn.prepareStatement("select sysdate + ? from dual")) {
				stmt.setInt(1, days);
				
				try (ResultSet rs = stmt.executeQuery()) {
					
					if(rs.next()) {
						return new OracleTimestamp(rs.getTimestamp(1));
					}
				}
				
			}
			
		} catch (SQLException sqle) {
		
			sqle.printStackTrace();
			
		} finally {
			Pools.releaseConnection(Pools.Names.REPORTCENTER, conn);
		}
		
		return null;
		
	}
	
	public OracleTimestamp daysFrom(int days) {
		
		Connection conn = null;
		
		try {
			conn = Pools.getConnection(Pools.Names.REPORTCENTER);
			
			try (PreparedStatement stmt = conn.prepareStatement("select ? + ? from dual")) {
				
				stmt.setTimestamp(1, new java.sql.Timestamp(this.getTime()));
				stmt.setInt(2, days);
				
				try (ResultSet rs = stmt.executeQuery()) {
					
					if(rs.next()) {
						return new OracleTimestamp(rs.getTimestamp(1));
					}
				}
				
			}
			
		} catch (SQLException sqle) {
			
			sqle.printStackTrace();
			
		} finally {
			Pools.releaseConnection(Pools.Names.REPORTCENTER, conn);
		}
		
		return null;
		
	}
	
	public OracleTimestamp lastDayOfMonth() {
		
		Connection conn = null;
		
		try {
			conn = Pools.getConnection(Pools.Names.REPORTCENTER);
			
			try (PreparedStatement stmt = conn.prepareStatement("select last_day(?) from dual")) {
				
				stmt.setTimestamp(1, new java.sql.Timestamp(this.getTime()));
				
				try (ResultSet rs = stmt.executeQuery()) {
					
					if(rs.next()) {
						return new OracleTimestamp(rs.getTimestamp(1));
					}
				}
				
			}
			
		} catch (SQLException sqle) {
			
			sqle.printStackTrace();
			
		} finally {
			Pools.releaseConnection(Pools.Names.REPORTCENTER, conn);
		}
		
		return null;	
	}

}
