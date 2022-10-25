mainApp.controller('indexDatabaseQueryController', [
    '$scope',
    '$location',
    'loggerService',
    '$filter',
    '$stateParams',
    'generalService',
    'ruleService',
    '$state',
    'sharedService',
    'indexDatabaseQueryService',
    'configService',
    function ($scope, $location, loggerService, $filter, $stateParams, generalService, ruleService, $state, sharedService, indexDatabaseQueryService, configService) {
        $scope.$parent.isDetailsPage = false;
        $scope.ruleID = $stateParams.id;
        $scope.sortOrders = configService.getSortOrders();
        $scope.sortOrder = $scope.sortOrders[0];

        if (!$scope.ruleID || $scope.ruleID.length === 0) {
            $scope.ruleID = 'custom';
        }

        // criteria variables
        ruleService.getMatchingConditions(function (data) {
            $scope.matchingConditions = data;
        });

        ruleService.getMetaDataMatchingConditions(function (data) {
            $scope.metaDataMatchingConditions = data
        });

        ruleService.getDocumentMatchingConditions(function (data) {
            $scope.documentMatchingConditions = data;
        });

        ruleService.getDataProviders(function (data) {
            $scope.dataProviders = data;
        });

        $scope.dataSections = ruleService.getDataSections();

        ruleService.getRepositoryTypes(function (data) {
            $scope.repositoryTypes = data;
        });

        ruleService.getMetaDataList(function (data) {
            $scope.metaDataList = data;
        });

        // add criteria groups
        $scope.addCriteriaGroup = function () {
            if (!$scope.query) {
                $scope.query = {};
            }
            if (!$scope.query.criteria) {
                $scope.query.criteria = [];
            }

            $scope.query.criteria.push({
                "criterias": [{
                    "displayOrder": 1,
                    "dataSection": "A",
                    "fieldName": "",
                    "matchingCondition": "CONTAINS",
                    "value": null
                }],
                "displayOrder": $scope.query.criteria.length + 1,
                "operator": "AND"
            })
        };

        // add criteria
        $scope.addCriteria = function (cg) {
            if (!cg.criterias) {
                cg.criterias = [];
            }

            cg.criterias.push({
                "displayOrder": cg.criterias.length + 1,
                "dataSection": "A",
                "fieldName": "",
                "matchingCondition": "CONTAINS",
                "value": null
            });
        };

        if (sharedService.data.query) {
            $scope.query = angular.copy(sharedService.data.query);
        } else {
            if ($scope.ruleID === 'custom') {
                $scope.query = {
                    "repositoryType": "SHARED FOLDER"
                };
                //$scope.query.repositoryType = "SHARED FOLDER";
                $scope.addCriteriaGroup();
            } else {
                ruleService.getRule($scope.ruleID, function (response) {
                    $scope.query = {
                        "repositoryType": response.data.repositoryType,
                        "criteria": response.data.criteriaGroups,
                        "directories": response.data.directories
                    }
                })
            }
        }

        // remove criteria group
        $scope.removeCriteriaGroup = function (index) {
            $scope.query.criteria.splice(index, 1);
        };

        // remove criteria
        $scope.removeCriteria = function (cg, index, cindex) {
            cg.criterias.splice(cindex, 1);
            if (cg.criterias.length === 0) {
                $scope.query.criteria.splice(index, 1);
            }
        };

        $scope.removeCriteriaGroup = function (index) {
            $scope.query.criteria.splice(index, 1);
        };

        $scope.changeDataSection = function (cri, section) {
            cri.dataSection = section;
            cri.matchingCondition = "CONTAINS";
            cri.value = null;
            if (section !== 'M') {
                cri.fieldName = "";
                cri.field = "";
            }
        };

        $scope.changeMatchingCondition = function (cri, condition) {
            cri.matchingCondition = condition.code;
            cri.value = null;
        };

        $scope.changeMetaMatchingCondition = function (cri, condition) {
            cri.matchingCondition = condition.code;
            cri.value = null;
            if (cri.fieldName) {
                if ($scope.isStringCriteria(cri)) {
                    cri.fieldName = cri.field;
                } else if ($scope.isDateCriteria(cri)) {
                    cri.date = new Date();
                    cri.dateOpened = false;
                    cri.openDate = function () {
                        cri.dateOpened = true;
                    };
                    cri.value = cri.date.getTime();
                    cri.fieldName = cri.field;
                } else if ($scope.isNumberCriteria(cri)) {
                    cri.fieldName = cri.field;
                } else {
                    cri.fieldName = cri.field;
                }
            } else {
                if ($scope.isDateCriteria(cri)) {
                    cri.date = new Date();
                    cri.value = cri.date.getTime();
                    cri.dateOpened = false;
                    cri.openDate = function () {
                        cri.dateOpened = true;
                    }
                }
            }
        };

        $scope.updateDateCriteria = function (cri) {
            if (cri.date) {
                cri.value = cri.date.getTime();
            }
        };

        $scope.updateMetaField = function (cri) {
            if (cri.field) {
                cri.fieldName = cri.field;
            } else {
                cri.fieldName = null;
            }
        };

        $scope.changeMetadataField = function (cri, meta) {
            cri.field = meta.value;
            cri.fieldName = meta.value;
            cri.fieldCode = meta.code;

            if (meta.uiDataType === "DATE") {
                cri.matchingCondition = "DATE_EQUALS";
                cri.date = new Date();
                cri.dateOpened = false;
                cri.openDate = function () {
                    cri.dateOpened = true;
                };
                cri.value = cri.date.getTime();
            } else if (meta.uiDataType === "INTEGER") {
                cri.matchingCondition = "NUM_EQUALS";
                cri.value = null;
            } else {
                cri.matchingCondition = "CONTAINS";
                cri.value = null;
            }
        };

        $scope.resetMetaMatchingCondition = function (cri) {
            cri.fieldCode = undefined;
        };

        $scope.hideMetaMatchingCondition = function (cri, condition) {
            if (!cri.fieldCode) {
                return false;
            }

            if (generalService.stringStartWith(condition, 'DATE')) {
                if (generalService.stringStartWith(cri.matchingCondition, 'DATE')) {
                    return false;
                }
            } else if (generalService.stringStartWith(condition, "NUM")) {
                if (generalService.stringStartWith(cri.matchingCondition, 'NUM')) {
                    return false;
                }
            } else if (condition === 'NOT' || condition === 'CONTAINS' || condition === 'NOT_CONTAINS' || condition === 'MUST_CONTAINS') {
                if (cri.matchingCondition === 'NOT' || cri.matchingCondition === 'CONTAINS' || cri.matchingCondition === 'NOT_CONTAINS' || cri.matchingCondition === 'MUST_CONTAINS') {
                    return false;
                }
            }

            return true;
        };

        $scope.isMetaData = function (cri, cg) {
            if (cri.dataSection === 'M') {
                cri.isMetaData = true;
                if (cri.fieldName && !cri.field) {
                    if (cri.fieldDisplayName) {
                        cri.field = cri.fieldDisplayName;
                        cri.fieldCode = cri.fieldName;
                        cri.fieldName = cri.fieldDisplayName;
                    } else {
                        cri.field = cri.fieldName;
                    }
                }
                return true;
            } else {
                cri.isMetaData = false;
                return false;
            }
        };

        $scope.hasMetaData = function (cg) {
            for (var i = 0; i < cg.criterias.length; i++) {
                if (cg.criterias[i].isMetaData) {
                    return true;
                }
            }
            return false;
        };

        $scope.isDateCriteria = function (cri) {
            var isDate = (generalService.stringStartWith(cri.matchingCondition, 'DATE'));
            if (isDate) {
                cri.openDate = function () {
                    cri.dateOpened = true;
                };
                if (!cri.date) {
                    cri.dateOpened = false;
                    if (cri.value) {
                        cri.date = new Date(Number(cri.value));
                    } else {
                        cri.date = new Date();
                        cri.value = cri.date.getTime();
                    }
                }
            }
            return isDate;
        };

        $scope.isNumberCriteria = function (cri) {
            return (generalService.stringStartWith(cri.matchingCondition, 'NUM'));
        };

        $scope.isStringCriteria = function (cri) {
            return (cri.matchingCondition === 'NOT' || cri.matchingCondition === 'NOT_CONTAINS' || cri.matchingCondition === 'MUST_CONTAINS' || cri.matchingCondition === 'CONTAINS');
        };

        $scope.checkDataProvider = function (value, viewValue) {
            loggerService.getLogger().debug(value + " " + viewValue);
            if (value && viewValue) {
                return generalService.stringStartWith(value, viewValue);
            } else {
                return false;
            }
        };

        $scope.changeDataProvider = function (cri, dp) {
            cri.value = dp.suggestion;
        };

        // go back to list
        $scope.backToRule = function () {
            $state.go('ruleDetails', {
                id: $scope.ruleID
            });
        };

        $scope.scrollTo = function (target) {
            $location.hash(target);
            $anchorScroll();
            /* $location.hash('resourceInfo', ''); */
            currentTarget = target;
        };

        $scope.querySolr = function (form) {
            // to be implemented

            if (form.$invalid) {
                form.$setDirty();
                for (var field in form) {
                    if (field[0] == '$')
                        continue;
                    form[field].$touched = true;
                    form[field].$dirty = true;
                }
                return;
            }

            // process directories
            angular.forEach($scope.query.directories, function (group, index) {
                angular.forEach(group.criterias, function (dir, index) {
                    $scope.checkDirectory(dir);
                });
            });

            sharedService.data.query = angular.copy($scope.query);
            $scope.executeSolrQuery();
        };

        // directories function
        $scope.addDirectoryGroup = function () {
            if (!$scope.query.directories || $scope.query.directories.length === 0) {
                $scope.query.directories = [{
                    "operator": "AND",
                    "criterias": [{
                        "dataSection": "D",
                        "matchingCondition": "CONTAINS",
                        "value": ""
                    }]
                }];
            } else {
                $scope.query.directories.push({
                    "operator": "AND",
                    "criterias": [{
                        "dataSection": "D",
                        "matchingCondition": "CONTAINS",
                        "value": ""
                    }]
                });
            }
        };

        $scope.removeDirectoryGroup = function (index) {
            $scope.query.directories.splice(index, 1);
        };

        $scope.addDirectory = function (directoryGroup) {
            if (!directoryGroup.criterias) {
                directoryGroup.criterias = [];
            }

            directoryGroup.criterias.push({
                "dataSection": "D",
                "matchingCondition": "CONTAINS",
                "value": ""
            });
        };

        $scope.removeDirectory = function (directoryGroup, index, dindex) {
            directoryGroup.criterias.splice(dindex, 1);

            if (directoryGroup.criterias.length === 0) {
                $scope.query.directories.splice(index, 1);
            }
        };

        $scope.changeDirectoryMatchingCondition = function (directory, matchingCondition) {
            directory.matchingCondition = matchingCondition.code;
        };

        $scope.checkDirectory = function (dir) {

            if (!dir.value) {
                return;
            }
            if (dir.value.slice(dir.value.length - 2, dir.value.length) != ("\\*")
                && (dir.value.slice(dir.value.length - 1, dir.value.length) != ("\\") && dir.value.slice(dir.value.length - 1, dir.value.length) != ("/"))
                && dir.value.slice(dir.value.length - 2, dir.value.length) != ("/*")) {
                if ($scope.query.repositoryType === "SHAREPOINT") {
                    dir.value += "/";
                } else {
                    dir.value += "\\";
                }
            }
        };

        $scope.executeSolrQuery = function () {
            $scope.resultList = [];
            $scope.total = 0;
            $scope.resultPageNumber = 1;

            indexDatabaseQueryService.getSortFields(function (response) {
                $scope.sortByFields = response.data;

                loggerService.getLogger().debug("Sort fields from service " + $scope.sortByFields);
                loggerService.getLogger().debug(response);

                if ($scope.query.repositoryType === "SHARED FOLDER") {
                    loggerService.getLogger().debug("Inside $scope.query.repositoryType === SHARED FOLDER");
                    for (var i = 0; i < $scope.sortByFields.length; i++) {
                        if ($scope.sortByFields[i].code === "folder_url") {
                            loggerService.getLogger().debug("Removing folder_url");
                            $scope.sortByFields.splice(i, 1);
                        }
                    }
                } else if ($scope.query.repositoryType === "SHAREPOINT") {
                    loggerService.getLogger().debug("Inside $scope.query.repositoryType === SHAREPOINT");
                    for (i = 0; i < $scope.sortByFields.length; i++) {
                        if ($scope.sortByFields[i].code === "directory_lcase") {
                            loggerService.getLogger().debug("Removing directory_lcase");
                            $scope.sortByFields.splice(i, 1);
                        }
                    }
                }
                loggerService.getLogger().debug(JSON.stringify($scope.sortByFields));

                if (!$scope.sortByFields) {
                    $scope.sortByFields = [{
                        "code": "modifiedOn",
                        "value": "Last Updated"
                    }];
                }

                $scope.sortBy = $scope.sortByFields[0];
                var sortFields = [{
                    "field": $scope.sortBy.code, /*document_name_lcase*/
                    "order": $scope.sortOrder.value /*Ascending*/
                }];

                indexDatabaseQueryService.executeQuery($scope.query, $scope.resultPageNumber, sortFields, function (response) {
                    $scope.resultList = response.data;
                    $scope.total = response.totalNoOfRecords;
                    sharedService.data.solrResult = angular.copy($scope.resultList);
                    sharedService.data.solrTotal = angular.copy($scope.total);
                    sharedService.data.sortByFields = angular.copy($scope.sortByFields);
                    //sharedService.data.repositoryType = angular.copy($scope.query.repositoryType);
                    $state.go("indexDatabaseQueryResult", {
                        id: $scope.ruleID
                    });
                });
            })
        };

        $scope.changeRepositoryType = function (repositoryType) {
            $scope.query.repositoryType = repositoryType;
        }
    }]);