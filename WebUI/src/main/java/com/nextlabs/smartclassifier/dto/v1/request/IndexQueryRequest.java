package com.nextlabs.smartclassifier.dto.v1.request;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.entity.CriteriaGroup;
import com.nextlabs.smartclassifier.dto.v1.CriteriaGroupDTO;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class IndexQueryRequest {

    @Expose
    private List<CriteriaGroupDTO> criteria;
    @Expose
    private String repositoryType;
    @Expose
    private List<CriteriaGroupDTO> directories;
    @Expose
    private List<String> fields;
    @Expose
    private List<SortField> sortFields;
    @Expose
    private int pageSize;
    @Expose
    private int pageNo;

    public List<CriteriaGroupDTO> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<CriteriaGroupDTO> criteria) {
        this.criteria = criteria;
    }

    public List<CriteriaGroupDTO> getDirectories() {
        return directories;
    }

    public void setDirectories(List<CriteriaGroupDTO> directories) {
        this.directories = directories;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<SortField> getSortFields() {
        return sortFields;
    }

    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getFieldsInString() {
        if (this.fields != null && this.fields.size() > 0) {
            return StringUtils.join(this.fields, ' ');
        }

        return null;
    }

    public Set<CriteriaGroup> getCriteriaGroups() {
        Set<CriteriaGroup> criteriaGroups = new LinkedHashSet<>();

        RepositoryType repositoryType = RepositoryType.getRepositoryType(this.repositoryType);

        if (this.criteria != null && this.criteria.size() > 0) {
            for (CriteriaGroupDTO criteriaGroupDTO : this.criteria) {
                if (criteriaGroupDTO.getCriterias() != null
                        && !criteriaGroupDTO.getCriterias().isEmpty()) {
                    criteriaGroups.add(criteriaGroupDTO.getEntity(repositoryType));
                }
            }
        }

        if (this.directories != null && this.directories.size() > 0) {
            for (CriteriaGroupDTO criteriaGroupDTO : this.directories) {
                if (criteriaGroupDTO.getCriterias() != null
                        && !criteriaGroupDTO.getCriterias().isEmpty()) {
                    criteriaGroups.add(criteriaGroupDTO.getEntity(repositoryType));
                }
            }
        }

        return criteriaGroups;
    }

    public String getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }
}
