package com.nextlabs.smartclassifier.database.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.nextlabs.smartclassifier.database.entity.ExcludedMetadataField;
/**
 * Created by pkalra on 10/31/2016.
 */
public class ExcludedMetadataFieldDAO extends HibernateDAO<ExcludedMetadataField> {

    public ExcludedMetadataFieldDAO(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
    }
}
