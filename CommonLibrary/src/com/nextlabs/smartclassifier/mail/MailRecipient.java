package com.nextlabs.smartclassifier.mail;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

public class MailRecipient {
	
	private RecipientType recipientType;
	
	private InternetAddress address;
	
	public MailRecipient(RecipientType recipientType, InternetAddress address) {
		super();
		this.recipientType = recipientType;
		this.address = address;
	}
	
	public RecipientType getRecipientType() {
		return recipientType;
	}
	
	public void setRecipientType(RecipientType recipientType) {
		this.recipientType = recipientType;
	}
	
	public InternetAddress getAddress() {
		return address;
	}
	
	public void setAddress(InternetAddress address) {
		this.address = address;
	}
}
