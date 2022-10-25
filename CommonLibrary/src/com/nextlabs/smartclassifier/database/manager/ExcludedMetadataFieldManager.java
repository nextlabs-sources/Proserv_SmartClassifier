package com.nextlabs.smartclassifier.database.manager;

import com.nextlabs.smartclassifier.database.dao.ExcludedMetadataFieldDAO;
import com.nextlabs.smartclassifier.database.entity.ExcludedMetadataField;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pkalra on 10/31/2016.
 */
public class ExcludedMetadataFieldManager extends Manager {

    private ExcludedMetadataFieldDAO excludedMetadataFieldDAO;

    public ExcludedMetadataFieldManager(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
        this.excludedMetadataFieldDAO = new ExcludedMetadataFieldDAO(sessionFactory, session);
    }

    public List<ExcludedMetadataField> getAllExcludedFields() {
        return excludedMetadataFieldDAO.getAll();
    }

    public List<String> getAllExcludedFieldNames() {

        List<String> excludedMetadataFieldNames = new ArrayList<>();
        List<ExcludedMetadataField> excludedMetadataFieldList =  excludedMetadataFieldDAO.getAll();

        for(ExcludedMetadataField excludedMetadataField : excludedMetadataFieldList){
            excludedMetadataFieldNames.add(excludedMetadataField.getFieldName());
        }
        return excludedMetadataFieldNames;

    }
}
