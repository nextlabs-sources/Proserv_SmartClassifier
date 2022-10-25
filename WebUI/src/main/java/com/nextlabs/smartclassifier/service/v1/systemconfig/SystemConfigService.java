package com.nextlabs.smartclassifier.service.v1.systemconfig;

import com.nextlabs.smartclassifier.database.manager.SystemConfigManager;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Map;

public class SystemConfigService 
		extends Service {
	
	public SystemConfigService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public SystemConfigService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public Map<String, String> loadConfigs() {
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Map<String, String> systemConfigs = (new SystemConfigManager(getSessionFactory(), session)).loadConfigs();
			
			transaction.commit();
			
			return systemConfigs;
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
		
		return null;
	}
}
