package com.nextlabs.smartclassifier.mail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

public class Mail {
	
	private String subject;
	private String content;
	private String contentType;
	private InternetAddress sender;
	private List<MailRecipient> recipients;
	private String attachement;
	
	

	public Mail() {
		super();
		this.recipients = new ArrayList<MailRecipient>();
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public InternetAddress getSender() {
		return sender;
	}
	
	public void setSender(InternetAddress sender) {
		this.sender = sender;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public List<MailRecipient> getRecipients() {
		return recipients;
	}
	
	public void setRecipients(List<MailRecipient> recipients) {
		this.recipients = recipients;
	}
	
	public String getAttachement() {
		return attachement;
	}

	public void setAttachement(String attachement) {
		this.attachement = attachement;
	}
}
