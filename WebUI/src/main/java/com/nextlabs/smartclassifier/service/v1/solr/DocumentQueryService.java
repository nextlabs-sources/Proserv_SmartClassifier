package com.nextlabs.smartclassifier.service.v1.solr;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.dto.v1.SolrDocumentDTO;
import com.nextlabs.smartclassifier.dto.v1.request.IndexQueryRequest;
import com.nextlabs.smartclassifier.dto.v1.request.SortField;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.solr.QueryEngine;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class DocumentQueryService
        extends Service {

    public DocumentQueryService(ServletContext servletContext) {
        super(servletContext);
    }

    public DocumentQueryService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
        super(servletContext, servletRequest, servletResponse);
    }

    public ServiceResponse getDocuments(IndexQueryRequest queryRequest) {
        RetrievalResponse response = new RetrievalResponse();

        try {
            String sortFieldName = null;
            boolean isAscending = false;
            SortField sortField = getSortField(queryRequest);

            if (sortField != null) {
                sortFieldName = sortField.getField();
                isAscending = "asc".equalsIgnoreCase(sortField.getOrder());
            }

            QueryEngine queryEngine = new QueryEngine(getSystemConfig(SystemConfigKey.INDEXER_URL), 
            		getSystemConfig(SystemConfigKey.INDEXER_USERNAME),
            		getSystemConfig(SystemConfigKey.INDEXER_PASSWORD));
            String queryString = QueryEngine.getQueryString(getMetadataFields(), queryRequest.getCriteriaGroups(), queryRequest.getRepositoryType());
            QueryResponse queryResponse = queryEngine.getDocuments(queryString,
                    getSystemConfig(SystemConfigKey.SOLR_INDEXING_QUERY_WEB_UI_FIELDS),
                    getOffSet(getPageInfo(queryRequest)),
                    queryRequest.getPageSize(),
                    sortFieldName,
                    isAscending);

            SolrDocumentList documentList = queryResponse.getResults();
            response.setTotalNoOfRecords(documentList.getNumFound());

            if (documentList.size() > 0) {
                List<SolrDocumentDTO> solrDocumentDTOs = new ArrayList<>();

                for (SolrDocument solrDocument : documentList) {
                    solrDocumentDTOs.add(new SolrDocumentDTO(solrDocument));
                }

                response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
                response.setMessage(MessageUtil.getMessage("success.data.loaded"));
                response.setData(solrDocumentDTOs);
            } else {
                response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
                response.setMessage(MessageUtil.getMessage("no.data.found"));
            }

            response.setPageInfo(getPageInfo(queryRequest));
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            response.setStatusCode(MessageUtil.getMessage("server.error.code"));
            response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
        }

        return response;
    }

    private SortField getSortField(IndexQueryRequest request) {
        if (request != null
                && request.getSortFields() != null
                && request.getSortFields().size() > 0) {
            return request.getSortFields().get(0);
        }

        return null;
    }

    private PageInfo getPageInfo(IndexQueryRequest request) {
        PageInfo pageInfo = new PageInfo();

        if (request != null) {
            if (request.getPageNo() == 0) {
                pageInfo.setPageNumber(1);
            } else {
                pageInfo.setPageNumber(request.getPageNo());
            }

            if (request.getPageSize() == 0) {
                pageInfo.setSize(Integer.parseInt(getSystemConfig(SystemConfigKey.RESTFUL_RECORD_PAGE_SIZE)));
            } else {
                pageInfo.setSize(request.getPageSize());
            }

            pageInfo.setSkip((request.getPageNo() - 1) * request.getPageSize());
        } else {
            pageInfo.setPageNumber(1);
            pageInfo.setSize(Integer.parseInt(getSystemConfig(SystemConfigKey.RESTFUL_RECORD_PAGE_SIZE)));
        }

        return pageInfo;
    }

    private int getOffSet(PageInfo pageInfo) {
        if (pageInfo != null) {
            if (pageInfo.getPageNumber() > 0 && pageInfo.getSize() > 0) {
                return (pageInfo.getPageNumber() - 1) * pageInfo.getSize();
            }
        }

        return 0;
    }
}
