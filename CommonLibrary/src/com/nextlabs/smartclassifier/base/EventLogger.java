package com.nextlabs.smartclassifier.base;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.constant.Punctuation;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.manager.EventLogManager;
import com.nextlabs.smartclassifier.dto.Event;
import com.nextlabs.smartclassifier.exception.ManagerException;

public class EventLogger {
	
	private static final Logger logger = LogManager.getLogger(EventLogger.class);
	
	private Object notifier;
	private Component component;
	private Queue<Event> eventQueue;
	private Thread eventCommitThread;
	
	public EventLogger(Component component) {
		super();
		this.notifier = new Object();
		this.component = component;
		this.eventQueue = new ConcurrentLinkedQueue<Event>();
		this.eventCommitThread = new EventCommitThread(notifier, component, eventQueue);
		this.eventCommitThread.start();
		
		logger.debug("Event commit task started.");
	}
	
	public void log(Event event) {
		event.setComponentName(component.getName());
		
		if(StringUtils.isNotBlank(event.getFilePath())
				&& !event.getFilePath().endsWith(Punctuation.FORWARD_SLASH)) {
			event.setFilePath(event.getFilePath() + Punctuation.FORWARD_SLASH);
		}
		
		// Handle for URL encoded value
		if(RepositoryType.SHAREPOINT.getName().equals(event.getRepositoryType())
				&& event.getFilePath().startsWith("http")) {
			try {
				event.setFilePath(URLDecoder.decode(event.getFilePath(), "UTF-8"));
			} catch(Exception err) {
				// Ignore
			}
		}
		
		eventQueue.add(event);
		
		if(eventQueue.size() >= 25 || component.isShuttingDown()) {
			synchronized(notifier) {
				notifier.notify();
			}
		}
	}
	
	public boolean isAlive() {
		return eventCommitThread.isAlive();
	}
}

class EventCommitThread
	extends Thread {
	
	private static final Logger logger = LogManager.getLogger(EventCommitThread.class);
	
	// Wait until notify or 5 seconds (max)
	private static final int WAIT_DURATION = 5000;
	
	private Object notifier;
	private Component component;
	private Queue<Event> blockingQueue;
	
	public EventCommitThread(Object notifier, Component component, Queue<Event> blockingQueue) {
		super("EventCommitThread");
		this.notifier = notifier;
		this.component = component;
		this.blockingQueue = blockingQueue;
	}
	
	@Override
	public void run() {
		while(!component.isShuttingDown()) {
			try {
				synchronized(notifier) {
					logger.debug("Waiting notifier for event log commit action...");
					notifier.wait(WAIT_DURATION);
				}
				
				if(blockingQueue.size() > 0) {
					Session session = null;
					Transaction transaction = null;
					
					try {
						List<Event> events = new ArrayList<Event>();
						
						for(int recordCount = 0; recordCount <= 50; recordCount ++) {
							Event event = blockingQueue.poll();
							
							if(event == null) {
								break;
							}
							
							events.add(event);
						}
						
						if(events.size() > 0) {
							logger.debug("Committing event log into database.");
							session = component.getSessionFactory().openSession();
							transaction = session.beginTransaction();
							
							EventLogManager eventLogManager = new EventLogManager(component.getSessionFactory(), session);
							eventLogManager.logEvents(events);
							
							transaction.commit();
							logger.debug("Event log committed into database.");
						}
					} catch(ManagerException | Exception err) {
						if(transaction != null) {
							try {
								transaction.rollback();
							} catch(Exception rollbackErr) {
								logger.error(rollbackErr.getMessage(), rollbackErr);
							}
						}
						logger.error(err.getMessage(), err);
					} finally {
						if(session != null) {
							try {
								session.close();
							} catch(HibernateException err) {
								// Ignore
							}
						}
					}
				} else {
					logger.debug("No event log to commit.");
				}
			} catch(Exception err) {
				logger.error(err.getMessage(), err);
			}
		}
		
		logger.info("Event commit thread has stopped.");
	}
}
