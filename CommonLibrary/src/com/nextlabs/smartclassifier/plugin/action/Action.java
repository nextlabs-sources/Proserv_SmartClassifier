package com.nextlabs.smartclassifier.plugin.action;

import com.nextlabs.nxl.NxlException;
import com.nextlabs.nxl.RightsManager;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.entity.MetadataField;
import com.nextlabs.smartclassifier.exception.RollbackException;
import com.nextlabs.smartclassifier.solr.SolrDocumentInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.cache.NullFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Action {

    protected Long id;
    protected Long ruleId;
    protected Long pluginId;
    protected String name;
    protected String displayName;
    protected String description;
    protected String toleranceLevel;
    protected Map<String, String> systemConfigs;
    protected Map<String, MetadataField> metadataFieldByName;
    protected Map<String, Set<String>> propertyByName;
    protected Map<String, Set<String>> tagsByName;
    protected Boolean fireOncePerRule;
    protected RepositoryType repositoryType;
    protected SolrDocumentInfo docInfo;
    private static final Logger logger = LogManager.getLogger(Action.class);
    
    public static DefaultFileSystemManager fsMgr;

	protected Action(String name) {
		super();
		this.name = name;
		this.propertyByName = new HashMap<>();
		this.tagsByName = new HashMap<>();

		// Initialize the VFS file system manager
		try {
			if (fsMgr == null) {
				fsMgr = new DefaultFileSystemManager();
				fsMgr.addProvider("file", new DefaultLocalFileProvider());
				// never cache a file
				fsMgr.setFilesCache(new NullFilesCache());
				// get a file object from system every time a method on it is called
				fsMgr.setCacheStrategy(CacheStrategy.ON_CALL);
				fsMgr.init();
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getPluginId() {
        return pluginId;
    }

    public void setPluginId(Long pluginId) {
        this.pluginId = pluginId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getToleranceLevel() {
        return toleranceLevel;
    }

    public void setToleranceLevel(String toleranceLevel) {
        this.toleranceLevel = toleranceLevel;
    }

    public Map<String, String> getSystemConfigs() {
        return this.systemConfigs;
    }

    public void setSystemConfigs(Map<String, String> configs) {
        this.systemConfigs = configs;
    }

    public Map<String, MetadataField> getMetadataFieldByName() {
        return this.metadataFieldByName;
    }

    public void setMetadataFieldByName(Map<String, MetadataField> metadataFieldByName) {
        this.metadataFieldByName = metadataFieldByName;
    }

    public Map<String, Set<String>> getPropertyByName() {
        return propertyByName;
    }

    public void setPropertyByName(Map<String, Set<String>> propertyByName) {
        this.propertyByName = propertyByName;
    }

    public Map<String, Set<String>> getTagsByName() {
        return tagsByName;
    }

    public void setTagsByName(Map<String, Set<String>> tagsByName) {
        this.tagsByName = tagsByName;
    }

    public Boolean isFireOncePerRule() {
        return fireOncePerRule;
    }

    public void isFireOncePerRule(Boolean fireOncePerRule) {
        this.fireOncePerRule = fireOncePerRule;
    }

    // Always return first value
    protected String getParameterByKey(String key) {
        Set<String> valueSet = this.propertyByName.get(key);

        if (valueSet != null && valueSet.size() > 0) {
            return valueSet.iterator().next();
        }

        return StringUtils.EMPTY;
    }

    protected Set<String> getParametersByKey(String key) {
        return this.propertyByName.get(key);
    }

    protected String getTagByKey(String key) {
        Set<String> valueSet = this.tagsByName.get(key);

        if (valueSet != null && valueSet.size() > 0) {
            return valueSet.iterator().next();
        }

        return StringUtils.EMPTY;
    }

    protected RightsManager setupSkyDRMSDK() {
		try {
			logger.debug("SKYDRM URL " + getSystemConfigs().get(SystemConfigKey.SKYDRM_ROUTER_URL));
			RightsManager rm = new RightsManager(getSystemConfigs().get(SystemConfigKey.SKYDRM_ROUTER_URL), Integer.parseInt(getSystemConfigs().get(SystemConfigKey.SKYDRM_APP_ID)), getSystemConfigs().get(SystemConfigKey.SKYDRM_APP_KEY));
			return rm;
		} catch (NumberFormatException e) {
			logger.error("Error parsing Application Key for SkyDRM. Check that the application key is set as an Integer", e);
			return null;
		} catch (NxlException e) {
			logger.error("Error setting up SkyDRMSDK. SkyDRMSDK Exception is: " + e.getMessage(), e);
			return null;
		}

	}

    protected Set<String> getTagsByKey(String key) {
        return this.tagsByName.get(key);
    }

    public RepositoryType getRepositoryType() {
        return this.repositoryType;
    }

    public void setRepositoryType(RepositoryType repositoryType) {
        this.repositoryType = repositoryType;
    }

    public String toString() {
        return this.name;
    }
    
    // Concrete class should override this implementation when necessary
    public void commit() {
    	// TODO: Implement actual commit action in concrete class
    }

    public abstract void rollback() throws RollbackException;

}
