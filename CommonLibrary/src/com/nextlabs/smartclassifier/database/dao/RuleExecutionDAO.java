package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.RuleExecution;

public class RuleExecutionDAO 
		extends HibernateDAO<RuleExecution> {
	
	public RuleExecutionDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
