package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.Rule;

public class RuleDAO 
		extends HibernateDAO<Rule> {
	
	public RuleDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
