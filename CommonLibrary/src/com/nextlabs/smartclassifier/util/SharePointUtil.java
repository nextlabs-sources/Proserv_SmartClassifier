package com.nextlabs.smartclassifier.util;

import com.nextlabs.smartclassifier.constant.Punctuation;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import static com.nextlabs.smartclassifier.constant.Punctuation.FORWARD_SLASH;

/**
 * Utility class to perform SharePoint operations for Smart Classifier
 *
 * @author pkalra
 */
public class SharePointUtil {

    protected static final Logger logger = LogManager.getLogger(SharePointUtil.class);
    private static final String DAVWWWROOT = "DavWWWRoot";
    private static final String DELETE = "DELETE";
    private static final String PATCH = "PATCH";
    private static final String MERGE = "MERGE";
    private static final String X_REQUEST_DIGEST = "X-RequestDigest";
    private static final String X_HTTP_Method = "X-HTTP-Method";
    private static final String ACCEPT = "Accept";
    private static final String IF_MATCH = "If-Match";
    private static final String APPLICATION_JSON = "application/json;odata=verbose";
    private static final String API_ENDPOINT = "/_api/web";
    private static final String LISTS_ENDPOINT = "/Lists";
    private static final String CONTEXT_INFO_ENDPOINT = "/_api/contextinfo";
    private static final String METADATA = "__metadata";

    /**
     * Utility method to test connection to the url. Calls the /_api/contextinfo to check connection
     *
     * @param httpclient the HTTPClient instance with a set of credentials
     * @param url        the url to be accessed
     * @return true, if the URL can be accessed, false otherwise.
     */
    public static boolean testConnection(CloseableHttpClient httpclient, String url) {
        CloseableHttpResponse response;

        URI uri;
        try {
            uri = new URI(StringFunctions.removeWhiteSpacesInURL(url));

            HttpPost post = new HttpPost(uri + CONTEXT_INFO_ENDPOINT);
            post.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);

            logger.debug(post.getRequestLine());

            response = httpclient.execute(post);

            logger.debug(response.getStatusLine());

            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 & status < 300) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Utility method to get the SharePoint document libraries from a SharePoint site URL. Returns
     * null if any of the parameters is null.
     *
     * @param httpclient      the HTTPClient instance
     * @param sharePointURL   the SharePoint site URL or document library URL
     * @param spFoldersToSite The output map storing a mapping from sharepoint folders to site URL
     * @return true, if the operation was a success false (e.g. HTTP Status code 400/500) otherwise
     */
    public static Boolean getSPFolders(
            CloseableHttpClient httpclient, String sharePointURL, TreeMap<Path, String> spFoldersToSite) {

        AtomicReference<Boolean> returnValue = new AtomicReference<>(true);
        if (httpclient == null || sharePointURL == null) {
            return false;
        }

        sharePointURL = StringFunctions.removeTrailingForwardSlash(sharePointURL);
        sharePointURL = StringFunctions.removeWhiteSpacesInURL(sharePointURL);

        Path spRootPath = getSPRootPath(sharePointURL);

        if (spRootPath != null) {
            if (isASite(httpclient, sharePointURL, returnValue)) {
                logger.debug("getSPFolders(): " + sharePointURL + " is a Site");
                getFolders(httpclient, sharePointURL, spFoldersToSite, spRootPath);
                return true;
            } else if (returnValue.get()) {
                logger.debug("getSPFolders(): " + sharePointURL + " is a document library");
                addFoldersInThisDL(httpclient, sharePointURL, spFoldersToSite, spRootPath);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

        // return sharePointFoldersToSiteURLMap;
    }

    /**
     * Utility Method to get the Root Path for the SharePoint site
     * <p>
     * <p>e.g. for input = <code>http://sp2013w2k12r2</code>, the output = <code>
     * \\sp2013w2k12r2\DavWWWRoot\</code> (the SharePoint root directory)
     * <p>
     * <p>If the directory does not exist it logs an error, and returns <code>null</code>.
     *
     * @param sharePointURL the SharePoint URL could either be a site or a document library
     * @return Path to this SharePoint root directory
     */
    private static Path getSPRootPath(String sharePointURL) {

        URI uri;
        Path spRootPath = null;

        try {
            uri = new URI(StringFunctions.removeWhiteSpacesInURL(sharePointURL));
            String spHostName = uri.getHost();

            if (uri.getScheme().equals("http")) {
                spRootPath = Paths.get("\\\\" + spHostName + "\\DavWWWRoot\\");
            } else if (uri.getScheme().equals("https")) {
                spRootPath = Paths.get("\\\\" + spHostName + "\\@SSL\\DavWWWRoot\\");
            }

            if (spRootPath == null || Files.notExists(spRootPath, LinkOption.NOFOLLOW_LINKS)) {
                logger.error("SharePoint Directory not found. Please check if you have have enabled WebClient Web Service.");
                return null; // or throw an exception
            }

        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }

        return spRootPath;
    }

    /**
     * Helper method to find out if the SharePoint URL is a site (ie. not a document library)
     *
     * @param httpClient    the HTTPClient object
     * @param sharePointURL the SharePoint URL
     * @return true if the sharePointURL is a site URL, false otherwise
     */
    private static boolean isASite(
            CloseableHttpClient httpClient, String sharePointURL, AtomicReference<Boolean> returnValue) {

        String webFullUrl;
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {

            URI uri = new URI(StringFunctions.removeWhiteSpacesInURL(sharePointURL));

            HttpPost httpPost = new HttpPost(uri + CONTEXT_INFO_ENDPOINT);
            httpPost.addHeader(ACCEPT, APPLICATION_JSON);
            httpPost.setConfig(requestConfig);

            logger.debug("isASite(): " + httpPost.getRequestLine());

            response = httpClient.execute(httpPost);

            logger.debug("isASite(): " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {

                HttpEntity httpEntity = response.getEntity();

                if (httpEntity != null) {

                    String result = EntityUtils.toString(response.getEntity());
                    JSONObject resultObject = new JSONObject(result);
                    webFullUrl =
                            resultObject
                                    .getJSONObject("d")
                                    .getJSONObject("GetContextWebInformation")
                                    .getString("WebFullUrl");
                    String spURL = StringFunctions.removeWhiteSpacesInURL(sharePointURL);
                    String wbUrl = StringFunctions.removeWhiteSpacesInURL(webFullUrl);
                    return spURL.equalsIgnoreCase(wbUrl);
                } else {
                    logger.error("isASite(): " + "THE RESPONSE ENTITY IS NULL ! ");
                    returnValue.set(false);
                    return false;
                }
            } else {
                logger.error("isASite(): " + response.getStatusLine().toString().toUpperCase());
                returnValue.set(false);
                return false;
            }

        } catch (IOException | URISyntaxException ie) {
            logger.error(ie.getMessage(), ie);
            returnValue.set(false);
            return false;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
                returnValue.set(false);
            }
        }
    }

    /**
     * Utility method to get the list of folders within a site. This site may have other sub-sites.
     * The <code>spFoldersToSite</code> is updated with the folders found and the site they belong to
     *
     * @param httpClient                  the instance of HTTPClient
     * @param siteURL                     the site URL
     * @param spFoldersToSite             the map that stores a mapping from sharepoint folders to site URL
     * @param sharePointDirectoryRootPath the SharePoint root directory
     */
    private static void getFolders(
            CloseableHttpClient httpClient,
            String siteURL,
            Map<Path, String> spFoldersToSite,
            Path sharePointDirectoryRootPath) {

        // get folders within the site
        getSiteFolders(httpClient, siteURL, spFoldersToSite, sharePointDirectoryRootPath);

        // get sub-sites
        CloseableHttpResponse response;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {

            // get the document libraries in this site
            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));

            URI uri =
                    new URIBuilder(siteURI)
                            .setScheme(siteURI.getScheme())
                            .setHost(siteURI.getHost())
                            .setPath(siteURI.getPath() + API_ENDPOINT + "/webs")
                            .setParameter("$select", "Url")
                            .build();

            HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader(ACCEPT, APPLICATION_JSON);
            httpGet.setConfig(requestConfig);

            logger.debug("getFolders(): " + httpGet.getRequestLine());

            response = httpClient.execute(httpGet);

            logger.debug("getFolders(): " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                String responseString;

                if (entity != null) {
                    responseString = EntityUtils.toString(response.getEntity());
                    JSONArray resultsArray =
                            new JSONObject(responseString).getJSONObject("d").getJSONArray("results");

                    for (Object object : resultsArray) {

                        JSONObject jsonObject = (JSONObject) object;
                        String subSiteURL = jsonObject.get("Url").toString();

                        getFolders(httpClient, subSiteURL, spFoldersToSite, sharePointDirectoryRootPath);
                    }
                } else {
                    logger.error("getFolders(): " + "THE RESPONSE BODY IS NULL ! ");
                }
            } else {
                logger.error("getFolders(): " + response.getStatusLine().toString().toUpperCase());
            }
            // Map<String, Object> jsonMap = jsonToMap(new
            // JSONObject(result).getJSONObject("d").getJSONObject("results"));
        } catch (IOException | URISyntaxException e2) {
            logger.error(e2.getMessage(), e2);
        }
    }

    /**
     * Helper method to get the folders within a site. The <code>spFoldersToSite</code> is updated
     * with the folders found and the SharePoint site they belong to
     *
     * @param httpClient                  the HTTPClient instance
     * @param siteURL                     the SharePoint site URL
     * @param spFoldersToSite             the Map containing the mapping from SharePoint folders -> the SharePoint
     *                                    site the folders belong to.
     * @param sharePointDirectoryRootPath the SharePoint root directory path
     */
    private static void getSiteFolders(
            CloseableHttpClient httpClient,
            String siteURL,
            Map<Path, String> spFoldersToSite,
            Path sharePointDirectoryRootPath) {

        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            URI uri =
                    new URIBuilder(siteURI)
                            .setScheme(siteURI.getScheme())
                            .setHost(siteURI.getHost())
                            .setPath(siteURI.getPath() + API_ENDPOINT + "/lists")
                            .setParameter("$select", "RootFolder,ParentWebUrl,BaseTemplate")
                            .setParameter(
                                    "$filter",
                                    "BaseTemplate eq 101 and Title ne 'Site Assets' and Title ne 'Style Library'and Title ne 'Form Templates'")
                            .build();

            HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader(ACCEPT, APPLICATION_JSON);
            httpGet.setConfig(requestConfig);

            logger.debug("getSiteFolders(): " + httpGet.getRequestLine());

            response = httpClient.execute(httpGet);

            logger.debug("getSiteFolders(): " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(response.getEntity());

                    JSONArray resultsArray =
                            new JSONObject(result).getJSONObject("d").getJSONArray("results");

                    for (Object o : resultsArray) {
                        JSONObject resultObject = (JSONObject) o;
                        Map<String, Object> resultsMap = JSONUtil.jsonToMap(resultObject);

                        String parentWebUrl =
                                StringFunctions.changeForwardSlashToBackSlash(
                                        (String) resultsMap.get("ParentWebUrl"));
                        parentWebUrl = StringFunctions.appendBackSlash(parentWebUrl);
                        Path directoryPath = sharePointDirectoryRootPath.resolve(parentWebUrl);
                        HashMap<String, Object> rootFolderValues =
                                (HashMap<String, Object>)
                                        ((HashMap<String, Object>) resultsMap.get("RootFolder")).get("__deferred");
                        addFoldersFromDL(
                                httpClient,
                                (String) rootFolderValues.get("uri"),
                                spFoldersToSite,
                                siteURL,
                                directoryPath);
                    }
                } else {
                    logger.error("getSiteFolders(): " + "THE RESPONSE ENTITY IS NULL !");
                }
            } else {
                logger.error("getSiteFolders(): " + response.getStatusLine().toString().toUpperCase());
            }

        } catch (IOException | URISyntaxException ie) {
            logger.error(ie.getMessage(), ie);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Adds folders from DL to the spFoldersToSite map.
     *
     * @param httpClient      The HttpClient Instance
     * @param rootFolderURI   The RootFolder URI for the DL
     * @param spFoldersToSite the map containing the mapping from SharePoint folders to Site URL
     * @param siteURL         the site this Document library belong to
     * @param parentDirPath   the Path to the parent directory for this folder
     */
    private static void addFoldersFromDL(
            CloseableHttpClient httpClient,
            String rootFolderURI,
            Map<Path, String> spFoldersToSite,
            String siteURL,
            Path parentDirPath) {

        CloseableHttpResponse response;
        HttpGet httpGet;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        try {
            URI uri =
                    new URIBuilder(StringFunctions.removeWhiteSpacesInURL(rootFolderURI))
                            .addParameter("$select", "Name,Folders")
                            .addParameter("$filter", "Name ne 'Forms'")
                            .build();

            httpGet = new HttpGet(uri);
            httpGet.addHeader(ACCEPT, APPLICATION_JSON);
            httpGet.setConfig(requestConfig);

            logger.debug("addFoldersFromDL(): " + httpGet.getRequestLine());

            response = httpClient.execute(httpGet);

            logger.debug("addFoldersFromDL(): " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);
                    response.close();
                    // add root folder
                    JSONObject d = new JSONObject(responseString).getJSONObject("d");
                    String rootFolder = d.getString("Name");
                    Path directoryPath = parentDirPath.resolve(rootFolder).normalize();

                    if (Files.exists(directoryPath, LinkOption.NOFOLLOW_LINKS)) {
                        spFoldersToSite.put(directoryPath, siteURL);
                    } else {
                        logger.error("addFoldersFromDL(): " + directoryPath + " DOES NOT EXIST!");
                    }
                    // get all sub-folders within
                    Map<String, Object> jsonToMap = JSONUtil.jsonToMap(d);
                    HashMap<String, Object> folderValues =
                            (HashMap<String, Object>)
                                    ((HashMap<String, Object>) jsonToMap.get("Folders")).get("__deferred");
                    addSubFolders(
                            httpClient,
                            folderValues.get("uri").toString(),
                            spFoldersToSite,
                            siteURL,
                            directoryPath);
                } else {
                    logger.error("addFoldersFromDL(): " + "THE RESPONSE ENTITY IS NULL !");
                }
            } else {
                logger.error("addFoldersFromDL(): " + response.getStatusLine().toString().toUpperCase());
            }
        } catch (URISyntaxException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void addSubFolders(
            CloseableHttpClient httpClient,
            String folderURI,
            Map<Path, String> spFoldersToSite,
            String siteURL,
            Path parentDirPath) {

        CloseableHttpResponse response;
        HttpGet httpGet;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            URI uri =
                    new URIBuilder(StringFunctions.removeWhiteSpacesInURL(folderURI))
                            .addParameter("$select", "Name,Folders")
                            .addParameter("$filter", "Name ne 'Forms'")
                            .build();

            httpGet = new HttpGet(uri);
            httpGet.addHeader(ACCEPT, APPLICATION_JSON);
            httpGet.setConfig(requestConfig);

            logger.debug("addSubFolders(): " + httpGet.getRequestLine());

            response = httpClient.execute(httpGet);

            logger.debug("addSubFolders(): " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);
                    response.close();
                    JSONArray resultsArray =
                            new JSONObject(responseString).getJSONObject("d").getJSONArray("results");

                    if (resultsArray.length() != 0) {
                        for (Object o : resultsArray) {

                            if (o instanceof JSONObject) {
                                JSONObject resultObject = (JSONObject) o;

                                String dirName = resultObject.getString("Name");
                                Path dirPath = parentDirPath.resolve(dirName);

                                if (Files.exists(dirPath, LinkOption.NOFOLLOW_LINKS)) {
                                    spFoldersToSite.put(dirPath, siteURL);
                                } else {
                                    logger.error("addSubFolders(): " + dirPath + " DOES NOT EXIST!");
                                }

                                Map<String, Object> objectMap = JSONUtil.jsonToMap(resultObject);
                                HashMap<String, Object> foldersMap =
                                        (HashMap<String, Object>)
                                                ((HashMap<String, Object>) objectMap.get("Folders")).get("__deferred");
                                addSubFolders(
                                        httpClient, (String) foldersMap.get("uri"), spFoldersToSite, siteURL, dirPath);
                            }
                        }
                    }
                } else {
                    logger.error("addSubFolders(): " + "THE RESPONSE ENTITY IS NULL !");
                }
            } else {
                logger.error("addSubFolders(): " + response.getStatusLine().toString().toUpperCase());
            }
        } catch (URISyntaxException | IOException e) {
            logger.error(e.getMessage(), e);
        }

        // return null;
    }

    /**
     * Method to get the root folder for a DL and then all the sub-folders inside it.
     *
     * @param httpClient                  HttpClient Instance
     * @param documentLibraryURL          The url for the document library
     * @param spFoldersToSite             the map containing the mapping from sharePoint Folders to Site URL
     * @param sharePointDirectoryRootPath the SharePoint Root Directory Path
     */
    private static void addFoldersInThisDL(
            CloseableHttpClient httpClient,
            String documentLibraryURL,
            TreeMap<Path, String> spFoldersToSite,
            Path sharePointDirectoryRootPath) {

        CloseableHttpResponse response;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            String siteURL = getSiteURL(httpClient, documentLibraryURL);

            if (siteURL != null) {
                String serverRelativeUrl = new URI(StringFunctions.removeWhiteSpacesInURL(documentLibraryURL)).getPath();
                URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
                URI getURI = new URIBuilder(siteURI)
                        .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFolderByServerRelativeUrl('" + serverRelativeUrl + "')")
                        .setParameter("$select", "Name,Folders,ServerRelativeUrl")
                        .build();

                HttpGet httpGet = new HttpGet(getURI);
                httpGet.addHeader(ACCEPT, APPLICATION_JSON);
                httpGet.setConfig(requestConfig);

                logger.debug("addFoldersInThisDL(): " + httpGet.getRequestLine());

                response = httpClient.execute(httpGet);

                logger.debug("addFoldersInThisDL(): " + response.getStatusLine());
                int status = response.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    HttpEntity httpEntity = response.getEntity();
                    if (httpEntity != null) {
                        String responseString = EntityUtils.toString(httpEntity);
                        response.close();

                        JSONObject d = new JSONObject(responseString).getJSONObject("d");
                        String serverRelativeURL = d.getString("ServerRelativeUrl");

                        Path dLFolderPath = sharePointDirectoryRootPath.resolve(serverRelativeURL).normalize();
                        if (Files.exists(dLFolderPath, LinkOption.NOFOLLOW_LINKS)) {
                            spFoldersToSite.put(dLFolderPath, siteURL);
                        }

                        Map<String, Object> jsonToMap = JSONUtil.jsonToMap(d);
                        HashMap<String, Object> foldersMap;
                        foldersMap =
                                (HashMap<String, Object>)
                                        ((HashMap<String, Object>) jsonToMap.get("Folders")).get("__deferred");
                        addSubFolders(
                                httpClient, (String) foldersMap.get("uri"), spFoldersToSite, siteURL, dLFolderPath);
                    } else {
                        logger.error("addFoldersInThisDL(): " + "THE RESPONSE ENTITY IS NULL ! ");
                    }
                } else {
                    logger.error("addFoldersInThisDL(): " + response.getStatusLine());
                }
            } else {
                logger.error(
                        "addFoldersInThisDL(): " + "THE SITE URL FOR THIS DOCUMENT LIBRARY WAS NOT FOUND ! ");
            }

        } catch (URISyntaxException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Checks if the folder is a valid folder. Returns 404 for Site Folders
     *
     * @param httpClient HttpClient Instance
     * @param targetURL  the target URL
     * @return true, if the targetURL is not a site and is a valid SharePoint folder.
     */
    public static boolean isAValidFolder(CloseableHttpClient httpClient, String targetURL) throws IOException, URISyntaxException {

        CloseableHttpResponse response;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        if (httpClient == null || StringUtils.isBlank(targetURL)) {
            logger.debug("isAValidFolder() : Please check the arguments");
            return false;
        }

        try {
            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(targetURL));
            String serverRelativeUrl = siteURI.getPath();

            URI getURI = new URIBuilder(siteURI)
                    .setPath(API_ENDPOINT + "/GetFolderByServerRelativeUrl('" + serverRelativeUrl + "')")
                    .build();

            HttpGet httpGet = new HttpGet(getURI);
            httpGet.addHeader(ACCEPT, APPLICATION_JSON);
            httpGet.setConfig(requestConfig);

            logger.debug("isAValidFolder(): " + httpGet.getRequestLine());

            response = httpClient.execute(httpGet);

            logger.debug("isAValidFolder(): " + response.getStatusLine());

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                String str = EntityUtils.toString(httpEntity);
                logger.debug("isAValidFolder(): " + str);
            }

            int status = response.getStatusLine().getStatusCode();
            return status >= 200 && status < 300;
        } catch (IOException | URISyntaxException e) {
            logger.error("isAValidFolder(): " + e.getMessage(), e);
            throw e;
        }
    }


  /*
   * public static boolean isURLAValidFolder(CloseableHttpClient httpClient, String targetURL) {
   *
   * CloseableHttpResponse response;
   *
   * try { URI siteURI = new URI(targetURL); String serverRelativeUrl = siteURI.getPath();
   *
   * URI getURI = new URIBuilder(siteURI) .setPath(API_ENDPOINT + "/GetFolderByServerRelativeUrl('" +
   * serverRelativeUrl + "')") .build();
   *
   * HttpGet httpGet = new HttpGet(getURI); httpGet.addHeader(ACCEPT, APPLICATION_JSON);
   *
   * logger.trace(httpGet.getRequestLine());
   *
   * HttpContext httpContext = new BasicHttpContext(); response = httpClient.execute(httpGet, httpContext);
   *
   * logger.trace(response.getStatusLine());
   *
   * int status = response.getStatusLine().getStatusCode();
   *
   * if (status >= 200 && status < 300) { return true; } else if (status >= 400 && status < 500) {
   * logger.error(targetURL + " is not a valid document library folder."); return false; } else if (status >= 500) {
   * logger.error(targetURL + " threw a server side exception!"); return false; } } catch (IOException |
   * URISyntaxException e) { logger.error(e.getMessage(), e); return false; } return false; }
   */

    /**
     * Checks if the SharePoint folder is a document library folder and not a site folder.
     *
     * @param httpClient   the HTTPClient Instance
     * @param directoryURL the directory URL
     * @return true only if the URL is a document library folder, false in all other cases including
     * errors.
     */
    private static boolean isADL(CloseableHttpClient httpClient, String directoryURL) {

        String webFullUrl;
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {

            URI folderURI = new URI(StringFunctions.removeWhiteSpacesInURL(directoryURL));
            URI postURI =
                    new URIBuilder(folderURI).setPath(folderURI.getPath() + CONTEXT_INFO_ENDPOINT).build();

            HttpPost httpPost = new HttpPost(postURI);
            httpPost.addHeader(ACCEPT, APPLICATION_JSON);
            httpPost.setConfig(requestConfig);

            logger.debug("isADL() Request = " + httpPost.getRequestLine());

            response = httpClient.execute(httpPost);

            logger.debug("isADL() Response = " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {

                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONObject obj = new JSONObject(result);
                    webFullUrl =
                            obj.getJSONObject("d")
                                    .getJSONObject("GetContextWebInformation")
                                    .getString("WebFullUrl");
                    return !directoryURL.equals(webFullUrl);
                } else {
                    logger.error("THE RESPONSE ENTITY IS NULL ! ");
                    return false;
                }
            } else {

                logger.error(response.getStatusLine().toString().toUpperCase());
                return false;
            }
        } catch (IOException | URISyntaxException ie) {
            logger.error(ie.getMessage(), ie);
            return false;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }


    public static String getSiteByFolder(CloseableHttpClient httpClient, String folderURL) throws IOException, URISyntaxException {

        String webFullUrl;
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        if (httpClient == null || StringUtils.isBlank(folderURL)) {
            logger.debug("getSiteByFolder(): Please check the input arguments!");
            return null;
        }

        try {
            URI folderURI = new URI(StringFunctions.removeWhiteSpacesInURL(folderURL));
            URI postURI = new URIBuilder(folderURI)
                    .setPath(folderURI.getPath() + CONTEXT_INFO_ENDPOINT)
                    .build();

            HttpPost httpPost = new HttpPost(postURI);
            httpPost.addHeader(ACCEPT, APPLICATION_JSON);
            httpPost.setConfig(requestConfig);

            logger.debug("getSiteByFolder(): " + httpPost.getRequestLine());

            response = httpClient.execute(httpPost);

            logger.debug("getSiteByFolder(): " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity httpEntity = response.getEntity();

                if (httpEntity != null) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONObject obj = new JSONObject(result);
                    webFullUrl =
                            obj.getJSONObject("d")
                                    .getJSONObject("GetContextWebInformation")
                                    .getString("WebFullUrl");
                    return webFullUrl;
                } else {
                    throw new IOException("response.getEntity() is null!");
                }
            } else {
                throw new HttpResponseException(status, response.getStatusLine().toString());
            }
        } catch (IOException | URISyntaxException ie) {
            logger.error("getSiteByFolder(): " + ie.getMessage(), ie);
            throw ie;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }


    public static boolean moveFile(
            CloseableHttpClient httpClient,
            String siteURL,
            String fileRelativePath,
            String targetPath,
            boolean overwrite) throws Exception {

        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        if (httpClient == null
                || StringUtils.isBlank(siteURL)
                || StringUtils.isBlank(fileRelativePath)
                || StringUtils.isBlank(targetPath)) {
            logger.error("PLEASE CHECK THE INPUT ARGUMENTS");
            return false;
        }

        try {
            logger.debug("Trying to move the file " + fileRelativePath + " at " + siteURL + " to " + targetPath);

            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            URI postURI = new URIBuilder(siteURI)
                    .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFileByServerRelativeUrl('" + fileRelativePath + "')/moveTo(newurl='" + targetPath + "',flags=" + ((overwrite) ? "1" : "0") + ")")
                    .build();

            logger.debug(postURI.toString());

            HttpPost httpPost = new HttpPost(postURI);
            httpPost.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
            httpPost.addHeader(HttpHeaders.HOST, postURI.getHost());
            httpPost.setConfig(requestConfig);

            String formDigestValue = getFormDigestValue(httpClient, siteURI);

            httpPost.addHeader(X_REQUEST_DIGEST, formDigestValue);

            logger.debug("Move Request = " + httpPost.getRequestLine());

            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                String str = EntityUtils.toString(httpEntity);
                logger.debug("Move Response = " + str);
            }
            logger.debug("Move Response = " + response.getStatusLine());

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                return true;
            } else {
                logger.error("Move Error = " + response.getStatusLine().toString().toUpperCase());
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), "Move File: " + response.getStatusLine().toString());
            }

        } catch (Exception e) {
            logger.error("Move File: " + e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param httpClient         an HTTPClient Instance
     * @param siteURL            the site the file belong to
     * @param serverRelativePath the server relative path to the file
     * @return true if the operation is successful, false otherwise
     */
    public static boolean deleteFile(
            CloseableHttpClient httpClient, String siteURL, String serverRelativePath) throws Exception {

        CloseableHttpResponse httpResponse = null;

        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        if (httpClient == null
                || StringUtils.isBlank(siteURL)
                || StringUtils.isBlank(serverRelativePath)) {
            logger.error("PLEASE CHECK THE INPUT ARGUMENTS");
            return false;
        }

        try {
            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            URI postURI = new URIBuilder(siteURI)
                    .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFileByServerRelativeUrl('" + serverRelativePath + "')")
                    .build();
            HttpPost httpPost = new HttpPost(postURI);
            httpPost.addHeader(ACCEPT, APPLICATION_JSON);
            httpPost.addHeader(X_HTTP_Method, DELETE);
            httpPost.setConfig(requestConfig);

            String formDigestValue = getFormDigestValue(httpClient, siteURI);
            if (StringUtils.isBlank(formDigestValue)) {
                logger.error("FORM DIGEST VALUE IS = " + formDigestValue);
                throw new Exception("Form Digest = " + formDigestValue);
            }

            httpPost.addHeader(X_REQUEST_DIGEST, formDigestValue);

            logger.debug("Delete File Request = " + httpPost.getRequestLine());

            httpResponse = httpClient.execute(httpPost);

            logger.debug("Delete File Response = " + httpResponse.getStatusLine());

            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                String str = EntityUtils.toString(httpEntity);
                logger.debug("Delete File Response = " + str);
            }

            int status = httpResponse.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                return true;
            } else {
                logger.error("Delete Error = " + httpResponse.getStatusLine().toString().toUpperCase());
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().toString());
            }
        } catch (Exception e) {
            logger.error("Delete File: " + e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Copies a file represented by <code> fileRelativePath </code> in <code>siteURL</code> to folder
     * represented by <code>targetURL</code>
     *
     * @param siteURL          the site the file belong to
     * @param fileRelativePath the relative path to the file
     * @param targetPath       the target folder URL
     * @return true if the operation was successful, false otherwise
     */
    public static boolean copyFile(
            CloseableHttpClient httpClient,
            String siteURL,
            String fileRelativePath,
            String targetPath,
            boolean overwrite) throws Exception {

        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        if (httpClient == null
                || StringUtils.isBlank(siteURL)
                || StringUtils.isBlank(fileRelativePath)
                || StringUtils.isBlank(targetPath)) {
            logger.error("PLEASE CHECK THE INPUT ARGUMENTS");
            return false;
        }

        try {
            logger.debug("Trying to copy the file " + fileRelativePath + " at " + siteURL + " to " + targetPath);

            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            URI postURI = new URIBuilder(siteURI)
                    .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFileByServerRelativeUrl('" + fileRelativePath + "')/copyTo(strnewurl='" + targetPath + "',boverwrite=" + overwrite + ")")
                    .build();

            HttpPost httpPost = new HttpPost(postURI);
            httpPost.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
            httpPost.addHeader(HttpHeaders.HOST, postURI.getHost());
            httpPost.setConfig(requestConfig);
            String formDigestValue = getFormDigestValue(httpClient, siteURI);

            httpPost.addHeader(X_REQUEST_DIGEST, formDigestValue);

            logger.debug("Copy Request = " + httpPost.getRequestLine());

            response = httpClient.execute(httpPost);

            logger.debug("Copy Response = " + response.getStatusLine());

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                String str = EntityUtils.toString(httpEntity);
                logger.debug("Copy Response = " + str);
            }

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                return true;
            } else {
                logger.error("Copy Error = " + response.getStatusLine().toString().toUpperCase());
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), "Copy File: " + response.getStatusLine().toString());
            }

        } catch (Exception e) {
            logger.error("Copy File: " + e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static boolean checkIn(CloseableHttpClient httpClient, String siteURL, String serverRelativePath, String comment) throws Exception {

        CloseableHttpResponse response = null;
        URI siteURI, postURI;

        if (httpClient == null
                || StringUtils.isBlank(siteURL)
                || StringUtils.isBlank(serverRelativePath)
                || StringUtils.isBlank(comment)) {
            logger.error("checkIn(): PLEASE CHECK THE INPUT ARGUMENTS");
            return false;
        }
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            postURI = new URIBuilder(siteURI)
                    .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFileByServerRelativeUrl('" + serverRelativePath + "')/CheckIn(comment='" + comment + "',checkintype=0)")
                    .build();

            String formDigestValue = getFormDigestValue(httpClient, siteURI);
            if (StringUtils.isBlank(formDigestValue)) {
                logger.error("FORM DIGEST VALUE IS = " + formDigestValue);
                throw new Exception("Form Digest = " + formDigestValue);
            }
            String hostname = siteURI.getHost();

            HttpPost postRequest = new HttpPost(postURI);
            postRequest.addHeader(X_REQUEST_DIGEST, formDigestValue);
            postRequest.addHeader(HttpHeaders.ACCEPT, "application/json;odata=verbose");
            postRequest.addHeader(HttpHeaders.HOST, hostname);
            postRequest.setConfig(requestConfig);
            logger.debug("Check-in request : " + postRequest.getRequestLine());

            response = httpClient.execute(postRequest);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                String str = EntityUtils.toString(httpEntity);
                logger.debug("Check-in response: " + str);
            }
            logger.debug("Check-in response: " + response.getStatusLine());

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                return true;
            } else {
                logger.error("CheckIn Error = " + response.getStatusLine().toString().toUpperCase());
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().toString());
            }
        } catch (Exception e) {
            logger.error("CheckIn File: " + e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public static boolean checkout(
            CloseableHttpClient httpClient, String siteURL, String serverRelativePath) throws Exception {

        CloseableHttpResponse response = null;
        URI siteURI, postURI;
        String hostname;

        if (httpClient == null
                || StringUtils.isBlank(siteURL)
                || StringUtils.isBlank(serverRelativePath)) {
            logger.error("checkout(): PLEASE CHECK THE INPUT ARGUMENTS");
            return false;
        }
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            postURI = new URIBuilder(siteURI)
                    .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFileByServerRelativeUrl('" + serverRelativePath + "')/CheckOut()")
                    .build();
            hostname = siteURI.getHost();

            String formDigestValue = getFormDigestValue(httpClient, siteURI);
            if (StringUtils.isBlank(formDigestValue)) {
                logger.error("FORM DIGEST VALUE IS = " + formDigestValue);
                throw new Exception("Form Digest = " + formDigestValue);
            }

            HttpPost postRequest = new HttpPost(postURI);
            postRequest.addHeader(X_REQUEST_DIGEST, formDigestValue);
            postRequest.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
            postRequest.addHeader(HttpHeaders.HOST, hostname);
            postRequest.setConfig(requestConfig);
            logger.debug("Checkout request = " + postRequest.getRequestLine());

            response = httpClient.execute(postRequest);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                String str = EntityUtils.toString(httpEntity);
                logger.debug("Checkout response = " + str);
            }

            logger.debug("Checkout response = " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                return true;
            } else {
                logger.error("Checkout Error = " + response.getStatusLine().toString().toUpperCase());
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().toString());
            }

        } catch (Exception e) {
            logger.error("Checkout File: " + e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public static boolean undoCheckout(
            CloseableHttpClient httpClient, String siteURL, String serverRelativePath) throws Exception {

        CloseableHttpResponse response = null;
        URI siteURI, postURI;
        String hostname;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            postURI = new URIBuilder(siteURI)
                    .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFileByServerRelativeUrl('" + serverRelativePath + "')/undoCheckOut()")
                    .build();
            hostname = siteURI.getHost();

            String formDigestValue = getFormDigestValue(httpClient, siteURI);
            if (StringUtils.isBlank(formDigestValue)) {
                logger.error("FORM DIGEST VALUE IS = " + formDigestValue);
                throw new Exception("Form Digest = " + formDigestValue);
            }

            HttpPost postRequest = new HttpPost(postURI);
            postRequest.addHeader(X_REQUEST_DIGEST, formDigestValue);
            postRequest.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
            postRequest.addHeader(HttpHeaders.HOST, hostname);
            postRequest.setConfig(requestConfig);
            logger.debug("undoCheckout request = " + postRequest.getRequestLine());

            response = httpClient.execute(postRequest);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                String str = EntityUtils.toString(httpEntity);
                logger.debug("undoCheckout response = " + str);
            }
            logger.debug("undoCheckout response = " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                return true;
            } else {
                logger.error("undoCheckout Error = " + response.getStatusLine().toString().toUpperCase());
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().toString());
            }

        } catch (Exception e) {
            logger.error("undoCheckout File: " + e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

    }

    /**
     * Utility method to find out the SharePoint document Id. Returns null if any of the parameters is
     * null
     *
     * @param httpClient the HTTPClient object
     * @param filePath   the network path of this file, need to be converted to serverRelativeURL
     * @param siteURL    the SharePoint site the document belong to.
     * @return the document id
     */
    public static String getDocumentId(
            CloseableHttpClient httpClient, String filePath, String siteURL) {

        String documentID = "0-0";
        CloseableHttpResponse response = null;

        if (httpClient == null || filePath == null || siteURL == null) {
            return null;
        } else {
            try {
                int indexOfDavWWWRoot = filePath.indexOf(DAVWWWROOT);
                filePath = filePath.substring(indexOfDavWWWRoot + DAVWWWROOT.length());
                URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));

                URI documentIDURI = new URIBuilder(siteURI)
                        .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFileByServerRelativeUrl('" + filePath + "')/ListItemAllFields")
                        .setParameter("$select", "OData__dlc_DocId")
                        .build();

                HttpGet httpget = new HttpGet(documentIDURI);
                httpget.setHeader(ACCEPT, APPLICATION_JSON);

                logger.debug("getDocumentId(): " + httpget.getRequestLine());

                response = httpClient.execute(httpget);

                logger.debug("getDocumentId(): " + response.getStatusLine());

                int status = response.getStatusLine().getStatusCode();

                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);
                logger.debug("getDocumentId(): Response = " + responseString);

                if (status >= 200 && status < 300) {
                    if (entity != null) {
                        documentID = (new JSONObject(responseString)).getJSONObject("d").getString("OData__dlc_DocId");
                    }
                } else {
                    logger.error("getDocumentId(): " + response.getStatusLine());
                }
                EntityUtils.consumeQuietly(entity);
            } catch (Exception ie) {
                logger.error(ie.getMessage(), ie);
            } finally {
                try {
                    if (response != null) {
                        response.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            return documentID;
        }
    }

    /**
     * Helper method to get the site URL for a document library
     *
     * @param httpClient         the HTTPClient Instance
     * @param documentLibraryURL the document library URL
     * @return the siteURL for the document library
     */
    private static String getSiteURL(CloseableHttpClient httpClient, String documentLibraryURL) {

        String webFullUrl;
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {

            URI uri = new URI(StringFunctions.removeWhiteSpacesInURL(documentLibraryURL));
            HttpPost httpPost = new HttpPost(uri + CONTEXT_INFO_ENDPOINT);
            httpPost.addHeader(ACCEPT, APPLICATION_JSON);
            httpPost.setConfig(requestConfig);
            logger.debug("getSiteURL(): " + httpPost.getRequestLine());

            response = httpClient.execute(httpPost);

            logger.debug("getSiteURL(): " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                HttpEntity httpEntity = response.getEntity();

                if (httpEntity != null) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONObject obj = new JSONObject(result);
                    webFullUrl =
                            obj.getJSONObject("d")
                                    .getJSONObject("GetContextWebInformation")
                                    .getString("WebFullUrl");
                    return webFullUrl;
                } else {
                    logger.error("getSiteURL(): " + "THE RESPONSE ENTITY IS NULL !");
                }
            } else {
                logger.error("getSiteURL(): " + response.getStatusLine().toString().toUpperCase());
            }
        } catch (IOException | URISyntaxException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ie) {
                logger.error(ie.getMessage(), ie);
            }
        }
        return null;
    }

    /**
     * Gets the form digest value
     *
     * @return the form digest value
     */
    private static String getFormDigestValue(CloseableHttpClient httpClient, URI siteURI) throws IOException, URISyntaxException {
        String formDigestValue;
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            logger.debug("Getting the Form Digest Value");

            URI httpPostURI =
                    new URIBuilder(siteURI).setPath(siteURI.getPath() + CONTEXT_INFO_ENDPOINT).build();

            HttpPost httpPost = new HttpPost(httpPostURI);
            httpPost.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
            httpPost.addHeader(HttpHeaders.HOST, httpPostURI.getHost().trim());
            httpPost.setConfig(requestConfig);

            logger.debug("Form Digest Request = " + httpPost.getRequestLine());

            response = httpClient.execute(httpPost);

            logger.debug("Form Digest response = " + response.getStatusLine());

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject obj = new JSONObject(result);
                formDigestValue =
                        obj.getJSONObject("d")
                                .getJSONObject("GetContextWebInformation")
                                .getString("FormDigestValue");
                return formDigestValue;
            } else {
                throw new HttpResponseException(status, "getFormDigestValue() : " + response.getStatusLine().toString());
            }
        } catch (IOException | URISyntaxException ie) {
            logger.error("getFormDigestValue(): " + ie.getMessage(), ie);
            throw ie;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ie) {
                logger.error(ie.getMessage(), ie);
            }
        }
    }

    /**
     * Gets the SharePoint metadata from a SharePoint file object.
     *
     * @param httpClient HTTPClient instance
     * @param siteURL    the SharePoint site for the file object
     * @param filePath   the Path to the file
     * @return Map containing the metadata for SharePoint file
     */
    public static Map<String, String> getSharePointMetadata(
            CloseableHttpClient httpClient, String siteURL, String filePath)
            throws Exception {
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;
        HashMap<String, String> metadata = new HashMap<>();
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        if (httpClient == null || StringUtils.isEmpty(siteURL.trim()) || StringUtils.isEmpty(filePath.trim())) {
            throw new IllegalArgumentException(
                    "Either httpClient is null or the siteURL/filePath is null/empty.");
        }

        siteURL = StringFunctions.removeTrailingForwardSlash(siteURL);
        siteURL = StringFunctions.removeWhiteSpacesInURL(siteURL);

        metadata.put("site_url", siteURL);

        String serverRelativeURL = getServerRelativeURL(filePath);

        try {
            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            URI getURI = new URIBuilder(siteURI)
                    .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFileByServerRelativeUrl('" + serverRelativeURL + "')")
                    .build();

            httpGet = new HttpGet(getURI);
            httpGet.setConfig(requestConfig);
            httpGet.addHeader(ACCEPT, APPLICATION_JSON);

            logger.debug("getSharePointMetadata(): " + httpGet.getRequestLine());

            response = httpClient.execute(httpGet);

            logger.debug("getSharePointMetadata(): " + getURI.getPath() + ", Response = " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();

            if (status >= 200 && status < 300) {

                if (entity != null) {
                    JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                    JSONObject d = jsonObject.getJSONObject("d");
                    Map<String, Object> jsonMap = JSONUtil.jsonToMap(d);

                    logger.debug("Trying to extract attributes now for " + getURI.getPath());

                    extractAttributes(httpClient, jsonMap, metadata, getURI.getPath());

                    logger.debug("The keys for " + getURI.getPath() + " = " + Arrays.toString(metadata.keySet().toArray()));
                    String site_URL = metadata.get("site_url");
                    String fileSRURL = metadata.get("ServerRelativeUrl".toLowerCase());
                    if (StringUtils.isBlank(site_URL) || StringUtils.isBlank(fileSRURL)) {
                        logger.error(getURI.getPath() + ": siteURL = " + site_URL + " , serverRelativeURL = " + serverRelativeURL);
                        throw new Exception("siteURL or serverRelativeURL is missing for " + getURI.getPath());
                    }

                    String folderSRURL = fileSRURL.substring(0, fileSRURL.lastIndexOf('/'));
                    URI folderURI = new URIBuilder(siteURI)
                            .setPath(folderSRURL)
                            .build();
                    logger.debug("The folder_url for " + getURI.getPath() + " = " + folderURI.toString());
                    String folderURLString = folderURI.toString();
                    if (!folderURLString.endsWith(FORWARD_SLASH)) {
                        folderURLString += FORWARD_SLASH;
                    }
                    metadata.put("folder_url", folderURLString.toLowerCase());

                    logger.info(String.format("The number of tags found for the SharePoint file %s is %d", filePath, metadata.size()));
                    return metadata;
                } else {
                    logger.error("The HTTPEntity is null");
                }
            } else {
                EntityUtils.consumeQuietly(entity);
                logger.error("getSharePointMetadata(): " + getURI.getPath() + " " + response.getStatusLine());
                if (entity != null) {
                    logger.error("getSharePointMetadata(): " + getURI.getPath() + " Response = " + EntityUtils.toString(entity));
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpGet != null) {
                    httpGet.releaseConnection();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    private static String getServerRelativeURL(String filePath) {

        filePath = filePath.substring(filePath.indexOf(DAVWWWROOT) + DAVWWWROOT.length());
        filePath = filePath.replaceAll("\\\\", "/");
        return filePath;
    }

    private static void extractAttributes(
            CloseableHttpClient httpClient,
            Map<String, Object> fromMap,
            HashMap<String, String> attributeMap, String filePath) {
        for (Map.Entry<String, Object> pair : fromMap.entrySet()) {

            String key = pair.getKey();
            Object value = pair.getValue();

            if (!(value instanceof HashMap)) {
                logger.debug(filePath + " Adding in the map " + pair);
                attributeMap.put(key.toLowerCase(), value.toString());
            } else {
                switch (key) {
                    case "Author":
                        logger.debug("Trying to get the author for " + filePath);
                        getAuthor(httpClient, (HashMap<String, Object>) value, attributeMap, filePath);
                        logger.debug("the author for " + filePath + " is = " + attributeMap.get("author"));
                        break;

                    case "ModifiedBy":
                        logger.debug("Trying to get the editor for " + filePath);
                        getEditor(httpClient, (HashMap<String, Object>) value, attributeMap, filePath);
                        logger.debug("the editor for " + filePath + " is = " + attributeMap.get("last_author"));
                        break;

                    case "ListItemAllFields":
                        logger.debug("Trying to list all fields for " + filePath);
                        listItemAllFields(httpClient, (HashMap<String, Object>) value, attributeMap, filePath);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private static void listItemAllFields(
            CloseableHttpClient httpClient,
            HashMap<String, Object> deferredObjectMap,
            HashMap<String, String> attributeMap, String filePath) {

        CloseableHttpResponse response = null;
        HttpGet httpGet = null;

        String uri = ((HashMap<String, String>) deferredObjectMap.get("__deferred")).get("uri").toString();
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {

            httpGet = new HttpGet(uri);
            httpGet.setConfig(requestConfig);
            httpGet.addHeader(ACCEPT, APPLICATION_JSON);

            logger.debug("listItemAllFields(): " + httpGet.getRequestLine());

            response = httpClient.execute(httpGet);
            logger.debug("listItemAllFields(): " + filePath + " " + response.getStatusLine());

            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                String result = EntityUtils.toString(httpEntity);
                logger.debug("listItemAllFields(): " + filePath + " " + result);
                Map<String, Object> map = JSONUtil.jsonToMap(new JSONObject(result).getJSONObject("d"));
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (!(value instanceof HashMap)) {
                        logger.debug("listItemAllFields(): " + filePath + " Adding " + entry);
                        attributeMap.put(key.toLowerCase(), value.toString());
                    }
                }
            }
            EntityUtils.consumeQuietly(httpEntity);
        } catch (IOException e) {
            logger.error("listItemAllFields(): " + filePath + e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpGet != null) {
                    httpGet.releaseConnection();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static void getEditor(
            CloseableHttpClient httpClient,
            HashMap<String, Object> deferredObjectMap,
            HashMap<String, String> attributeMap, String filePath) {

        CloseableHttpResponse response = null;
        HttpGet httpGet = null;

        String uri =
                ((HashMap<String, String>) deferredObjectMap.get("__deferred")).get("uri").toString();
        uri += "?$select=Title";

        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        try {
            httpGet = new HttpGet(uri);
            httpGet.setConfig(requestConfig);
            httpGet.addHeader(ACCEPT, APPLICATION_JSON);

            logger.debug("getEditor() : " + httpGet.getRequestLine());
            response = httpClient.execute(httpGet);

            logger.debug("getEditor() : " + filePath + " " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            HttpEntity httpEntity = response.getEntity();

            if (status >= 200 && status < 300) {
                if (httpEntity != null) {
                    String responseString = EntityUtils.toString(httpEntity);
                    logger.debug("getEditor(): " + filePath + " " + responseString);
                    JSONObject obj = new JSONObject(responseString);
                    String author_name = obj.getJSONObject("d").getString("Title");
                    attributeMap.put("sp_last_author", author_name);
                }
            } else {
                logger.error("getEditor() : " + filePath + " " + response.getStatusLine());
            }
            EntityUtils.consumeQuietly(httpEntity);
        } catch (IOException e) {
            logger.error("getEditor() : " + filePath + " " + e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpGet != null) {
                    httpGet.releaseConnection();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static boolean setFieldValue(
            CloseableHttpClient httpClient,
            String siteURL,
            String serverRelativeURL,
            String fieldName,
            Object fieldValue,
            String objectType) throws Exception {

        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            URI postURI =
                    new URIBuilder(siteURI)
                            .setPath(
                                    siteURI.getPath()
                                            + API_ENDPOINT
                                            + "/GetFileByServerRelativeUrl('"
                                            + serverRelativeURL
                                            + "')/ListItemAllFields")
                            .build();
            HttpPost postRequest = new HttpPost(postURI);

            postRequest.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
            postRequest.addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
            postRequest.addHeader(HttpHeaders.HOST, siteURI.getHost());
            postRequest.addHeader(IF_MATCH, Punctuation.ASTERISK);
            postRequest.addHeader(X_HTTP_Method, MERGE);
            postRequest.setConfig(requestConfig);
            String formDigestValue = getFormDigestValue(httpClient, siteURI);
            if (StringUtils.isBlank(formDigestValue)) {
                logger.error("FORM DIGEST VALUE IS = " + formDigestValue);
                throw new Exception("SetFieldValue: Form Digest = " + formDigestValue);
            }
            postRequest.addHeader(X_REQUEST_DIGEST, formDigestValue);

            logger.debug("fieldName = " + fieldName);
            logger.debug("fieldValue = " + fieldValue);

            JSONObject type = new JSONObject();
            type.put("type", objectType);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put(METADATA, type);
            jsonBody.put(fieldName, fieldValue);

            logger.debug("setFieldValue(): Request Body = " + jsonBody.toString());

            postRequest.setEntity(
                    new ByteArrayEntity(jsonBody.toString().getBytes(), ContentType.APPLICATION_JSON));

            logger.debug("setFieldValue(): " + postRequest.getRequestLine());

            response = httpClient.execute(postRequest);

            logger.debug("setFieldValue(): " + response.getStatusLine());

            int status = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
                logger.debug("setFieldValue(): Response = " + responseString);
            }

            if (status >= 200 && status < 300) {
                return true;
            } else {
                logger.error("setFieldValue(): " + response.getStatusLine().toString().toUpperCase());
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), "setFieldValue: " + response.getStatusLine().toString());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean getFieldValues(
            CloseableHttpClient httpClient,
            String siteURL,
            String serverRelativeURL,
            String folderURL,
            Map<String, Object> oldFieldValues,
            Set<String> fieldsToBeSet,
            Map<String, Integer> fieldType,
            StringBuilder objectType) throws Exception {

        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        if (httpClient == null
                || StringUtils.isBlank(siteURL)
                || StringUtils.isBlank(serverRelativeURL)
                || StringUtils.isBlank(folderURL)
                || oldFieldValues == null
                || fieldsToBeSet == null
                || fieldsToBeSet.size() == 0) {
            logger.error("getFieldValues(): Please check the input arguments!!!");
            return false;
        }

        try {
            populateFieldTypes(httpClient, siteURL, folderURL, fieldsToBeSet, fieldType);
            HashMapUtil.printMap(fieldType);

            String fieldNames = "";
            for (String fieldName : fieldsToBeSet) {
                fieldNames += fieldName + ",";
            }

            if (fieldNames.length() > 0) {
                fieldNames = fieldNames.substring(0, fieldNames.length() - 1);
            } else {
                logger.error("getFieldValues(): Could not get the field names!!!");
                throw new Exception("NO field names to be fetched!");
            }

            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            URI getURI = new URIBuilder(siteURI)
                    .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFileByServerRelativeUrl('" + serverRelativeURL + "')/ListItemAllFields").setParameter("$select", fieldNames)
                    .build();
            HttpGet getRequest = new HttpGet(getURI);
            getRequest.addHeader(ACCEPT, APPLICATION_JSON);
            getRequest.setConfig(requestConfig);
            logger.debug("getFieldValues(): " + getRequest.getRequestLine());

            response = httpClient.execute(getRequest);

            logger.debug("getFieldValues(): " + response.getStatusLine());

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);

                    logger.debug("getFieldValues() response = " + responseString);

                    String _type = (new JSONObject(responseString)).getJSONObject("d").getJSONObject("__metadata").getString("type");
                    objectType.append(_type);
                    logger.debug("type = " + objectType);

                    for (String fieldName : fieldsToBeSet) {

                        logger.debug("getFieldValues(): Trying to get " + fieldName + " field for " + serverRelativeURL);

                        JSONObject data = (new JSONObject(responseString)).getJSONObject("d");

                        logger.debug(data);
                        if (data.has(fieldName)) {

                            logger.debug("data has " + fieldName);

                            Object fieldValue;
                            if (!data.isNull(fieldName)) {

                                logger.debug(fieldName + " is not null");

                                fieldValue = data.get(fieldName);
                                oldFieldValues.put(fieldName, fieldValue);

                                logger.debug("getFieldValues(): " + fieldName + " = " + fieldValue);
                            } else {

                                logger.debug(fieldName + " is null");
                                oldFieldValues.put(fieldName, null);
                                logger.debug("getFieldValues(): " + fieldName + " = " + null);
                            }
                        } else {
                            logger.warn("getFieldValues(): " + fieldName + " was not found for " + serverRelativeURL + " Please check the document library.");
                        }
                    }
                    return true;
                } else {
                    throw new IOException("getFieldValues(): RESPONSE ENTITY IS NULL!");
                }
            } else {
                logger.error("ERROR: " + response.getStatusLine().toString().toUpperCase());
                throw new HttpResponseException(status, "getFieldValues():" + response.getStatusLine().toString());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static void populateFieldTypes(
            CloseableHttpClient httpClient,
            String siteURL,
            String folderURL,
            Set<String> fieldsToSet,
            Map<String, Integer> fieldType) throws Exception {

        String listGUID = getListGUID(httpClient, siteURL, folderURL);

        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            URI getURI = new URIBuilder(siteURI).setPath(siteURI.getPath() + API_ENDPOINT + LISTS_ENDPOINT + "(guid'" + listGUID + "')/Fields")
                    .setParameter("$select", "Title,FieldTypeKind")
                    .build();

            HttpGet getReq = new HttpGet(getURI);
            getReq.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
            getReq.addHeader(HttpHeaders.HOST, siteURI.getHost());
            getReq.setConfig(requestConfig);
            logger.debug("populateFieldTypes(): " + getReq.getRequestLine());

            response = httpClient.execute(getReq);

            logger.debug("populateFieldTypes(): " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {

                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);
                JSONArray result =
                        new JSONObject(responseString).getJSONObject("d").getJSONArray("results");

                for (Object obj : result) {

                    JSONObject jsonObj = (JSONObject) obj;
                    String fieldName = jsonObj.getString("Title");
                    if (fieldsToSet.contains(fieldName)) {
                        Integer fieldTypeKind = jsonObj.getInt("FieldTypeKind");
                        fieldType.put(fieldName, fieldTypeKind);
                    }
                }
            } else {
                logger.error("populateFieldTypes(): " + response.getStatusLine());
            }
        } catch (Exception e) {
            logger.error("populateFieldTypes(): " + e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static String getListGUID(
            CloseableHttpClient httpClient, String siteURL, String folderURL) throws Exception {
        CloseableHttpResponse response = null;
        String listGUID = null;

        if (httpClient == null || StringUtils.isBlank(siteURL) || StringUtils.isBlank(folderURL)) {
            logger.error("getListGUID(): PLEASE CHECK THE ARGUMENTS!!!");
            return null;
        }
        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        try {
            URI siteURI = new URI(StringFunctions.removeWhiteSpacesInURL(siteURL));
            URI folderURI = new URI(StringFunctions.removeWhiteSpacesInURL(folderURL));
            URI getURI = new URIBuilder(siteURI)
                    .setPath(siteURI.getPath() + API_ENDPOINT + "/GetFolderByServerRelativeUrl('" + folderURI.getPath() + "')/Properties")
                    .build();

            HttpGet getReq = new HttpGet(getURI);
            getReq.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
            getReq.addHeader(HttpHeaders.HOST, siteURI.getHost());
            getReq.setConfig(requestConfig);
            logger.debug("getListGUID(): " + getReq.getRequestLine());

            response = httpClient.execute(getReq);

            logger.debug("getListGUID(): " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {

                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);
                    listGUID = new JSONObject(responseString).getJSONObject("d").getString("vti_x005f_listname");
                    listGUID = listGUID.substring(1, listGUID.length() - 1);
                }
            } else {
                throw new IOException("response.getEntity() returns null");
            }

        } catch (Exception e) {
            logger.error("getListGUID(): " + e.getMessage(), e);
            throw new Exception("getListGUID(): " + e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return listGUID;
    }

    private static void getAuthor(
            CloseableHttpClient httpClient,
            HashMap<String, Object> deferredObjectMap,
            HashMap<String, String> attributeMap, String filePath) {

        CloseableHttpResponse response = null;
        HttpGet httpGet = null;

        String uri =
                ((HashMap<String, String>) deferredObjectMap.get("__deferred")).get("uri").toString();
        uri += "?$select=Title";

        RequestConfig requestConfig = RequestConfig.copy(HTTPClientUtil.getDefaultRequestConfig())
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        try {
            httpGet = new HttpGet(uri);
            httpGet.setConfig(requestConfig);

            httpGet.addHeader(ACCEPT, APPLICATION_JSON);
            logger.debug("getAuthor(): " + httpGet.getRequestLine());

            response = httpClient.execute(httpGet);
            logger.debug("getAuthor(): " + filePath + " " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            HttpEntity httpEntity = response.getEntity();

            if (status >= 200 & status < 300) {
                if (httpEntity != null) {
                    String responseString = EntityUtils.toString(httpEntity);
                    logger.debug("getAuthor(): " + filePath + " " + responseString);
                    JSONObject obj = new JSONObject(responseString);
                    String author_name = obj.getJSONObject("d").getString("Title");
                    attributeMap.put("sp_author", author_name);
                }
            } else {
                logger.error("getAuthor(): " + filePath + " " + response.getStatusLine());
            }
            EntityUtils.consumeQuietly(httpEntity);
        } catch (IOException e) {
            logger.error("getAuthor(): " + filePath + " " + e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpGet != null) {
                    httpGet.releaseConnection();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static String getSharePointURL(String absolutePath) {

        String serverRelativeURL = absolutePath.substring(absolutePath.indexOf(DAVWWWROOT) + DAVWWWROOT.length());
        String host = absolutePath.substring(2).replaceFirst("/.*", "");

        return StringFunctions.removeWhiteSpacesInURL("http://" + host + serverRelativeURL);
    }

    /**
     * This method is used to remove sub-folders from folderMap. If any path (ie. a key) is a sub-path
     * of any other path (i.e. any other key), it is removed.
     *
     * @param input the input Map
     * @return the map with sub-paths removed.
     */
    public static TreeMap<Path, String> removeSubPathsFromMap(TreeMap<Path, String> input) {

        TreeMap<Path, String> outputMap = new TreeMap<>(input);

        TreeSet<Path> keySet1 = new TreeSet<>(outputMap.keySet());
        TreeSet<Path> keySet2 = new TreeSet<>(outputMap.keySet());

        for (Path path : keySet2) {
            for (Path oneOfThePaths : keySet1) {
                if (path.startsWith(oneOfThePaths) && path != oneOfThePaths) {
                    outputMap.remove(path);
                }
            }
        }
        return outputMap;
    }

    public static class JSONUtil {
        /**
         * Convenience method to convert JSONObject to a Map
         *
         * @param json JSONObject
         * @return Map created from the JSONObject
         * @throws JSONException thrown by the org.json library
         */
        public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
            Map<String, Object> retMap = new HashMap<>();

            if (json != JSONObject.NULL) {
                retMap = toMap(json);
            }
            return retMap;
        }

        private static Map<String, Object> toMap(JSONObject object) throws JSONException {
            Map<String, Object> map = new HashMap<>();

            Iterator<String> keysItr = object.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                Object value = object.get(key);

                if (value instanceof JSONArray) {
                    value = toList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = toMap((JSONObject) value);
                }
                map.put(key, value);
            }
            return map;
        }

        private static List<Object> toList(JSONArray array) throws JSONException {
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                Object value = array.get(i);
                if (value instanceof JSONArray) {
                    value = toList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = toMap((JSONObject) value);
                }
                list.add(value);
            }
            return list;
        }
    }

    public static class HashMapUtil {
        public static LinkedHashMap<Path, String> sortHashMapByValues(HashMap<Path, String> passedMap) {
            List<Path> mapKeys = new ArrayList<>(passedMap.keySet());
            List<String> mapValues = new ArrayList<>(passedMap.values());
            Collections.sort(mapValues);
            Collections.sort(mapKeys);

            LinkedHashMap<Path, String> sortedMap = new LinkedHashMap<>();

            Iterator<String> valueIt = mapValues.iterator();
            while (valueIt.hasNext()) {
                String val = valueIt.next();
                Iterator<Path> keyIt = mapKeys.iterator();

                while (keyIt.hasNext()) {
                    Path key = keyIt.next();
                    String comp1 = passedMap.get(key);
                    String comp2 = val;

                    if (comp1.equals(comp2)) {
                        keyIt.remove();
                        sortedMap.put(key, val);
                        break;
                    }
                }
            }
            return sortedMap;
        }

        public static void printMap(Map<? extends Object, ? extends Object> map) {

            for (Map.Entry<? extends Object, ? extends Object> entry : map.entrySet()) {
                logger.debug("< " + entry.getKey() + ", " + entry.getValue() + " >");
            }
        }
    }
}
