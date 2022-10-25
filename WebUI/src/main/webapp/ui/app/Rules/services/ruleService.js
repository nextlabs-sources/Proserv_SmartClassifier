mainApp.factory('ruleService', ['networkService', 'loggerService', 'configService', '$filter', 'statusCodeService', 'collectionService',
    function (networkService, loggerService, configService, $filter, statusCodeService, collectionService) {

        var PAGE_SIZE = configService.configObject['defaultRulePageSize'];
        var matchingConditions = [];
        var metaDataMatchingConditions = [];
        var documentMatchingConditions = [];
        var dataProviders = [];
        var dataSections = ["D", "N", "H", "C", "F", "A", "M"];
        var metaDataList = [];
        var repositoryTypes = [];
        var sharePointPlugins = [];
        var sharedFolderPlugins = [];

        var getSortFields = function (callback) {
            networkService.get(configService.getUrl("rule.sortFields"), function (data) {
                statusCodeService.list("rule sort fields", callback, data);
            });
        };

        var getMetaDataList = function (callback) {
            if (metaDataList && metaDataList.length > 0) {
                callback(metaDataList);
            }
            collectionService.getCollection("MetadataFieldName", function (response) {
                if (callback) {
                    callback(response.data);
                }
                metaDataList = response.data;
            });
        };

        var getRepositoryTypes = function (callback) {
            if (repositoryTypes && repositoryTypes.length > 0) {
                callback(repositoryTypes);
            }
            collectionService.getCollection("RepositoryType", function (response) {
                if (callback) {
                    callback(response.data);
                }
                repositoryTypes = response.data;
            });
        };

        var getRuleList = function (pageNo, sortFields, callback) {
            loggerService.getLogger().info("Get rule list with page number " + pageNo + " and page size " + PAGE_SIZE);
            var requestData = {
                'sortFields': sortFields,
                'pageNo': pageNo,
                'pageSize': PAGE_SIZE
            };

            networkService.post(configService.getUrl("rule.list"), requestData, function (data) {
                statusCodeService.list("Rule", callback, data);
            });
        };
        
        var importRule = function(file, callback) {
        	loggerService.getLogger().info("Import rule from json file.");
        	
        	networkService.upload(configService.getUrl("rule.import"), file, function (data) {
        		statusCodeService.importData("Rule", callback, data);
        	});
        };
        
        var exportRule = function(callback) {
            var requestData = {
            };
        	
            networkService.post(configService.getUrl("rule.export"), requestData, function (data) {
        		statusCodeService.exportData("Rule", callback, data);
        	});
        };

        var getRule = function (ruleID, callback) {
            loggerService.getLogger().info("Get rule with ID " + ruleID);

            networkService.get(configService.getUrl("rule.get") + ruleID, function (data) {
                statusCodeService.get("Rule", callback, data);
            });
        };

        var createRule = function (rule, callback) {
            loggerService.getLogger().info("Creating rule with details " + rule);
            var requestData = rule;
            networkService.post(configService.getUrl("rule.add"), requestData, function (data) {
                statusCodeService.create("Rule", callback, data);
            })
        };

        var modifyRule = function (rule, callback) {
            loggerService.getLogger().info("Modifying rule  " + rule.id);

            var requestData = rule;

            networkService.put(configService.getUrl("rule.modify"), requestData, function (data) {
                statusCodeService.modify("Rule", callback, data);
            });
        };

        var deleteRule = function (id, lastModifiedDate, callback) {
            loggerService.getLogger().info("Deleting rule " + id);

            networkService.del(configService.getUrl("rule.delete") + id + "/" + lastModifiedDate, function (data) {
                statusCodeService.del("Rule", callback, data);
            })
        };

        var executeRule = function (id, callback) {
            loggerService.getLogger().info("Executing rule " + id);

            networkService.post(configService.getUrl("rule.execute") + id, null, function (data) {
                statusCodeService.execute("Rule execution", callback, data);
            });
        };

        var getMatchingConditions = function (callback) {
            if (matchingConditions && matchingConditions.length > 0) {
                callback(matchingConditions);
            }
            collectionService.getCollection("MatchingCondition", function (response) {
                if (callback) {
                    callback(response.data);
                }
                matchingConditions = response.data;
            });

        };

        var getMetaDataMatchingConditions = function (callback) {
            if (metaDataMatchingConditions && metaDataMatchingConditions.length > 0) {
                callback(metaDataMatchingConditions);
            }
            collectionService.getCollection("MetaDataMatchingCondition", function (response) {
                if (callback) {
                    callback(response.data);
                }
                metaDataMatchingConditions = response.data;
            });

        };
        
        var getDocumentMatchingConditions = function (callback) {
            if (documentMatchingConditions && documentMatchingConditions.length > 0) {
                callback(documentMatchingConditions);
            }
            collectionService.getCollection("DocumentMatchingCondition", function (response) {
                if (callback) {
                    callback(response.data);
                }
                documentMatchingConditions = response.data;
            });
        };
        
        var getDataProviders = function (callback) {
            if (dataProviders && dataProviders.length > 0) {
                callback(dataProviders);
            }
            collectionService.getCollection("DataProvider", function (response) {
                if (callback) {
                    callback(response.data);
                }
                dataProviders = response.data;
            });
        };

        var getSharePointPlugins = function (callback) {
            if (sharePointPlugins && sharePointPlugins.length > 0) {
                callback(sharePointPlugins);
            }
            collectionService.getCollection("SharePointPlugin", function (response) {
                if (callback) {
                    callback(response.data);
                }
                sharePointPlugins = response.data;
            });
        };

        var getSharedFolderPlugins = function (callback) {
            if (sharedFolderPlugins && sharedFolderPlugins.length > 0) {
                callback(sharedFolderPlugins);
            }
            collectionService.getCollection("SharedFolderPlugins", function (response) {
                if (callback) {
                    callback(response.data);
                }
                sharedFolderPlugins = response.data;
            });
        };

        var getDataSections = function () {
            return dataSections;
        };

        return {
            getRuleList: getRuleList,
            getRule: getRule,
            modifyRule: modifyRule,
            getSortFields: getSortFields,
            deleteRule: deleteRule,
            getMatchingConditions: getMatchingConditions,
            getMetaDataMatchingConditions: getMetaDataMatchingConditions,
            getDocumentMatchingConditions: getDocumentMatchingConditions,
            getDataProviders: getDataProviders,
            getRepositoryTypes: getRepositoryTypes,
            getSharedFolderPlugins: getSharedFolderPlugins,
            getSharePointPlugins: getSharePointPlugins,
            getDataSections: getDataSections,
            importRule : importRule,
            exportRule : exportRule,
            createRule: createRule,
            getMetaDataList: getMetaDataList,
            executeRule: executeRule
        }

    }]);