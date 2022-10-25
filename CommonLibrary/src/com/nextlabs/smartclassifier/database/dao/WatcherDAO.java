package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.database.entity.Watcher;

public class WatcherDAO
        extends HibernateDAO<Watcher> {

    public WatcherDAO(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
    }
}
