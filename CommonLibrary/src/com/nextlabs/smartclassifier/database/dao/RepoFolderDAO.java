package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.RepoFolder;

public class RepoFolderDAO 
		extends HibernateDAO<RepoFolder> {

	public RepoFolderDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
