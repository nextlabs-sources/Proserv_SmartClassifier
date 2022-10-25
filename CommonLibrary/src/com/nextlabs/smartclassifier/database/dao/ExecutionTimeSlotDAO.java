package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.ExecutionTimeSlot;

public class ExecutionTimeSlotDAO 
		extends HibernateDAO<ExecutionTimeSlot> {
	
	public ExecutionTimeSlotDAO(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
	}

}
