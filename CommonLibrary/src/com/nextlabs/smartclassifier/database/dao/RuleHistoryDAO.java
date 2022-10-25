package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.RuleHistory;

public class RuleHistoryDAO 
		extends HibernateDAO<RuleHistory> {
	
	public RuleHistoryDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
