package com.nextlabs.smartclassifier.database.manager;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;

import com.nextlabs.smartclassifier.database.dao.DataProviderDAO;
import com.nextlabs.smartclassifier.database.entity.DataProvider;
import com.nextlabs.smartclassifier.exception.ManagerException;

public class DataProviderManager 
		extends Manager {
	
	private DataProviderDAO dataProviderDAO;
	
	public DataProviderManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.dataProviderDAO = new DataProviderDAO(sessionFactory, session);
	}
	
	public List<DataProvider> getDataProviders()
			throws ManagerException {
		try {
			return dataProviderDAO.getAll();
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public long getRecordCount(List<Criterion> criterion) 
			throws ManagerException {
		try {
			return dataProviderDAO.getCount(criterion);
		} catch(HibernateException err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err);
		}
	}
}
