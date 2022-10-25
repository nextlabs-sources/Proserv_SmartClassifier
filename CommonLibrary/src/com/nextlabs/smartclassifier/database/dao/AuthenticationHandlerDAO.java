package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.AuthenticationHandler;

public class AuthenticationHandlerDAO 
		extends HibernateDAO<AuthenticationHandler> {
	
	public AuthenticationHandlerDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
