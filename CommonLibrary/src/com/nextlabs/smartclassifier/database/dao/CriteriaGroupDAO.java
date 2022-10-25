package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.CriteriaGroup;

public class CriteriaGroupDAO 
		extends HibernateDAO<CriteriaGroup> {
	
	public CriteriaGroupDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
