package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.DocumentSizeLimit;

public class DocumentSizeLimitDAO 
		extends HibernateDAO<DocumentSizeLimit> {
	
	public DocumentSizeLimitDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
