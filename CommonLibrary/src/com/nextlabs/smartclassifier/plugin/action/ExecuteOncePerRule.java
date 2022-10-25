package com.nextlabs.smartclassifier.plugin.action;

/**
 * Created by pkalra on 1/23/2017.
 */
public interface ExecuteOncePerRule {

    /**
     * Execute the action on the documents returned by this solr query.
     * @param solrQuery the solrQuery passed
     * @return ActionOutcome object
     * @throws Exception exception
     */
    public ActionOutcome execute(final String solrQuery) throws Exception;
}
