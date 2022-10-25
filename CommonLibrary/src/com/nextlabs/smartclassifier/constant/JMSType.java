package com.nextlabs.smartclassifier.constant;

public enum JMSType {
	
	QUEUE("Q", "Queue"),
	TOPIC("T", "Topic");
	
	private String code;
	
	private String name;
	
	private JMSType(final String code, final String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public static JMSType getType(final String typeCode) {
		if(typeCode != null) {
			for(JMSType jms : JMSType.values()) {
				if(jms.getCode().equals(typeCode)) {
					return jms;
				}
			}
		}
		
		return null;
	}
}
