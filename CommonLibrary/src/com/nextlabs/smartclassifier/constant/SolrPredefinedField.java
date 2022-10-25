package com.nextlabs.smartclassifier.constant;

public interface SolrPredefinedField {

    // Standard Solr schema fields
    public static final String ID = "id";
    public static final String FILE_ID = "file_id";
    public static final String DIRECTORY = "directory";
    public static final String DIRECTORY_LCASE = "directory_lcase";
    public static final String DOCUMENT_NAME = "document_name";
    public static final String DOCUMENT_NAME_LCASE = "document_name_lcase";
    public static final String FILE_TYPE = "file_type";
    public static final String REPOSITORY_TYPE = "repository_type";
    public static final String REPO_PATH = "repository_path";
    //public static final String DOCUMENT_ID = "document_id";
    public static final String HEADER = "header";
    public static final String BODY = "body";
    public static final String FOOTER = "footer";
    public static final String CONTENT = "content";

    // Metadata fields
    public static final String AUTHOR = "author";
    public static final String COMMENTS = "comments";
    public static final String KEYWORDS = "keywords";
    public static final String LAST_AUTHOR = "last_author";
    public static final String REVISION_NUMBER = "revision_number";
    public static final String SUBJECT = "subject";
    public static final String TEMPLATE = "template";
    public static final String TITLE = "title";
    public static final String CREATION_DATE = "creation_date";
    public static final String CREATION_DATE_MILLISECOND = "creation_date_millisecond";
    public static final String LAST_MODIFIED_DATE = "last_modified_date";
    public static final String LAST_MODIFIED_DATE_MILLISECOND = "last_modified_date_millisecond";
    public static final String APPLICATION_NAME = "application_name";
    public static final String WORD_COUNT = "word_count";
    public static final String CATEGORY = "category";
    public static final String DESCRIPTION = "description";
    public static final String PAGE_COUNT = "page_count";
    public static final String CREATOR = "creator";
    public static final String PRODUCER = "producer";
    public static final String EMAIL_SENDER = "from";
    public static final String EMAIL_RECIPIENT = "to";
    public static final String FOLDER_URL = "folder_url";
    public static final String SITE_URL = "site_url";
}
