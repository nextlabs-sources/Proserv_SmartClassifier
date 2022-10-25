package com.nextlabs.smartclassifier.mail;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.mail.util.MailSSLSocketFactory;

public class MailService {
	
	private static final Logger logger = LogManager.getLogger(MailService.class);
	
	private static Properties properties;
	private static MailServiceConfig mailServiceConfig;
	
	/**
	 * Initialize mail service by providing mail server configuration information.
	 * 
	 * @param config Mail server configuration.
	 * @throws Exception All exceptions will not be handle and failure of initialization will abort the system.
	 */
	public static void init(MailServiceConfig config) 
			throws Exception {
		properties = System.getProperties();
		mailServiceConfig = config;
		
		properties.setProperty("mail.debug", config.getDebug());
		properties.setProperty("mail.smtp.host", config.getHost());
		properties.setProperty("mail.smtp.port", config.getPort());
		properties.setProperty("mail.smtp.auth", config.getAuthentication());
		properties.setProperty("mail.smtp.starttls.enable", config.getStartTLS());
		properties.setProperty("mail.smtp.ssl.checkserveridentity", config.getCheckServerIdentity());
		
		MailSSLSocketFactory sslSocketFactory = new MailSSLSocketFactory();
		sslSocketFactory.setTrustAllHosts(Boolean.valueOf(config.getCheckServerIdentity()));
		properties.put("mail.smtp.ssl.socketFactory", sslSocketFactory);
	}
	
	/**
	 * Send mail action using initialized mail server.
	 * 
	 * @param mail Mail information which contains sender, recipient, subject and content.
	 * @return <true> when sent successfully. <false> when encounter exception.
	 */
	public static boolean sendMail(Mail mail) {
		if(mail != null) {
			Session session = Session.getDefaultInstance(properties);
			Transport transport = null;
			
			try {
				Message message = new MimeMessage(session);
				
				message.setFrom(mail.getSender() == null ? mailServiceConfig.getSender() : mail.getSender());
				for(MailRecipient recipient : mail.getRecipients()) {
					message.addRecipient(recipient.getRecipientType(), recipient.getAddress());
				}
				message.setSubject(mail.getSubject());
				
				if (mail.getAttachement() != null) {
					// Create the message part
					BodyPart messageBodyPart = new MimeBodyPart();

					// Now set the actual message
					messageBodyPart.setText(mail.getContent());

					// Create a multipar message
					Multipart multipart = new MimeMultipart();

					// Set text message part
					multipart.addBodyPart(messageBodyPart);

					// Part two is attachment
					messageBodyPart = new MimeBodyPart();
					String filename = mail.getAttachement();
					DataSource source = new FileDataSource(filename);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(filename);
					multipart.addBodyPart(messageBodyPart);

					// Send the complete message parts
					message.setContent(multipart);

				} else {
					message.setContent(mail.getContent(),(mail.getContentType() == null) ? "text/html" : mail.getContentType());
				}
				
				
				transport = session.getTransport("smtp");
				if(mailServiceConfig.getAuthentication().equals("true")) {
					transport.connect(mailServiceConfig.getHost(), mailServiceConfig.getUsername(), mailServiceConfig.getPassword());
				} else {
					transport.connect(mailServiceConfig.getHost(), null, null);
				}
				transport.sendMessage(message, message.getAllRecipients());
				
				return true;
			} catch(MessagingException err) {
				logger.error(err.getMessage(), err);
			} finally {
				if(transport != null) {
					try {
						transport.close();
					} catch(MessagingException err) {
						// Ignore
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Get the mail service configuration of this service.
	 * 
	 * @return MailServiceConfig object used to initialize this service.
	 */
	public static MailServiceConfig getMailServiceConfig() {
		return mailServiceConfig;
	}
}
