package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.Action;

public class ActionDAO 
		extends HibernateDAO<Action> {
	
	public ActionDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
