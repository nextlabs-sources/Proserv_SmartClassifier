package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.User;

public class UserDAO 
		extends HibernateDAO<User> {
	
	public UserDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
