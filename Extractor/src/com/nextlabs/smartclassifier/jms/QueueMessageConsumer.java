package com.nextlabs.smartclassifier.jms;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.smartclassifier.constant.JMSType;
import com.nextlabs.smartclassifier.dto.JMSConfig;

public class QueueMessageConsumer {
	
	private static final Logger logger = LogManager.getLogger(QueueMessageConsumer.class);
	
	protected final static IDecryptor decryptor = new ReversibleEncryptor();
	
	// JMS configuration
	private String providerURL;
	private String initialContextFactory;
	private String queueName;
	private long connectionRetryInterval;
	private String jmsUsername;
	private String jmsPassword;
	
	private volatile Context context;
	private volatile Connection connection;
	private volatile Session session;
	private volatile MessageConsumer messageConsumer;
	
	private boolean shutdown;
	
	/**
	 * Creates message consumer for message retrieval.
	 * 
	 * @param jmsConfig JMS configuration details.
	 */
	public QueueMessageConsumer(final JMSConfig jmsConfig) {
		super();
		
		if(jmsConfig == null || !jmsConfig.getType().equals(JMSType.QUEUE)) {
			throw new IllegalArgumentException("JMS configuration is not queue type.");
		}
		
		this.providerURL = jmsConfig.getProviderURL();
		this.initialContextFactory = jmsConfig.getInitialContextFactory();
		this.queueName = jmsConfig.getServiceName();
		this.connectionRetryInterval = jmsConfig.getConnectionRetryInterval() > 0 ? jmsConfig.getConnectionRetryInterval() : 5L;
		this.jmsUsername = jmsConfig.getUsername();
		this.jmsPassword = jmsConfig.getPassword();
		
		this.shutdown = false;
	}
	
	/**
	 * Retrieve message from JMS queue with given wait timeout
	 * 
	 * @param timeout Duration to wait before conclude there is not message to receive
	 * @return TextMessage from JMS queue or null when there is no message to receive
	 * @throws JMSException Error when accessing to JMS queue
	 */
	public TextMessage retrieveMessage(long timeout) 
			throws JMSException {
		logger.debug("Getting message from queue.");
		
		try {
			return (TextMessage)messageConsumer.receive(timeout);
		} catch(JMSException err) {
			logger.error(err.getMessage(), err);
			connectToJMS();
		}
		
		return null;
	}
	
	/**
	 * Update JMS configuration.
	 * 
	 * @param jmsConfig JMS configuration information
	 */
	public void updateJMSConfig(final JMSConfig jmsConfig) {
		if(jmsConfig == null || !jmsConfig.getType().equals(JMSType.QUEUE)) {
			throw new IllegalArgumentException("JMS configuration is not queue type.");
		}
		
		this.providerURL = jmsConfig.getProviderURL();
		this.initialContextFactory = jmsConfig.getInitialContextFactory();
		this.queueName = jmsConfig.getServiceName();
		this.connectionRetryInterval = jmsConfig.getConnectionRetryInterval() > 0 ? jmsConfig.getConnectionRetryInterval() : 5L;
		
		// Close current connection, new connection will be created with updated JMS configuration during next message sending.
		close();
	}
	
	/**
	 * Set shutdown flag to true and quit re-connect to the JMS.
	 */
	public void shutdown() {
		this.shutdown = true;
	}
	
	/**
	 * Close JMS message consumer, session and connection
	 */
	public void close() {
		try {
			if(messageConsumer != null) {
				try {
					messageConsumer.close();
					messageConsumer = null;
				} catch(JMSException err) {
					// Ignore
				}
			}
			
			if(session != null) {
				try {
					session.close();
					session = null;
				} catch(JMSException err) {
					// Ignore
				}
			}
			
			if(connection != null) {
				try {
					connection.close();
					connection = null;
				} catch(JMSException err) {
					// Ignore
				}
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
	}
	
	/**
	 * Try to connect to JMS for one time
	 * <P>Each failed attempt will be retry after X seconds later.
	 */
	public void connectToJMS() {
		boolean connected = false;
		
		while(!(connected || shutdown)) {
			try {
				try {
					connected = createJMSConnection();
				} catch(CommunicationException err) {
					// Ignore
				}
				
				if(!connected) {
					logger.info("Failed to create JMS connection, retry after " + (connectionRetryInterval) + " seconds.");
					Thread.sleep(connectionRetryInterval * 1000);
				}
			} catch(Exception err) {
				logger.error(err.getMessage(), err);
			}
		}
	}
	
	/**
	 * Override hashCode() implementation of object class.
	 * 
	 * @return hash code of the object.
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(providerURL)
										  .append(initialContextFactory)
										  .append(queueName)
										  .append(connectionRetryInterval)
										  .toHashCode();
	}
	
	/**
	 * Override equals(obj) implementation of object class.
	 * 
	 * @param object to compare with
	 * @return true if equals
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof QueueMessageConsumer)) {
			return false;
		}
		
		if(obj == this) {
			return true;
		}
		
		QueueMessageConsumer rhs = (QueueMessageConsumer)obj;
		return new EqualsBuilder().append(providerURL, rhs.providerURL)
								  .append(initialContextFactory, rhs.initialContextFactory)
								  .append(queueName, rhs.queueName)
								  .append(connectionRetryInterval, rhs.connectionRetryInterval)
								  .isEquals();
	}
	
	/**
	 * Implements the JMS context lookup and connection creation.
	 * 
	 * @return <tt>true</tt> if JMS connection created successfully
	 * @throws NamingException if the context lookup failed
	 * @throws JMSException if the JMS connection failed to create
	 * @see #connectToJMS()
	 */
	private boolean createJMSConnection()
			throws NamingException, JMSException {
		try {
			context = null;
			connection = null;
			session = null;
			
			logger.info("Creating JMS connection.");
	    	// Step 1. Create an initial context to perform the JNDI lookup.
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, this.initialContextFactory);
            env.put(Context.PROVIDER_URL, this.providerURL);
            env.put("queue.queues/".concat(this.queueName), this.queueName);
	    	logger.info("JMS environment is " + env.toString());
	    	context = new InitialContext(env);
	    	
	    	// Step 2. Lookup the connection factory
	    	ConnectionFactory factory = (ConnectionFactory)context.lookup("ConnectionFactory");
	    	logger.info("JMS connection factory is " + factory.toString());
	    	
	    	// Step 3. Lookup the JMS queue
	    	Queue queue = (Queue)context.lookup("queues/".concat(this.queueName));
	    	logger.info("JMS queue is " + queue.toString());
	    	
	    	// Step 4. Create the JMS objects to connect to the server and manage a session
	    	if(StringUtils.isNotBlank(this.jmsUsername) && StringUtils.isNotBlank(this.jmsPassword)) {
	    		connection = factory.createConnection(this.jmsUsername, decryptor.decrypt(this.jmsPassword));
	    	} else {
	    		connection = factory.createConnection();
	    	}
	    	
	    	session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    	
	    	// Step 5. Create a JMS Message Consumer to send a message on the queue
	    	messageConsumer = session.createConsumer(queue);
	    	connection.start();
	    	logger.info("JMS connection created successfully.");
			
	    	return true;
		} catch(NameNotFoundException | JMSException err) {
			close();
		}
		
		return false;
	}
}
