package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ReportRecipient;

public class ReportRecipientDAO 
		extends HibernateDAO<ReportRecipient> {
	
	public ReportRecipientDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
