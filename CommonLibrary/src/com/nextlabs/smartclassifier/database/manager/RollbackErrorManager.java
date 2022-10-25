package com.nextlabs.smartclassifier.database.manager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.dao.RollbackErrorDAO;
import com.nextlabs.smartclassifier.database.entity.RollbackError;
import com.nextlabs.smartclassifier.exception.ManagerException;

public class RollbackErrorManager
		extends Manager {
	
	private RollbackErrorDAO rollbackErrorDAO;
	
	public RollbackErrorManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.rollbackErrorDAO = new RollbackErrorDAO(sessionFactory, session);
	}
	
	public void logRollbackError(RollbackError rollbackError)
			throws ManagerException {
		try {
			rollbackErrorDAO.saveOrUpdate(rollbackError);
        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }
	}
}
