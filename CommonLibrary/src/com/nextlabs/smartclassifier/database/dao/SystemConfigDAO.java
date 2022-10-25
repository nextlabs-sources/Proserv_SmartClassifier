package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.SystemConfig;

public class SystemConfigDAO 
		extends HibernateDAO<SystemConfig> {
	
	public SystemConfigDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
