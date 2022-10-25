package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ExcludeRepoFolder;

public class ExcludeRepoFolderDAO 
		extends HibernateDAO<ExcludeRepoFolder> {
	
	public ExcludeRepoFolderDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
