package com.nextlabs.smartclassifier.util;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to create and obtain an instance of HTTPClient - with a pooling connection manager.
 * Created by pkalra on 10/14/2016.
 */

public class HTTPClientUtil {

    private final CloseableHttpClient httpclient;
    private final NTCredentials ntCredentials;
    private static RequestConfig defRequestConfig;

    static {
        defRequestConfig = RequestConfig.custom()
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .build();
    }
    /**
     * Map to look up HTTPClient Object from the credentialsProvider
     */
    private static Map<NTCredentials, HTTPClientUtil> HttpClientUtilByCredentials = new ConcurrentHashMap<>();
    protected static final Logger logger = LogManager.getLogger(HTTPClientUtil.class);

    private HTTPClientUtil(String username, String password, String domain, String workstation, int maxConnections) {

        ntCredentials = new NTCredentials(username, password, workstation, domain);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), ntCredentials);

        // Create socket configuration
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

        connManager.setMaxTotal(maxConnections);
        connManager.setDefaultMaxPerRoute(maxConnections);
        connManager.setDefaultSocketConfig(socketConfig);
        connManager.setValidateAfterInactivity(1000);

        httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(defRequestConfig)
                .setConnectionTimeToLive(1, TimeUnit.MINUTES)
                .build();

        logger.info("A new instance of HTTPClient was created.");
    }

    /**
     * Factory method to create and return an HTTPClient Instance. If the HTTPClient with the same set of credentials is found,
     * it is returned otherwise a new one is created and returned. All HTTPClient Instances are a pooling connection manager and hence can send multiple
     * simultaneous requests. Returns null if any of the username/password/domain is null or empty.
     *
     * @param username the username part of Credentials
     * @param password the password part of Credentials
     * @param domain   the domain name part of Credentials
     * @return CloseableHTTPClient object with these set of credentials
     */
    public static synchronized CloseableHttpClient getHTTPClient(String username, String password, String domain) {

        HTTPClientUtil httpClientUtil;

        if (StringUtils.isEmpty(username.trim()) || StringUtils.isEmpty(password.trim()) || StringUtils.isEmpty(domain.trim())) {
            return null;
        }
        try {

            String workstation = InetAddress.getLocalHost().getHostName();
            NTCredentials newNTCredentials = new NTCredentials(username, password, workstation, domain);

            if (HttpClientUtilByCredentials.containsKey(newNTCredentials)) {
                return HttpClientUtilByCredentials.get(newNTCredentials).httpclient;
            } else {
                logger.info("Trying to create a new instance of HTTP Client");
                httpClientUtil = new HTTPClientUtil(username, password, domain, workstation, 200);
                HttpClientUtilByCredentials.put(httpClientUtil.ntCredentials, httpClientUtil);
                return httpClientUtil.httpclient;
            }

        } catch (UnknownHostException e) {
            logger.error("getHTTPClient(): " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Factory method to close all open HTTPClients
     */
    public static synchronized void closeConnections() {
        logger.info("Trying to close HTTP Connections");

        for (HTTPClientUtil httpClientUtil : HttpClientUtilByCredentials.values()) {
            try {
                httpClientUtil.httpclient.close();
            } catch (IOException ie) {
                logger.error(ie.getMessage(), ie);
            }
        }
        HttpClientUtilByCredentials.clear();
        logger.info("HTTP Connections closed successfully.");
    }

    /**
     * Factory method to close all open HTTPClients
     */
    public static synchronized void closeConnection(CloseableHttpClient httpClient) {
        logger.info("Trying to close HTTP client connection");

        for (HTTPClientUtil httpClientUtil : HttpClientUtilByCredentials.values()) {
            if (httpClientUtil.httpclient == httpClient) {
                try {
                    httpClientUtil.httpclient.close();
                    HttpClientUtilByCredentials.remove(httpClientUtil.ntCredentials);
                    logger.info("HTTPClient was closed successfully.");
                } catch (IOException ie) {
                    logger.error(ie.getMessage(), ie);
                }
            }
        }
    }

    public static RequestConfig getDefaultRequestConfig() {
        return defRequestConfig;
    }
}
