package com.nextlabs.smartclassifier.plugin.action;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.dto.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.exception.RollbackException;
import com.nextlabs.smartclassifier.util.HTTPClientUtil;
import com.nextlabs.smartclassifier.util.NxlCryptoUtil;
import com.nextlabs.smartclassifier.util.RepositoryUtil;
import com.nextlabs.smartclassifier.util.SharePointUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;

/**
 * Created by pkalra on 11/23/2016.
 */
public abstract class SharePointAction extends Action {

    private static final Logger logger = LogManager.getLogger(SharePointAction.class);

    protected boolean actionPerformed;
    protected CloseableHttpClient httpClient;

    protected SharePointAction(String name) {
        super(name);
        this.repositoryType = RepositoryType.SHAREPOINT;
    }

    protected void initializeFields() {
        actionPerformed = false;
    }

    protected void initHTTPClient(String repoPath) {
        SourceAuthenticationDTO sourceAuthenticationDTO = RepositoryUtil.getSourceAuthentication(repoPath);

        if (sourceAuthenticationDTO != null) {
            logger.debug("Source Authentication Details " + sourceAuthenticationDTO);

            this.httpClient =
                    HTTPClientUtil.getHTTPClient(
                            sourceAuthenticationDTO.getUserName(),
                            NxlCryptoUtil.decrypt(sourceAuthenticationDTO.getPassword()),
                            sourceAuthenticationDTO.getDomain());
        } else {
            logger.error("SOURCE AUTHENTICATION DETAILS NOT FOUND!");
            this.httpClient = null;
        }
    }

    protected boolean checkOutFile(String siteURL, String serverRelativeURL) throws Exception {
        return SharePointUtil.checkout(httpClient, siteURL, serverRelativeURL);
    }

    protected boolean undoCheckoutFile(String siteURL, String serverRelativeURL) throws Exception {
        return SharePointUtil.undoCheckout(httpClient, siteURL, serverRelativeURL);
    }

    protected boolean checkInFile(String siteURL, String serverRelativeURL, String comment) throws Exception {
        return SharePointUtil.checkIn(httpClient, siteURL, serverRelativeURL, comment);
    }

    protected boolean copyFile(
            String siteURL,
            String sourceServerRelativeURL,
            String destinationServerRelativeURL,
            boolean overwrite) throws Exception {
        return SharePointUtil.copyFile(
                httpClient, siteURL, sourceServerRelativeURL, destinationServerRelativeURL, overwrite);
    }

    protected boolean deleteFile(String siteURL, String serverRelativeURL) throws Exception {
        return SharePointUtil.deleteFile(httpClient, siteURL, serverRelativeURL);
    }

    protected boolean getFieldValues(
            String siteURL,
            String serverRelativeURL,
            String folderURL,
            Map<String, Object> oldFieldValues,
            Set<String> fields,
            Map<String, Integer> fieldType,
            StringBuilder type) throws Exception {
        return SharePointUtil.getFieldValues(
                httpClient, siteURL, serverRelativeURL, folderURL, oldFieldValues, fields, fieldType, type);
    }

    protected boolean setFieldValue(
            String siteURL, String serverRelativeURL, String fieldName, Object fieldValue, String type) throws Exception {
        return SharePointUtil.setFieldValue(
                httpClient, siteURL, serverRelativeURL, fieldName, fieldValue, type);
    }

    public void rollback()
            throws RollbackException {
        logger.debug("Nothing to rollback for " + name);
    }
}
