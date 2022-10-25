package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.JMSProfile;

public class JMSProfileDAO 
		extends HibernateDAO<JMSProfile> {
	
	public JMSProfileDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
