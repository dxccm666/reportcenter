package edu.missouri.operations.data;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressWarnings("serial")
public class OracleDate extends Date {

	public OracleDate(long date) {
		super(date);
	}
	
	@Override
	public String toString() {
		return Formatter.DATE.format(new java.util.Date(getTime()));
	}
	
	public static OracleDate now() {
		java.util.Date d = new java.util.Date();
		return new OracleDate(d.getTime());
	}
	
	public static OracleDate set(int year, int month, int day, int hourOfDay, int minutes, int seconds) {
		
		// TODO should we use ICU's GregorianCalendar to implement TimeZone and Locale specificity
		
		Calendar c = GregorianCalendar.getInstance();
		c.set(year, month, day, hourOfDay, minutes, seconds);
		return new OracleDate(c.getTime().getTime());
		
	}
	
	public static OracleDate yearsFromNow(int years) {
		
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(new java.util.Date());
		
		int year = c.get(Calendar.YEAR) + years;
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int seconds = c.get(Calendar.SECOND);
		
		c.set(year, month, day, hourOfDay, minutes, seconds);
		return new OracleDate(c.getTime().getTime());
		
	}
	
	public static OracleDate daysFromNow(int days) {
		
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(new java.util.Date());
		c.add(Calendar.DATE, days);
		return new OracleDate(c.getTime().getTime());
		
	}
	
	public OracleDate daysFrom(int days) {
		
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(new java.util.Date(getTime()));
		c.add(Calendar.DATE, days);
		return new OracleDate(c.getTime().getTime());
		
	}
	
	
	public static OracleDate today() {
		
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(new java.util.Date());
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hourOfDay = 23;
		int minutes = 59;
		int seconds = 59;
		
		c.set(year, month, day, hourOfDay, minutes, seconds);
		return new OracleDate(c.getTime().getTime());	
		
	}
}
