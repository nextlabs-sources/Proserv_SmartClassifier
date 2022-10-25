package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.SystemConfigGroup;

public class SystemConfigGroupDAO 
		extends HibernateDAO<SystemConfigGroup> {
	
	public SystemConfigGroupDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
