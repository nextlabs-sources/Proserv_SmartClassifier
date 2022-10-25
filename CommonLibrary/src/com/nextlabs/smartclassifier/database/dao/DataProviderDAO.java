package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.DataProvider;

public class DataProviderDAO 
		extends HibernateDAO<DataProvider> {
	
	public DataProviderDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
