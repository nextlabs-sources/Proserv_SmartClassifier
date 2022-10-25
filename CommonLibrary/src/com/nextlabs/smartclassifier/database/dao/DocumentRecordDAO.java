package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.DocumentRecord;

public class DocumentRecordDAO 
		extends HibernateDAO<DocumentRecord> {
	
	public DocumentRecordDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
