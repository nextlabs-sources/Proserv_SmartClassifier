package com.nextlabs.smartclassifier.jms;

import static com.nextlabs.smartclassifier.constant.NextLabsConstant.ACTION_DELETE;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
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
import org.apache.commons.vfs2.FileObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.smartclassifier.base.Component;
import com.nextlabs.smartclassifier.constant.EventCategory;
import com.nextlabs.smartclassifier.constant.EventStage;
import com.nextlabs.smartclassifier.constant.EventStatus;
import com.nextlabs.smartclassifier.constant.JMSType;
import com.nextlabs.smartclassifier.constant.MessageFieldName;
import com.nextlabs.smartclassifier.constant.NextLabsConstant;
import com.nextlabs.smartclassifier.constant.ReportEvent;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.dto.Event;
import com.nextlabs.smartclassifier.dto.JMSConfig;
import com.nextlabs.smartclassifier.util.VFSUtil;


/**
 * This class consist of a JMS connection management and message sending implementation to HornetQ
 * in Indexer program.
 * <p>
 * <p>
 * <p>
 * <p>JMS configuration need to be provide via NextLabsSystemConfig object.
 * <p>
 * <p>
 * <p>
 * <p>Watcher threads share this object to publish message
 *
 * @author schok
 */
public class QueueMessageSender {

    private static final Logger logger = LogManager.getLogger(QueueMessageSender.class);

    protected final static IDecryptor decryptor = new ReversibleEncryptor();
    
    private Component component;
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
    private volatile MessageProducer messageProducer;

    private boolean shutdown;

    /**
     * Creates message producer for message sending.
     *
     * @param jmsConfig JMS configuration details.
     */
    public QueueMessageSender(final Component component, final JMSConfig jmsConfig) {
        super();

        this.component = component;
        if (jmsConfig == null || !jmsConfig.getType().equals(JMSType.QUEUE)) {
            throw new IllegalArgumentException("JMS configuration is not queue type.");
        }

        this.providerURL = jmsConfig.getProviderURL();
        this.initialContextFactory = jmsConfig.getInitialContextFactory();
        this.queueName = jmsConfig.getServiceName();
        this.connectionRetryInterval =
                jmsConfig.getConnectionRetryInterval() > 0 ? jmsConfig.getConnectionRetryInterval() : 5L;
        this.jmsUsername = jmsConfig.getUsername();
        this.jmsPassword = jmsConfig.getPassword();
                
        this.shutdown = false;
    }

    public synchronized void send(
            FileObject file,
            String action,
            RepositoryType repositoryType,
            String siteURL,
            String repoFolderPath) {
        boolean sent = false;

        if(file == null) {
        	return;
        }
        
        while (!sent && !shutdown) {
            try {
                String documentId = "0-0";
                String absoluteFilePath = VFSUtil.getAbsoluteFilePath(file);

                TextMessage message = session.createTextMessage();
                message.setStringProperty(MessageFieldName.ID, documentId);
                message.setStringProperty(MessageFieldName.FILE_NAME, file.toString());
                message.setStringProperty(MessageFieldName.FILE_TYPE, repositoryType.getName());
                message.setStringProperty(MessageFieldName.SITE_URL, siteURL);
                message.setStringProperty(MessageFieldName.ACTION, action);
                message.setStringProperty(MessageFieldName.QUEUE_NAME, file.toString());
                message.setStringProperty(MessageFieldName.REPO_FOLDER_PATH, repoFolderPath);

                logger.debug("Before Send the " + message.getStringProperty(MessageFieldName.FILE_NAME) + " to the queue");
                messageProducer.send(message);
                logger.debug("Sent the " + message.getStringProperty(MessageFieldName.FILE_NAME) + " to the queue");
                sent = true;

                logDocumentEvent(documentId, repositoryType.getName(), absoluteFilePath, action);
            } catch (JMSException err) {
                logger.error(err.getMessage(), err);
                connectToJMS();
            } catch (Exception err) {
                logger.error(err.getMessage(), err);
                sent = true;
            }
        }
    }

    /**
     * Update JMS configuration.
     *
     * @param jmsConfig JMS configuration information
     */
    public void updateJMSConfig(final JMSConfig jmsConfig) {
        if (jmsConfig == null || !jmsConfig.getType().equals(JMSType.QUEUE)) {
            throw new IllegalArgumentException("JMS configuration is not queue type.");
        }

        this.providerURL = jmsConfig.getProviderURL();
        this.initialContextFactory = jmsConfig.getInitialContextFactory();
        this.queueName = jmsConfig.getServiceName();
        this.connectionRetryInterval =
                jmsConfig.getConnectionRetryInterval() > 0 ? jmsConfig.getConnectionRetryInterval() : 5L;

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
     * Close JMS message producer, session and connection
     */
    public void close() {
        try {
            logger.info("Closing JMS connection, if any.");
            if (messageProducer != null) {
                try {
                    messageProducer.close();
                    messageProducer = null;
                } catch (JMSException err) {
                    // Ignore
                }
            }

            if (session != null) {
                try {
                    session.close();
                    session = null;
                } catch (JMSException err) {
                    // Ignore
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (JMSException err) {
                    // Ignore
                }
            }
            logger.info("JMS connection closed successfully.");
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }
    }

    /**
     * Keeps connect to JMS as provide in JMS configuration until success. Each failed attempt will be
     * retry after X seconds later.
     */
    public void connectToJMS() {
        boolean connected = false;

        while (!(connected || shutdown)) {
            try {
                try {
                    connected = createJMSConnection();
                } catch (CommunicationException err) {
                    // Ignore printing
                }

                if (!connected) {
                    logger.info(
                            "Failed to create JMS connection, retry after "
                                    + (connectionRetryInterval)
                                    + " seconds.");
                    Thread.sleep(connectionRetryInterval * 1000);
                }
            } catch (Exception err) {
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
        return new HashCodeBuilder(17, 31)
                .append(providerURL)
                .append(initialContextFactory)
                .append(queueName)
                .append(connectionRetryInterval)
                .toHashCode();
    }

    /**
     * Override equals(obj) implementation of object class.
     *
     * @param obj to compare with
     * @return true if equals
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QueueMessageSender)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        QueueMessageSender rhs = (QueueMessageSender) obj;
        return new EqualsBuilder()
                .append(providerURL, rhs.providerURL)
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
     * @throws JMSException    if the JMS connection failed to create
     * @see #connectToJMS()
     */
    private boolean createJMSConnection() throws NamingException, JMSException {
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
            ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
            logger.info("JMS connection factory is " + factory.toString());

            // Step 3. Lookup the JMS queue
            Queue queue = (Queue) context.lookup("queues/".concat(this.queueName));
            logger.info("JMS queue is " + queue.toString());

            // Step 4. Create the JMS objects to connect to the server and manage a session
            if(StringUtils.isNotBlank(this.jmsUsername) && StringUtils.isNotBlank(this.jmsPassword)) {
            	connection = factory.createConnection(this.jmsUsername, decryptor.decrypt(this.jmsPassword));
            } else {
            	connection = factory.createConnection();	
            }
            
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Step 5. Create a JMS Message Producer to send a message on the queue
            messageProducer = session.createProducer(queue);
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            connection.start();
            logger.info("JMS connection created successfully.");

            return true;
        } catch (NameNotFoundException | JMSException err) {
            close();
        }

        return false;
    }

    /**
     * Perform document monitoring event logging
     *
     * @param action       Document action type
     * @param absolutePath Document absolute path
     */
    private void logDocumentEvent(
            String documentId, String repositoryType, String absolutePath, String action) {

        if (NextLabsConstant.ACTION_ADD.equals(action)) {
            if (Boolean.valueOf(component.getSystemConfig(SystemConfigKey.ENABLE_DOCUMENT_ADDED_LOG))) {
                try {
                    Event documentEvent = new Event();
                    documentEvent.setStage(EventStage.FILE_MONITORING);
                    documentEvent.setFileId(documentId);
                    documentEvent.setRepositoryType(repositoryType);
                    documentEvent.setAbsolutePath(absolutePath, RepositoryType.getRepositoryType(repositoryType));
                    documentEvent.setCategory(EventCategory.OPERATION);
                    documentEvent.setStatus(EventStatus.SUCCESS);
                    documentEvent.setMessageCode(ReportEvent.DOCUMENT_ADD.getMessageCode());
                    documentEvent.setTimestamp(System.currentTimeMillis());

                    component.log(documentEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        } else if (NextLabsConstant.ACTION_UPDATE.equals(action)) {
            if (Boolean.valueOf(component.getSystemConfig(SystemConfigKey.ENABLE_DOCUMENT_UPDATED_LOG))) {
                try {
                    Event documentEvent = new Event();
                    documentEvent.setStage(EventStage.FILE_MONITORING);
                    documentEvent.setFileId(documentId);
                    documentEvent.setRepositoryType(repositoryType);
                    documentEvent.setAbsolutePath(absolutePath, RepositoryType.getRepositoryType(repositoryType));
                    documentEvent.setCategory(EventCategory.OPERATION);
                    documentEvent.setStatus(EventStatus.SUCCESS);
                    documentEvent.setMessageCode(ReportEvent.DOCUMENT_UPDATE.getMessageCode());
                    documentEvent.setTimestamp(System.currentTimeMillis());

                    component.log(documentEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        } else if (ACTION_DELETE.equals(action)) {
            if (Boolean.valueOf(
                    component.getSystemConfig(SystemConfigKey.ENABLE_DOCOUMENT_REMOVED_LOG))) {
                try {
                    Event documentEvent = new Event();
                    documentEvent.setStage(EventStage.FILE_MONITORING);
                    documentEvent.setFileId(documentId);
                    documentEvent.setRepositoryType(repositoryType);
                    documentEvent.setAbsolutePath(absolutePath, RepositoryType.getRepositoryType(repositoryType));
                    documentEvent.setCategory(EventCategory.OPERATION);
                    documentEvent.setStatus(EventStatus.SUCCESS);
                    documentEvent.setMessageCode(ReportEvent.DOCUMENT_REMOVE.getMessageCode());
                    documentEvent.setTimestamp(System.currentTimeMillis());

                    component.log(documentEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        }
    }

}
