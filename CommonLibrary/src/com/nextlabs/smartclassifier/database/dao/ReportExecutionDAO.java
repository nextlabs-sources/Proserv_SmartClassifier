package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ReportExecution;

public class ReportExecutionDAO 
		extends HibernateDAO<ReportExecution> {
	
	public ReportExecutionDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
