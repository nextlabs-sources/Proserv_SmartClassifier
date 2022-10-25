package com.nextlabs.smartclassifier.database.manager;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;

import com.nextlabs.smartclassifier.database.dao.DocumentExtractorDAO;
import com.nextlabs.smartclassifier.database.entity.DocumentExtractor;
import com.nextlabs.smartclassifier.exception.ManagerException;

public class DocumentExtractorManager 
		extends Manager {
	
	private DocumentExtractorDAO documentExtractorDAO;
	
	public DocumentExtractorManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.documentExtractorDAO = new DocumentExtractorDAO(sessionFactory, session);
	}
	
	public List<DocumentExtractor> getDocumentExtractors() 
			throws ManagerException {
		try {
			return documentExtractorDAO.getAll();
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public long getRecordCount(List<Criterion> criterion) 
			throws ManagerException {
		try {
			return documentExtractorDAO.getCount(criterion);
		} catch(HibernateException err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err);
		}
	}
}
