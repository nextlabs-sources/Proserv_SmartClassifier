package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.DocumentExtractor;

public class DocumentExtractorDAO 
		extends HibernateDAO<DocumentExtractor> {
	
	public DocumentExtractorDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
