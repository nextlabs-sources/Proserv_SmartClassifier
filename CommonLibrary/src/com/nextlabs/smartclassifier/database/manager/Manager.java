package com.nextlabs.smartclassifier.database.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class Manager {
	
	protected final Logger logger = LogManager.getLogger(getClass());

	
	protected SessionFactory sessionFactory;
	
	protected Session session;
	
	public Manager(SessionFactory sessionFactory, Session session) {
		super();
		this.sessionFactory = sessionFactory;
		this.session = session;
	}
}
