package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ReportFilterGroup;

public class ReportFilterGroupDAO 
		extends HibernateDAO<ReportFilterGroup> {
	
	public ReportFilterGroupDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
