package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.DocumentTypeAssociation;

public class DocumentTypeAssociationDAO 
		extends	HibernateDAO<DocumentTypeAssociation> {
	
	public DocumentTypeAssociationDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
