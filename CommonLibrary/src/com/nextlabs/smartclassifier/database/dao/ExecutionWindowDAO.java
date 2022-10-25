package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ExecutionWindow;

public class ExecutionWindowDAO 
		extends HibernateDAO<ExecutionWindow> {
	
	public ExecutionWindowDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
