package com.nextlabs.smartclassifier.plugin.action;

import org.apache.solr.common.SolrDocument;

/**
 * Created by pkalra on 1/23/2017.
 */
public interface ExecuteOncePerFile {

    /**
     * Execute action on passed solr document.
     * @param solrDocument the solr document passed
     * @return ActionOutcome object
     * @throws Exception exception
     */
    public ActionOutcome execute(final SolrDocument solrDocument) throws Exception;
}
