package com.nextlabs.smartclassifier.database.entity;

/**
 * Created by pkalra on 10/31/2016.
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EXCLUDED_METADATA_FIELDS")
public class ExcludedMetadataField {

    public static final String FIELD_NAME = "fieldName";

    @Id
    @Column(name = "FIELD_NAME", unique = true, nullable = false, length = 100)
    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

}
