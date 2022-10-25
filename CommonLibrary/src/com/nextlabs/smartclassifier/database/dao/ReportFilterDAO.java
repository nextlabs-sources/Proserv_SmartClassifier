package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ReportFilter;

public class ReportFilterDAO 
		extends HibernateDAO<ReportFilter> {
	
	public ReportFilterDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
