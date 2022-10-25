mainApp.controller('indexDatabaseQueryResultController', ['$scope', 'loggerService', '$filter', '$stateParams', 'generalService', 'configService', '$state', "sharedService",
    'indexDatabaseQueryService', function ($scope, loggerService, $filter, $stateParams, generalService, configService, $state, sharedService, indexDatabaseQueryService) {
        $scope.$parent.isDetailsPage = true;
        $scope.ruleID = $stateParams.id;
        $scope.longDatetimeFormat = configService.configObject['long.datetime.format'];
        $scope.sortOrders = configService.getSortOrders();
        $scope.sortOrder = $scope.sortOrders[0];

        $scope.executeSolrQuery = function (sortFields) {

            $scope.resultList = [];
            $scope.total = 0;
            $scope.resultPageNumber = 1;
            $scope.totalSortFields = 0;

            if (!sortFields) {
                indexDatabaseQueryService.getSortFields(function (response) {
                    $scope.sortByFields = response.data;
                    loggerService.getLogger().debug(JSON.stringify(response.data));
                    loggerService.getLogger().info("Sort fields from service " + $scope.sortByFields);

                    if (!$scope.sortByFields) {
                        $scope.sortByFields = [{
                            "code": "modifiedOn",
                            "value": "Last Updated"
                        }];
                    }
                    $scope.sortBy = $scope.sortByFields[0];
                    var sortFields = [{
                        "field": $scope.sortBy.code,
                        "order": $scope.sortOrder.value
                    }];

                    indexDatabaseQueryService.executeQuery($scope.query, $scope.resultPageNumber, sortFields, function (response) {
                        $scope.resultList = response.data;
                        $scope.total = response.totalNoOfRecords;
                        loggerService.getLogger().debug(response);
                    });
                })
            } else {
                indexDatabaseQueryService.executeQuery($scope.query, $scope.resultPageNumber, sortFields, function (response) {
                    $scope.resultList = response.data;
                    $scope.total = response.totalNoOfRecords;
                    loggerService.getLogger().debug(JSON.stringify(response));
                });
            }
        };

        if (sharedService.data.query) {
            $scope.query = angular.copy(sharedService.data.query);
            if (sharedService.data.solrResult && sharedService.data.solrTotal && sharedService.data.sortByFields) {
                $scope.resultList = angular.copy(sharedService.data.solrResult);
                $scope.total = angular.copy(sharedService.data.solrTotal);
                $scope.sortByFields = angular.copy(sharedService.data.sortByFields);
                $scope.sortBy = $scope.sortByFields[0];
                $scope.resultPageNumber = 1;
            } else {
                $scope.executeSolrQuery();
            }
        } else {
            $state.go("indexDatabaseQuery", {
                id: 'custom'
            });
        }

        $scope.sortByField = function (sortBy) {

            var sortFields = [{
                "field": sortBy.code,
                "order": $scope.sortOrder.value
            }];
            $scope.sortBy = sortBy;

            $scope.executeSolrQuery(sortFields);
        };

        $scope.sortByOrder = function (sortOrder) {
            var sortFields = [{
                "field": $scope.sortBy.code,
                "order": sortOrder.value
            }];
            $scope.sortOrder = sortOrder;

            $scope.executeSolrQuery(sortFields);
        };

        $scope.loadMore = function () {
            var sortFields = [{
                "field": $scope.sortBy.code,
                "order": $scope.sortOrder.value
            }];
            $scope.resultPageNumber++;
            indexDatabaseQueryService.executeQuery($scope.query, $scope.resultPageNumber, sortFields, function (response) {
                $scope.resultList = $scope.resultList.concat(response.data);
                $scope.total = response.totalNoOfRecords;
                loggerService.getLogger().debug(JSON.stringify(response));
            });
        };

        $scope.editQuery = function () {
            $state.go("indexDatabaseQuery", {
                id: $scope.ruleID
            })
        };

        $scope.isDateCriteria = function (cri) {
            var isDate = (generalService.stringStartWith(cri.matchingCondition, 'DATE'));
            return isDate;
        }

    }]);