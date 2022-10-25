package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.Extractor;

public class ExtractorDAO 
		extends HibernateDAO<Extractor> {
	
	public ExtractorDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
