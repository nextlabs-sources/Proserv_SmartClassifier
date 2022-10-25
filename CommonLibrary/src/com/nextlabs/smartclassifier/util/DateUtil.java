package com.nextlabs.smartclassifier.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
	
	public static final String DF_DEFAULT = "dd-MMMM-yyyy HH:mm:ss";
	public static final String DF_DDMMMYYHHMMSS = "dd-MMM-yy HH:mm:ss";
	public static final String DF_YYYYMMDD = "yyyyMMdd";
	public static final String DF_DDMMMMYYYY = "dd-MMMM-yyyy";
	
	public static final long secondsInMilli = 1000;
	public static final long minutesInMilli = secondsInMilli * 60;
	public static final long hoursInMilli = minutesInMilli * 60;
	public static final long daysInMilli = hoursInMilli * 24;
	
	public static String format(Long datetime, String dateFormat) {
		if(datetime != null && dateFormat != null) {
			return new SimpleDateFormat(dateFormat).format(new Date(datetime));
		}
		
		return null;
	}
	
	public static String format(Date date, String dateFormat) {
		if(date != null && dateFormat != null) {
			return new SimpleDateFormat(dateFormat).format(date);
		}
		
		return null;
	}
	
	public static Date toDate(String date, String dateFormat) 
			throws ParseException {
		if(date != null && dateFormat != null) {
			try {
				return new SimpleDateFormat(dateFormat).parse(date);
			} catch(ParseException err) {
				throw err;
			}
		}
		
		return null;
	}
	
	public static long toStartOfTheDay(Date date) {
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(date);
		startTime.set(Calendar.HOUR_OF_DAY, 0);
		startTime.set(Calendar.MINUTE, 0);
		startTime.set(Calendar.SECOND, 0);
		startTime.set(Calendar.MILLISECOND, 0);		
		
		return startTime.getTimeInMillis();
	}
	
	public static Date toStartOfTheDay(long dateInMilliseconds) {
		Calendar startTime = Calendar.getInstance();
		startTime.setTimeInMillis(dateInMilliseconds);
		startTime.set(Calendar.HOUR_OF_DAY, 0);
		startTime.set(Calendar.MINUTE, 0);
		startTime.set(Calendar.SECOND, 0);
		startTime.set(Calendar.MILLISECOND, 0);		
		
		return new Date(startTime.getTimeInMillis());
	}
	
	public static long toEndOfTheDay(Date date) {
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(date);
		endTime.set(Calendar.HOUR_OF_DAY, 23);
		endTime.set(Calendar.MINUTE, 59);
		endTime.set(Calendar.SECOND, 59);
		endTime.set(Calendar.MILLISECOND, 999);
		
		return endTime.getTimeInMillis();
	}
	
	public static Date toEndOfTheDay(long dateInMilliseconds) {
		Calendar endTime = Calendar.getInstance();
		endTime.setTimeInMillis(dateInMilliseconds);
		endTime.set(Calendar.HOUR_OF_DAY, 23);
		endTime.set(Calendar.MINUTE, 59);
		endTime.set(Calendar.SECOND, 59);
		endTime.set(Calendar.MILLISECOND, 999);
		
		return new Date(endTime.getTimeInMillis());
	}
	
	public static Date addDays(Date date, int days) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		
		return calendar.getTime();
	}
	
	public static Date subtractDays(Date date, int days) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -days);
		
		return calendar.getTime();
	}
	
	public static Date addMonths(Date date, int months) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		
		return calendar.getTime();
	}
	
	public static Date subtractMonths(Date date, int months) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -months);
		
		return calendar.getTime();
	}
	
	public static Date addYears(Date date, int years) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, years);
		
		return calendar.getTime();
	}
	
	public static Date subtractYears(Date date, int years) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -years);
		
		return calendar.getTime();
	}
	
	public static String getTimeElapsed(Date startDate, Date endDate) {
		if(startDate == null) {
			return "Start date is null.";
		}
		
		if(endDate == null) {
			return "End date is null.";
		}
		
		return getTimeElapsed(startDate.getTime(), endDate.getTime());
	}
	
	public static String getTimeElapsed(long startTime, long endTime) {
		if(endTime >= startTime) {
			long different = endTime - startTime;
			
			long elapsedDays = different / daysInMilli;
			different %= daysInMilli;
			
			long elapsedHours = different / hoursInMilli;
			different %= hoursInMilli;
			
			long elapsedMinutes = different / minutesInMilli;
			different %= minutesInMilli;
			
			long elapsedSeconds = different / secondsInMilli;
			
			return elapsedDays + " days, " + elapsedHours + " hours, " + elapsedMinutes + " minutes, " + elapsedSeconds + " seconds";
		}
		
		return "End time is before start time.";
	}
}
