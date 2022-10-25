package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.Report;

public class ReportDAO 
		extends HibernateDAO<Report> {

	public ReportDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
