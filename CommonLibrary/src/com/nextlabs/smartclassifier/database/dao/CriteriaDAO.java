package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.Criteria;

public class CriteriaDAO 
		extends HibernateDAO<Criteria> {
	
	public CriteriaDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
