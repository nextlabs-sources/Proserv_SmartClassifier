package com.nextlabs.smartclassifier.job;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.util.DateUtil;

public class PurgeEventLogJob
		implements Job {
	
	private static final Logger logger = LogManager.getLogger(PurgeEventLogJob.class);
	
	private static final String LAST_ARCHIVED_TIMESTAMP = "SELECT MAX(TIMESTAMP) FROM EVENT_LOG_ARCHIVES";
	private static final String INSERT_ARCHIVE_SQL = "INSERT INTO EVENT_LOG_ARCHIVES SELECT * FROM EVENT_LOGS WHERE TIMESTAMP > ? AND TIMESTAMP < ?";
	private static final String PURGE_LIVE_SQL = "DELETE FROM EVENT_LOGS WHERE TIMESTAMP <= ?";
	
	@Override
	public void execute(JobExecutionContext context) 
			throws JobExecutionException {
		try {
			logger.debug(new Date() + ", executing PurgeEventLogJob...");
			
			ServletContext servletContext = (ServletContext)context.getScheduler().getContext().get("ServletContext");
			SessionFactory sessionFactory = (SessionFactory)servletContext.getAttribute("SessionFactory");
			logger.debug("Event log retention period: " + getSystemConfig(servletContext, SystemConfigKey.EVENT_LOG_RETENTION_DAY));
			
			Session session = null;
	        Transaction transaction = null;
			
			try {
	            session = sessionFactory.openSession();
	            transaction = session.beginTransaction();
	            
				moveToArchive(session, getLastArchivedTimestamp(session), 
						DateUtil.toStartOfTheDay(DateUtil.subtractDays(new Date(), Integer.parseInt(getSystemConfig(servletContext, SystemConfigKey.EVENT_LOG_RETENTION_DAY)))));
				purgeFromLive(session, getLastArchivedTimestamp(session));
				
				transaction.commit();
			} catch(HibernateException err) {
	            if (transaction != null) {
	                try {
	                    transaction.rollback();
	                } catch (Exception rollbackErr) {
	                    logger.error(rollbackErr.getMessage(), rollbackErr);
	                }
	            }
	            
	            logger.error(err.getMessage(), err);
	            err.printStackTrace();
			} finally {
	            if (session != null) {
	                try {
	                    session.close();
	                } catch (HibernateException err) {
	                    // Ignore
	                }
	            }
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			err.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getSystemConfig(ServletContext servletContext, String identifier) {
		return ((Map<String, String>)servletContext.getAttribute("SystemConfigs")).get(identifier);
	}
	
	private long getLastArchivedTimestamp(Session session) 
			throws HibernateException, Exception {
		try {
			SQLQuery sql = session.createSQLQuery(LAST_ARCHIVED_TIMESTAMP);
			Object lastArchivedTimestamp = sql.uniqueResult();
			
			if(lastArchivedTimestamp != null) {
				return ((BigInteger)lastArchivedTimestamp).longValue();
			}
			
			return 0L;
		} catch(HibernateException err) {
			throw err;
		}
	}
	
	private void moveToArchive(Session session, long fromTimestamp, long toTimestamp) 
			throws HibernateException, Exception {
		try {
			logger.debug("Archive event log from: " + fromTimestamp + "; to: " + toTimestamp);
			SQLQuery sql = session.createSQLQuery(INSERT_ARCHIVE_SQL);
			sql.setLong(0, fromTimestamp);
			sql.setLong(1, toTimestamp);
			
			sql.executeUpdate();
			session.flush();
		} catch(HibernateException err) {
			throw err;
		}
	}
	
	private void purgeFromLive(Session session, long upToTimestamp) 
			throws HibernateException, Exception {
		try {
			logger.debug("Purge event log up to: " + upToTimestamp);
			SQLQuery sql = session.createSQLQuery(PURGE_LIVE_SQL);
			sql.setLong(0, upToTimestamp);
			
			sql.executeUpdate();
		} catch(HibernateException err) {
			throw err;
		}
	}
}
