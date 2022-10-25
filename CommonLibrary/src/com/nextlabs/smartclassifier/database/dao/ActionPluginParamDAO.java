package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ActionPluginParam;

public class ActionPluginParamDAO 
		extends HibernateDAO<ActionPluginParam> {
	
	public ActionPluginParamDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
