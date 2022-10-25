package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.DayOfWeek;
import com.nextlabs.smartclassifier.constant.ScheduleType;

public class ExecutionFrequencyDTO {
	
	@Expose
	private String time;
	@Expose
	private String minute;
	@Expose
	private String hour;
	@Expose
	private List<String> dayOfMonth;
	@Expose
	private List<String> dayOfWeek;
	
	public ExecutionFrequencyDTO() {
		super();
	}
	
	public ExecutionFrequencyDTO(String scheduleType, String cronExpression) 
			throws IllegalArgumentException {
		super();
		parse(scheduleType, cronExpression);
	}
	
	public void parse(String scheduleType, String cronExpression) {
		if(StringUtils.isNotBlank(scheduleType) && StringUtils.isNotBlank(cronExpression)) {
			String[] timeSegments = cronExpression.split(" ");
			
			if(timeSegments.length != 6) {
				throw new IllegalArgumentException("Invalid cron expression. Segments of value is not equals to 6.");
			}
			
			this.minute = timeSegments[1];
			this.hour = timeSegments[2];
			this.time = ("00" + hour).substring(hour.length()) + ("00" + minute).substring(minute.length());
			if(ScheduleType.MONTHLY.getCode().equalsIgnoreCase(scheduleType)) {
				this.dayOfMonth = Arrays.asList(timeSegments[3].split(","));
			}
			
			if(ScheduleType.WEEKLY.getCode().equalsIgnoreCase(scheduleType)) {
				this.dayOfWeek = Arrays.asList(minusOne(timeSegments[5].split(",")));
			}
		}
	}
	
	public String getCronExpression(String scheduleType) {
		StringBuilder expression = new StringBuilder();
		
		if(StringUtils.isNotBlank(scheduleType)) {
			if(ScheduleType.DAILY.getCode().equalsIgnoreCase(scheduleType)) {
				return "0 " + this.minute + " " + this.hour + " ? * *";
			} else if(ScheduleType.WEEKLY.getCode().equalsIgnoreCase(scheduleType)) {
				return "0 " + this.minute + " " + this.hour + " ? * " + StringUtils.join(plusOne(this.dayOfWeek), ',');
			} else if(ScheduleType.MONTHLY.getCode().equalsIgnoreCase(scheduleType)) {
				return "0 " + this.minute + " " + this.hour + " " + StringUtils.join(this.dayOfMonth, ',') + " * ?";
			}
		}
		
		return expression.toString().trim();
	}
	
	// Special handling for day of week conversion where Quartz using 1-7 for Sun-Sat
	private List<String> plusOne(List<String> days) {
		if(days != null) {
			List<String> newDays = new ArrayList<String>(days.size());
			
			for(String day : days) {
				DayOfWeek dayOfWeek = DayOfWeek.getByShortForm(day);
				
				if(dayOfWeek != null) {
					newDays.add(day);
				} else {
					try {
						newDays.add(Integer.toString(Integer.parseInt(day) + 1));
					} catch(Exception err) {
						// Ignore
					}
				}
			}
			
			return newDays;
		}
		
		return null;
	}
	
	// Special handling for day of week conversion where Quartz using 1-7 for Sun-Sat
	private String[] minusOne(String[] days) {
		if(days != null && days.length > 0) {
			String[] newDays = new String[days.length];
			
			for(int i = 0; i < days.length; i++) {
				DayOfWeek dayOfWeek = DayOfWeek.getByShortForm(days[i]);
				
				if(dayOfWeek != null) {
					newDays[i] = days[i];
				} else {
					try {
						newDays[i] = Integer.toString(Integer.parseInt(days[i]) - 1);
					} catch(Exception err) {
						// Ignore
					}
				}
			}
			
			return newDays;
		}
		
		return null;
	}
	
	private String getMinute(String HHMM) {
		if(StringUtils.isNotBlank(HHMM) && HHMM.length() == 4) {
			if(HHMM.charAt(2) == '0') {
				return HHMM.substring(3);
			} else {
				return HHMM.substring(2);
			}
		}
		
		return "0";
	}
	
	private String getHour(String HHMM) {
		if(StringUtils.isNotBlank(HHMM) && HHMM.length() == 4) {
			if(HHMM.charAt(0) == '0') {
				return HHMM.substring(1, 2);
			} else {
				return HHMM.substring(0, 2);
			}
		}
		
		return "0";
	}
	
	public String getTime() {
		if(this.time != null && this.time.length() > 0 && (this.hour == null || this.minute == null)) {
			this.hour = getHour(this.time);
			this.minute = getMinute(this.time);
		}
		
		this.time = ("00" + this.hour).substring(this.hour.length()) + ("00" + this.minute).substring(this.minute.length());
		return this.time;
	}
	
	public void setTime(String time) {
		this.time = time;
		this.hour = getHour(time);
		this.minute = getMinute(time);
	}
	
	public List<String> getDayOfMonth() {
		return dayOfMonth;
	}
	
	public void setDayOfMonth(List<String> dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	
	public List<String> getDayOfWeek() {
		return dayOfWeek;
	}
	
	public void setDayOfWeek(List<String> dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	public static void main(String[] args) {
		try {
			ExecutionFrequencyDTO dailyDto = new ExecutionFrequencyDTO("D", "0 0 8 ? * *");
			System.out.println("Daily json: " + new Gson().toJson(dailyDto));
			System.out.println("Daily expression: " + dailyDto.getCronExpression("D"));
			
			ExecutionFrequencyDTO weeklyDto = new ExecutionFrequencyDTO("W", "0 15 8 ? * 1,2,3,4,5");
			System.out.println("\nWeekly json: " + new Gson().toJson(weeklyDto));
			System.out.println("Weekly expression: " + weeklyDto.getCronExpression("W"));
			
			ExecutionFrequencyDTO monthlyDto = new ExecutionFrequencyDTO("M", "0 0 8 1,2,3,17,30 * ?");
			System.out.println("\nMonthly json: " + new Gson().toJson(monthlyDto));
			System.out.println("Monthly expression: " + monthlyDto.getCronExpression("M"));
			
			ExecutionFrequencyDTO dailyDto1 = new ExecutionFrequencyDTO();
			dailyDto1.setTime("1315");
			System.out.println("\n\nDaily1 json: " + new Gson().toJson(dailyDto1));
			System.out.println("Daily1 expression: " + dailyDto1.getCronExpression(ScheduleType.DAILY.getCode()));
			
			ExecutionFrequencyDTO weeklyDto1 = new ExecutionFrequencyDTO();
			weeklyDto1.setTime("0905");
			weeklyDto1.setDayOfWeek(Arrays.asList(new String[] {"0", "3", "5", "6"}));
			System.out.println("\nWeekly1 json: " + new Gson().toJson(weeklyDto1));
			System.out.println("Weekly1 expression: " + weeklyDto1.getCronExpression(ScheduleType.WEEKLY.getCode()));
			
			ExecutionFrequencyDTO monthlyDto1 = new ExecutionFrequencyDTO();
			monthlyDto1.setTime("2205");
			monthlyDto1.setDayOfMonth(Arrays.asList(new String[] {"5", "12", "23", "24", "30", "L"}));
			System.out.println("\nMonthly1 json: " + new Gson().toJson(monthlyDto1));
			System.out.println("Monthly1 expression: " + monthlyDto1.getCronExpression(ScheduleType.MONTHLY.getCode()));
		} catch(Exception err) {
			err.printStackTrace();
		}
	}
}
