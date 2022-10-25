package com.nextlabs.smartclassifier.solr;

import static com.nextlabs.smartclassifier.constant.Punctuation.CLOSE_BRACKET;
import static com.nextlabs.smartclassifier.constant.Punctuation.COLON;
import static com.nextlabs.smartclassifier.constant.Punctuation.HYPHEN;
import static com.nextlabs.smartclassifier.constant.Punctuation.OPEN_BRACKET;
import static com.nextlabs.smartclassifier.constant.Punctuation.SPACE;
import static com.nextlabs.smartclassifier.solr.QueryBuilder.BODY_FIELD;
import static com.nextlabs.smartclassifier.solr.QueryBuilder.DIRECTORY_FIELD;
import static com.nextlabs.smartclassifier.solr.QueryBuilder.DOCUMENT_NAME_FIELD;
import static com.nextlabs.smartclassifier.solr.QueryBuilder.FOLDER_URL_FIELD;
import static com.nextlabs.smartclassifier.solr.QueryBuilder.FOOTER_FIELD;
import static com.nextlabs.smartclassifier.solr.QueryBuilder.HEADER_FIELD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.nextlabs.smartclassifier.constant.DataSection;
import com.nextlabs.smartclassifier.constant.Operator;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.database.entity.CriteriaGroup;
import com.nextlabs.smartclassifier.database.entity.MetadataField;

public class QueryEngine  {

    private static final Logger logger = LogManager.getLogger(QueryEngine.class);
    private HttpSolrClient client;
    private static String userName;
    private static String passWord;

    public QueryEngine(String indexServerURL, String userName, String passWord)
            throws Exception {
        if (StringUtils.isBlank(indexServerURL)) {
            throw new IllegalArgumentException("Invalid index server URL.");
        }
        
        this.userName = userName;
        this.passWord = passWord;
        
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder. 
                         <ConnectionSocketFactory> create()
                        .register("http", new PlainConnectionSocketFactory())
                        .register("https", sslsf)
                        .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(2000);

        CloseableHttpClient httpClient = HttpClients.custom()
                        .setSSLSocketFactory(sslsf)
                        .setConnectionManager(cm)
                        .build();
        
        client = new HttpSolrClient.Builder(indexServerURL).withHttpClient(httpClient).build();
        
    }
    
    public HttpSolrClient getHttpSolrClient() {
    	return client;
    }
    

    public long getDocumentCount(Map<DataSection, String> queries, String repositoryType)
            throws SolrServerException, IOException {
        return getDocumentCount(getQueryString(queries, repositoryType));
    }

    public long getDocumentCount(String queryString)
            throws SolrServerException, IOException {
        logger.info("Querying index for matching document count.");

        SolrQuery query = new SolrQuery();
        
        query.setQuery("id:*");
        query.setFilterQueries(queryString);
        query.setFields("id");
        query.setRows(0);        // Do not query any record
        
        QueryRequest req = new QueryRequest(query);
        req.setBasicAuthCredentials(userName, passWord);
        QueryResponse response = req.process(client);
        SolrDocumentList results = response.getResults();

        logger.info("The number of matching documents found = " + results.getNumFound());

        return results.getNumFound();
    }

    public List<SolrDocument> getDocuments(Map<DataSection, String> queries, String fields, int startIndex, int numDocs, String repositoryType)
            throws SolrServerException, IOException {
        return getDocuments(getQueryString(queries, repositoryType), fields, startIndex, numDocs);
    }

    public List<SolrDocument> getDocuments(String queryString, String fields, int startIndex, int numDocs)
            throws SolrServerException, IOException {
        logger.info("Getting list of matching documents for query: " + queryString);

        List<SolrDocument> documentPaths = new ArrayList<SolrDocument>();

        SolrQuery query = new SolrQuery();
        query.setQuery("id:*");
        query.setFields(fields);
        query.setFilterQueries(queryString);
        query.setStart(startIndex);
        query.setRows(numDocs);

        QueryRequest req = new QueryRequest(query);
        req.setBasicAuthCredentials(userName, passWord);
        QueryResponse response = req.process(client);
        
        SolrDocumentList results = response.getResults();

        for (int i = 0; i < results.size(); ++i) {
            documentPaths.add(results.get(i));
        }

        return documentPaths;
    }

    public QueryResponse getDocuments(String queryString, String fields, int startIndex, int numDocs, String sortFields, boolean ascending)
            throws SolrServerException, IOException {
        logger.info("Getting list of matching documents for query: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery("id:*");
        query.setFields(fields);

        if (StringUtils.isNotBlank(sortFields)) {
            if (ascending) {
                query.setSort(sortFields, SolrQuery.ORDER.asc);
            } else {
                query.setSort(sortFields, SolrQuery.ORDER.desc);
            }
        }

        query.setFilterQueries(queryString);
        query.setStart(startIndex);
        query.setRows(numDocs);
        
        QueryRequest req = new QueryRequest(query);
        req.setBasicAuthCredentials(userName, passWord);

        return req.process(client);
    }
    
    public QueryResponse exportDocuments(String queryString, String fields, int startIndex, int numDocs, String sortFields, boolean ascending)
            throws SolrServerException, IOException {
        logger.info("Getting list of matching documents for query: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/export");
        query.setQuery("id:*");
        query.setFields(fields);

        if (StringUtils.isNotBlank(sortFields)) {
            if (ascending) {
                query.setSort(sortFields, SolrQuery.ORDER.asc);
            } else {
                query.setSort(sortFields, SolrQuery.ORDER.desc);
            }
        }

        query.setFilterQueries(queryString);
        query.setStart(startIndex);
        query.setRows(numDocs);
        
        

        QueryRequest req = new QueryRequest(query);
        req.setBasicAuthCredentials(userName, passWord);

        return req.process(client);
    }


    /**
     * @param metadataFieldByName The Map from Metadata Field Name to MetadataField object
     * @param criteriaGroups                      The Criteria Groups
     * @param repositoryType                      The RepositoryType String
     * @return the query string for Solr
     */
    public static String getQueryString(Map<String, MetadataField> metadataFieldByName, Set<CriteriaGroup> criteriaGroups, String repositoryType) {
        StringBuilder queryString = new StringBuilder();

        queryString.append(QueryBuilder.getRepositoryTypeQueryString(repositoryType));

        if (criteriaGroups != null && criteriaGroups.size() > 0) {

            queryString.append(SPACE)
                    .append(Operator.AND.toString())
                    .append(SPACE);

            Iterator<CriteriaGroup> criteriaGroupIterator = criteriaGroups.iterator();

            while (criteriaGroupIterator.hasNext()) {
                CriteriaGroup criteriaGroup = criteriaGroupIterator.next();
                String criteriaGroupQueryString = getQueryString(QueryBuilder.createQueryString(metadataFieldByName, criteriaGroup.getCriterias(), repositoryType), repositoryType);

                if (StringUtils.isNotBlank(criteriaGroupQueryString)) {
                    queryString.append(OPEN_BRACKET)
                            .append(criteriaGroupQueryString.trim())
                            .append(CLOSE_BRACKET).append(SPACE);
                }

                if (criteriaGroupIterator.hasNext()) {
                    queryString.append(criteriaGroup.getOperator()).append(SPACE);
                }
            }
        }

        String solrQueryString = queryString.toString().trim();
        logger.info("Solr query: " + solrQueryString);

        return solrQueryString;
    }
    
    public long getLastModifiedDateMillisecond(String id)
    		throws SolrServerException, IOException {
    	logger.debug("Getting last modified date for document: " + id);
    	
    	if(StringUtils.isNotBlank(id)) {
	    	SolrQuery query = new SolrQuery();
	    	query.setQuery(SolrPredefinedField.ID + COLON + QueryBuilder.escapeSpecialCharacters(id));
	    	query.setFields(SolrPredefinedField.LAST_MODIFIED_DATE_MILLISECOND);
	    	
	    	QueryRequest req = new QueryRequest(query);
	        req.setBasicAuthCredentials(userName, passWord);
	        
	    	QueryResponse response = req.process(client);
	    	SolrDocumentList results = response.getResults(); 
	    	
	    	if(results.size() > 0) {
	    		SolrDocument documentRecord = results.get(0);
	    		if(documentRecord.get(SolrPredefinedField.LAST_MODIFIED_DATE_MILLISECOND) != null) {
	    			return ((Long) documentRecord.get(SolrPredefinedField.LAST_MODIFIED_DATE_MILLISECOND));
	    		}
	    	}
    	}
    	
    	return -1;
    }
    
    private static String getQueryString(Map<DataSection, String> queries, String repositoryType) {
        StringBuilder queryString = new StringBuilder();

        if (queries != null) {
            Set<Entry<DataSection, String>> entries = queries.entrySet();

            for (Entry<DataSection, String> entry : entries) {
                DataSection dataSection = entry.getKey();
                String value = entry.getValue().trim();

                if (StringUtils.isNotBlank(value)) {
                    if (!value.startsWith(OPEN_BRACKET)) {
                        value = OPEN_BRACKET + value + CLOSE_BRACKET;
                    }

                    if (DataSection.DIRECTORY.equals(dataSection)) {
                        if (RepositoryType.getRepositoryType(repositoryType) == RepositoryType.SHARED_FOLDER) {
                            setSectionValue(queryString, DIRECTORY_FIELD, value);
                        } else if (RepositoryType.getRepositoryType(repositoryType) == RepositoryType.SHAREPOINT) {
                            setSectionValue(queryString, FOLDER_URL_FIELD, value);
                        }
                    } else if (DataSection.FILE_NAME.equals(dataSection)) {
                        setSectionValue(queryString, DOCUMENT_NAME_FIELD, value);
                    } else if (DataSection.HEADER.equals(dataSection)) {
                        setSectionValue(queryString, HEADER_FIELD, value);
                    } else if (DataSection.CONTENT.equals(dataSection)) {
                        setSectionValue(queryString, BODY_FIELD, value);
                    } else if (DataSection.FOOTER.equals(dataSection)) {
                        setSectionValue(queryString, FOOTER_FIELD, value);
                    } else if (DataSection.METADATA.equals(dataSection)) {
                        queryString.append(value);
                    } else {
                        logger.warn("Unrecognizable data section : " + dataSection.getCode());
                    }

                    queryString.append(" ");
                }
            }
        }

        return queryString.toString().trim();
    }

    private static void setSectionValue(StringBuilder queryString, String sectionFieldName, String value) {
        if (value.startsWith(OPEN_BRACKET + HYPHEN)) {
            queryString.append("*:* AND ")
                    .append(HYPHEN)
                    .append(sectionFieldName)
                    .append(value.replaceFirst(HYPHEN, StringUtils.EMPTY));
        } else {
            queryString.append(sectionFieldName)
                    .append(value);
        }
    }
}
