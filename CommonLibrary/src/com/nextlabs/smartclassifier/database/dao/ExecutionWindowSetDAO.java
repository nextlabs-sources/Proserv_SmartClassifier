package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ExecutionWindowSet;

public class ExecutionWindowSetDAO 
		extends HibernateDAO<ExecutionWindowSet> {
	
	public ExecutionWindowSetDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
