package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.RollbackError;

public class RollbackErrorDAO 
		extends HibernateDAO<RollbackError> {
	
	public RollbackErrorDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
