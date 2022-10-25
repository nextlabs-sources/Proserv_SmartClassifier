package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.EventLog;

public class EventLogDAO 
		extends HibernateDAO<EventLog> {
	
	public EventLogDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
