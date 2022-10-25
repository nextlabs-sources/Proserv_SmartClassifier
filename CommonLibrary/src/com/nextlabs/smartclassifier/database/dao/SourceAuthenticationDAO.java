package com.nextlabs.smartclassifier.database.dao;

import com.nextlabs.smartclassifier.database.entity.SourceAuthentication;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by pkalra on 10/21/2016.
 */
public class SourceAuthenticationDAO extends HibernateDAO<SourceAuthentication> {
    public SourceAuthenticationDAO(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
    }
}
