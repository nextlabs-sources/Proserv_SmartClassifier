package com.nextlabs.smartclassifier.constant;

public enum DayOfWeek {
	
	SUNDAY("0", "SUN", "Sunday"),
	MONDAY("1", "MON", "Monday"),
	TUESDAY("2", "TUE", "Tuesday"),
	WEDNESDAY("3", "WED", "Wednesday"),
	THURSDAY("4", "THU", "Thursday"),
	FRIDAY("5", "FRI", "Friday"),
	SATURDAY("6", "SAT", "Saturday");
	
	private String code;
	
	private String shortForm;
	
	private String name;
	
	private DayOfWeek(String code, String shortForm, String name) {
		this.code = code;
		this.shortForm = shortForm;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getShortForm() {
		return shortForm;
	}
	
	public String getName() {
		return name;
	}
	
	public static DayOfWeek getDayOfWeek(String code) {
		if(code != null) {
			for(DayOfWeek day : DayOfWeek.values()) {
				if(day.getCode().equals(code)) {
					return day;
				}
			}
		}
		
		return null;
	}
	
	public static DayOfWeek getByShortForm(String shortForm) {
		if(shortForm != null) {
			for(DayOfWeek day : DayOfWeek.values()) {
				if(day.getShortForm().equalsIgnoreCase(shortForm)) {
					return day;
				}
			}
		}
		
		return null;
	}
}
