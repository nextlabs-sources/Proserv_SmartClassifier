package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.RuleEngine;

public class RuleEngineDAO 
		extends HibernateDAO<RuleEngine> {
	
	public RuleEngineDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
