package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.Heartbeat;

public class HeartbeatDAO 
		extends HibernateDAO<Heartbeat> {
	
	public HeartbeatDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}
}
