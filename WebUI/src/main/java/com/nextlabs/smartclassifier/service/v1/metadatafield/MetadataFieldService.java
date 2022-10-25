package com.nextlabs.smartclassifier.service.v1.metadatafield;

import com.nextlabs.smartclassifier.database.entity.MetadataField;
import com.nextlabs.smartclassifier.database.manager.MetadataFieldManager;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Map;

public class MetadataFieldService 
		extends Service {
	
	public MetadataFieldService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public MetadataFieldService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public Map<String, MetadataField> loadFields() {
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Map<String, MetadataField> metadataFields = (new MetadataFieldManager(getSessionFactory(), session)).loadFields();
			
			transaction.commit();
			
			return metadataFields;
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
