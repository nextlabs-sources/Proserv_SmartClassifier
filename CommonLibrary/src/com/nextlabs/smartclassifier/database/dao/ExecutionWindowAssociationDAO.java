package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ExecutionWindowAssociation;

public class ExecutionWindowAssociationDAO extends
		HibernateDAO<ExecutionWindowAssociation> {
	
	public ExecutionWindowAssociationDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
