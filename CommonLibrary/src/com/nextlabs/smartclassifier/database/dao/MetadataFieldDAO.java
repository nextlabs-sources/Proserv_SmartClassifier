package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.MetadataField;

public class MetadataFieldDAO 
		extends HibernateDAO<MetadataField> {
	
	public MetadataFieldDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
