package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ActionParam;

public class ActionParamDAO 
		extends HibernateDAO<ActionParam> {
	
	public ActionParamDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
