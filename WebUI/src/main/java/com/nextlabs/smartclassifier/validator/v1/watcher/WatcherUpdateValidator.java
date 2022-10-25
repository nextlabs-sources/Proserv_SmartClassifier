package com.nextlabs.smartclassifier.validator.v1.watcher;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.dto.v1.ExcludeRepoFolderDTO;
import com.nextlabs.smartclassifier.dto.v1.RepoFolderDTO;
import com.nextlabs.smartclassifier.dto.v1.WatcherDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.util.SharePointUtil;
import com.nextlabs.smartclassifier.util.StringFunctions;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public class WatcherUpdateValidator {

    private static final String SHARED_PATH_PATTERN =
            "^(\\\\)(\\\\[A-Za-z0-9_ ~!@#$%^&*()+-{}<>,.?;\"']+){2,}(\\\\?)$";
    private static final Logger logger = LogManager.getLogger(WatcherUpdateValidator.class);

    public static Validation validate(UpdateRequest request) throws Exception {
        CloseableHttpClient httpClient = null;
        Validation validation = new Validation();
        WatcherDTO watcherDTO = (WatcherDTO) request.getData();

        try {
            if (watcherDTO.getId() == null || watcherDTO.getId() == 0) {
                validation.isValid(false);
                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
                validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "WatcherID"));

                return validation;
            }

            if (StringUtils.isBlank(watcherDTO.getName())) {
                validation.isValid(false);
                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Name"));

                return validation;
            }

            if (watcherDTO.getJMSProfile() == null
                    || watcherDTO.getJMSProfile().getId() == null
                    || watcherDTO.getJMSProfile().getId() == 0) {
                validation.isValid(false);
                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
                validation.setErrorMessage(
                        MessageUtil.getMessage("invalid.input.field.zero", "JMS profile"));

                return validation;
            }

            if (watcherDTO.getFileMonitorCount() == null || watcherDTO.getFileMonitorCount() <= 0) {
                validation.isValid(false);
                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
                validation.setErrorMessage(
                        MessageUtil.getMessage("invalid.input.field.minvalue", "File monitor count", "1"));

                return validation;
            }

            if (watcherDTO.getDocumentTypeAssociations() == null) {
                validation.isValid(false);
                validation.setErrorCode(MessageUtil.getMessage("invalid.input.empty.collection.code"));
                validation.setErrorMessage(
                        MessageUtil.getMessage("invalid.input.empty.collection", "Document type association"));

                return validation;
            }

            if (watcherDTO.getConfigReloadInterval() == null || watcherDTO.getConfigReloadInterval() < 300) {
                validation.isValid(false);
                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
                validation.setErrorMessage(
                        MessageUtil.getMessage(
                                "invalid.input.field.minvalue", "Configuration reload interval", "300"));

                return validation;
            }

            if (watcherDTO.getRepositoryFolders() != null) {

                Set<String> includedFolders = new HashSet<>();
                Set<String> excludedFolders = new HashSet<>();
                Set<String> includedURLs = new HashSet<>();
                Set<String> excludedURLs = new HashSet<>();

                for (RepoFolderDTO repositoryFolderDTO : watcherDTO.getRepositoryFolders()) {

                    // 1. check for blank path
                    if (StringUtils.isBlank(repositoryFolderDTO.getPath())) {
                        validation.isValid(false);
                        validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                        validation.setErrorMessage(
                                MessageUtil.getMessage("invalid.input.field.blank", "Repository path"));

                        return validation;
                    }

                    RepositoryType repositoryType =
                            RepositoryType.getRepositoryType(repositoryFolderDTO.getRepositoryType());

                    if (repositoryType == RepositoryType.SHARED_FOLDER) {

                        if (!repositoryFolderDTO.getPath().endsWith("\\")) {
                            repositoryFolderDTO.setPath(repositoryFolderDTO.getPath() + "\\");
                        }

                        // Check if it is a shared path
                        if (!Pattern.matches(SHARED_PATH_PATTERN, repositoryFolderDTO.getPath())) {
                            validation.isValid(false);
                            validation.setErrorCode(
                                    MessageUtil.getMessage("invalid.input.field.directorypattern.code"));
                            validation.setErrorMessage(
                                    MessageUtil.getMessage(
                                            "invalid.input.field.directorypattern", repositoryFolderDTO.getPath()));

                            return validation;
                        }

                        for (String includedFolder : includedFolders) {
                            // duplicate path
                            if (repositoryFolderDTO.getPath().equalsIgnoreCase(includedFolder)) {
                                validation.isValid(false);
                                validation.setErrorCode(
                                        MessageUtil.getMessage("invalid.input.field.includedirectorydoubled.code"));
                                validation.setErrorMessage(
                                        MessageUtil.getMessage(
                                                "invalid.input.field.includedirectorydoubled",
                                                repositoryFolderDTO.getPath()));

                                return validation;
                            }

                            // repository folder = child folder, included folder = parent folder
                            // trying to add a child of already existing parent - will be crawled twice if allowed
                            if (repositoryFolderDTO
                                    .getPath()
                                    .toLowerCase()
                                    .startsWith(includedFolder.toLowerCase())) {
                                validation.isValid(false);
                                validation.setErrorCode(
                                        MessageUtil.getMessage("invalid.input.field.includedirectory.code"));
                                validation.setErrorMessage(
                                        MessageUtil.getMessage(
                                                "invalid.input.field.includedirectory",
                                                repositoryFolderDTO.getPath(),
                                                includedFolder));

                                return validation;
                            }

                            // included folder = child folder, repository path = parent folder
                            // trying to add a parent folder of an already existing folder -  will be crawled twice if allowed
                            if (includedFolder
                                    .toLowerCase()
                                    .startsWith(repositoryFolderDTO.getPath().toLowerCase())) {
                                validation.isValid(false);
                                validation.setErrorCode(
                                        MessageUtil.getMessage("invalid.input.field.includedirectory.code"));
                                validation.setErrorMessage(
                                        MessageUtil.getMessage(
                                                "invalid.input.field.includedirectory",
                                                includedFolder,
                                                repositoryFolderDTO.getPath()));

                                return validation;
                            }
                        }

                        includedFolders.add(repositoryFolderDTO.getPath());

                        if (repositoryFolderDTO.getExcludeRepositoryFolders() != null) {
                            for (ExcludeRepoFolderDTO excludeRepositoryFolderDTO :
                                    repositoryFolderDTO.getExcludeRepositoryFolders()) {

                                // checking for blank path
                                if (StringUtils.isBlank(excludeRepositoryFolderDTO.getPath())) {
                                    validation.isValid(false);
                                    validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                                    validation.setErrorMessage(
                                            MessageUtil.getMessage(
                                                    "invalid.input.field.blank", "Exclude repository path"));

                                    return validation;
                                } else {
                                    if (!excludeRepositoryFolderDTO.getPath().endsWith("\\")) {
                                        excludeRepositoryFolderDTO.setPath(excludeRepositoryFolderDTO.getPath() + "\\");
                                    }
                                }

                                // checking if exclude folder is a subpath of
                                // include folder
                                if (!excludeRepositoryFolderDTO
                                        .getPath()
                                        .toLowerCase()
                                        .startsWith(repositoryFolderDTO.getPath().toLowerCase())) {
                                    validation.isValid(false);
                                    validation.setErrorCode(
                                            MessageUtil.getMessage("invalid.input.field.childdirectory.code"));
                                    validation.setErrorMessage(
                                            MessageUtil.getMessage(
                                                    "invalid.input.field.childdirectory",
                                                    excludeRepositoryFolderDTO.getPath(),
                                                    repositoryFolderDTO.getPath()));

                                    return validation;
                                }

                                for (String excludedFolder : excludedFolders) {
                                    // duplicate exclude folder
                                    if (excludeRepositoryFolderDTO.getPath().equalsIgnoreCase(excludedFolder)) {
                                        validation.isValid(false);
                                        validation.setErrorCode(
                                                MessageUtil.getMessage("invalid.input.field.excludedirectorydoubled.code"));
                                        validation.setErrorMessage(
                                                MessageUtil.getMessage(
                                                        "invalid.input.field.excludedirectorydoubled",
                                                        repositoryFolderDTO.getPath()));

                                        return validation;
                                    }

                                    // exclude folder is a sub folder of already
                                    // excluded folder
                                    if (excludeRepositoryFolderDTO
                                            .getPath()
                                            .toLowerCase()
                                            .startsWith(excludedFolder.toLowerCase())) {
                                        validation.isValid(false);
                                        validation.setErrorCode(
                                                MessageUtil.getMessage("invalid.input.field.excludedirectory.code"));
                                        validation.setErrorMessage(
                                                MessageUtil.getMessage(
                                                        "invalid.input.field.excludedirectory",
                                                        excludeRepositoryFolderDTO.getPath(),
                                                        excludedFolder));

                                        return validation;
                                    }

                                    // exclude folder is a parent of the already
                                    // excluded folder
                                    if (excludedFolder
                                            .toLowerCase()
                                            .startsWith(excludeRepositoryFolderDTO.getPath().toLowerCase())) {
                                        validation.isValid(false);
                                        validation.setErrorCode(
                                                MessageUtil.getMessage("invalid.input.field.excludedirectory.code"));
                                        validation.setErrorMessage(
                                                MessageUtil.getMessage(
                                                        "invalid.input.field.excludedirectory",
                                                        excludedFolder,
                                                        excludeRepositoryFolderDTO.getPath()));

                                        return validation;
                                    }
                                }

                                excludedFolders.add(excludeRepositoryFolderDTO.getPath());
                            }
                        }
                    } else if (repositoryType == RepositoryType.SHAREPOINT) {
            /*
             * For any URL, check whether it a valid URL
             * a. valid means the URL is actually a valid URI
             * Whether it can be connected */

                        // 1. check if it is a valid URL
                        try {
                            URI uri = new URI(StringFunctions.removeWhiteSpacesInURL(repositoryFolderDTO.getPath()));
                            logger.debug(repositoryFolderDTO.getPath() + " is a valid URL.");

                        } catch (Exception e) {
                            validation.isValid(false);
                            validation.setErrorCode(
                                    MessageUtil.getMessage("invalid.input.field.invalid.url.code"));
                            validation.setErrorMessage(
                                    MessageUtil.getMessage(
                                            "invalid.input.field.invalid.url", repositoryFolderDTO.getPath()));

                            return validation;
                        }

                        // 2. check if a connection can be made.
                        String username = repositoryFolderDTO.getSourceAuthentication().getUsername();
                        String domain = repositoryFolderDTO.getSourceAuthentication().getDomain();
                        String password = repositoryFolderDTO.getSourceAuthentication().getPassword();
                        String workstation = InetAddress.getLocalHost().getHostName();

                        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(
                                new AuthScope(AuthScope.ANY),
                                new NTCredentials(username, password, workstation, domain));

                        httpClient = HttpClients.custom()
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .build();

                        //httpClient = HTTPClientUtil.getHTTPClient(username, password, domain);

                        if (!SharePointUtil.testConnection(httpClient, repositoryFolderDTO.getPath())) {
                            validation.isValid(false);
                            validation.setErrorCode(
                                    MessageUtil.getMessage("invalid.input.field.url.connection.code"));
                            validation.setErrorMessage(
                                    MessageUtil.getMessage(
                                            "invalid.input.field.url.connection", repositoryFolderDTO.getPath()));
                            return validation;
                        } else {
                            logger.debug("Connection to " + repositoryFolderDTO.getPath() + " was successful.");
                        }

                        // 3. check if there are no duplicate entries
                        if (includedURLs.isEmpty()) {
                            includedURLs.add(repositoryFolderDTO.getPath());
                        } else {
                            for (String includedURL : includedURLs) {
                                // duplicate
                                if (repositoryFolderDTO.getPath().equalsIgnoreCase(includedURL)) {
                                    validation.isValid(false);
                                    validation.setErrorCode(
                                            MessageUtil.getMessage("invalid.input.field.duplicate.url.code"));
                                    validation.setErrorMessage(
                                            MessageUtil.getMessage(
                                                    "invalid.input.field.duplicate.url", repositoryFolderDTO.getPath()));

                                    return validation;
                                }
                            }
                            includedURLs.add(repositoryFolderDTO.getPath());
                        }

                        // add excluded folder
                        // do the same stuff for it
                        if (repositoryFolderDTO.getExcludeRepositoryFolders() != null) {
                            for (ExcludeRepoFolderDTO excludeRepoFolderDTO :
                                    repositoryFolderDTO.getExcludeRepositoryFolders()) {

                                // excluded folder blank
                                if (StringUtils.isBlank(excludeRepoFolderDTO.getPath())) {
                                    validation.isValid(false);
                                    validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                                    validation.setErrorMessage(
                                            MessageUtil.getMessage(
                                                    "invalid.input.field.blank", "Exclude repository path"));

                                    return validation;
                                }

                                // excluded URL should start with included URL
                                if (!excludeRepoFolderDTO
                                        .getPath()
                                        .toLowerCase()
                                        .startsWith(repositoryFolderDTO.getPath().toLowerCase())) {
                                    validation.isValid(false);
                                    validation.setErrorCode(
                                            MessageUtil.getMessage("invalid.input.field.url.suburl.code"));
                                    validation.setErrorMessage(
                                            MessageUtil.getMessage(
                                                    "invalid.input.field.url.suburl",
                                                    excludeRepoFolderDTO.getPath(),
                                                    repositoryFolderDTO.getPath()));

                                    return validation;
                                }

                /*
                 * For any URL, check whether it a valid URL
                 * a. valid means the URL is actually a valid URI
                 * Whether it can be connected */

                                // validity
                                try {
                                    //URL url = new URL(StrexcludeRepoFolderDTO.getPath());
                                    URI uri = new URI(StringFunctions.removeWhiteSpacesInURL(excludeRepoFolderDTO.getPath()));
                                    logger.debug(excludeRepoFolderDTO.getPath() + " is a valid URL.");

                                } catch (Exception e) {
                                    validation.isValid(false);
                                    validation.setErrorCode(
                                            MessageUtil.getMessage("invalid.input.field.invalid.url.code"));
                                    validation.setErrorMessage(
                                            MessageUtil.getMessage(
                                                    "invalid.input.field.invalid.url", excludeRepoFolderDTO.getPath()));

                                    return validation;
                                }

                                // 2. check if a connection can be made.
                                if (!SharePointUtil.testConnection(httpClient, excludeRepoFolderDTO.getPath())) {
                                    validation.isValid(false);
                                    validation.setErrorCode(
                                            MessageUtil.getMessage("invalid.input.field.url.connection.code"));
                                    validation.setErrorMessage(
                                            MessageUtil.getMessage(
                                                    "invalid.input.field.url.connection", excludeRepoFolderDTO.getPath()));
                                    return validation;
                                } else {
                                    logger.debug(
                                            "Connection to " + excludeRepoFolderDTO.getPath() + " was successful.");
                                }

                                if (excludedURLs.size() == 0) {
                                    excludedURLs.add(excludeRepoFolderDTO.getPath());
                                } else {
                                    for (String excludeURL : excludedURLs) {
                                        // duplicate exclude folder
                                        if (excludeRepoFolderDTO.getPath().equalsIgnoreCase(excludeURL)) {
                                            validation.isValid(false);
                                            validation.setErrorCode(
                                                    MessageUtil.getMessage(
                                                            "invalid.input.field.excludedirectorydoubled.code"));
                                            validation.setErrorMessage(
                                                    MessageUtil.getMessage(
                                                            "invalid.input.field.excludedirectorydoubled",
                                                            repositoryFolderDTO.getPath()));

                                            return validation;
                                        }
                                    }
                                    excludedURLs.add(excludeRepoFolderDTO.getPath());
                                }
                            }
                        }
                    }
                }

                // check if any include folder starts with any excluded folder.
                for (RepoFolderDTO repositoryFolderDTO : watcherDTO.getRepositoryFolders()) {
                    for (String excludedFolder : excludedFolders) {
                        if (repositoryFolderDTO
                                .getPath()
                                .toLowerCase()
                                .startsWith(excludedFolder.toLowerCase())) {
                            validation.isValid(false);
                            validation.setErrorCode(
                                    MessageUtil.getMessage("invalid.input.field.excludedirectory.code"));
                            validation.setErrorMessage(
                                    MessageUtil.getMessage(
                                            "invalid.input.field.excludedirectory",
                                            repositoryFolderDTO.getPath(),
                                            excludedFolder));

                            return validation;
                        }
                    }
                }
            }
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }

        return validation;
    }
}
