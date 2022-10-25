package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ActionPlugin;

public class ActionPluginDAO 
		extends HibernateDAO<ActionPlugin> {
	
	public ActionPluginDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
