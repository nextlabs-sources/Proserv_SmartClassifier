mainApp.controller('overviewController', ['$scope', '$state', 'loggerService', '$stateParams', '$filter', 'dialogService', 'summaryService',
    function ($scope, $state, loggerService, $stateParams, $filter, dialogService, summaryService) {

        $scope.$parent.isDetailsPage = false;
        $scope.serviceLabels = [$filter('translate')('dashboard.service.healthy'), '', $filter('translate')('dashboard.service.critical')];
        $scope.processLabels = [$filter('translate')('dashboard.process.success'), $filter('translate')('dashboard.process.fail')];

        $scope.getDashboardInfo = function () {

            summaryService.getSummary("document", function (response) {
                $scope.documentSummary = response.data;
                $scope.loadLicenseInfo();
            });

            summaryService.getSummary("watcher", function (response) {
                $scope.watcherSummary = response.data;
                $scope.criticalHeartbeat = response.criticalHeartBeat;
                $scope.loadWatcherInfo();
            });

            summaryService.getSummary("extractor", function (response) {
                $scope.extractorSummary = response.data;
                $scope.loadExtractorInfo();
            });

            summaryService.getSummary("ruleEngine", function (response) {
                $scope.ruleEngineSummary = response.data;
                $scope.loadRuleEngineInfo();
            });
        };

        $scope.getDashboardInfo();

        $scope.refresh = function () {
            $scope.getDashboardInfo();
        };

        $scope.loadLicenseInfo = function () {

            for (var i = 0; i < $scope.documentSummary.length; i++) {
                if ($scope.documentSummary[i].value) {
                    if ($scope.documentSummary[i].code == "Data size") {
                        console.log($scope.documentSummary[i].value);
                        if ($scope.documentSummary[i].value == "UNLIMITED") {
                            $scope.dataSize = $scope.documentSummary[i].value;
                        } else {
                            $scope.dataSize = Number($scope.documentSummary[i].value);
                        }
                        continue;
                    }

                    if ($scope.documentSummary[i].code == "License Validity") {
                        console.log($scope.documentSummary[i].value);
                        $scope.licenseValidity = $scope.documentSummary[i].value;
                        continue;
                    }

                    if ($scope.documentSummary[i].code == "Used size") {
                        $scope.usedSize = Number($scope.documentSummary[i].value) / 1024 / 1024 / 1024;
                        $scope.usedSize = $scope.usedSize.toFixed(2);

                        continue;
                    }
                    if ($scope.documentSummary[i].code == "Total document") {
                        $scope.totalDocs = Number($scope.documentSummary[i].value);
                        // $scope.totalDocs = 200000;
                        continue;
                    }
                    if ($scope.documentSummary[i].code == "Success extraction") {
                        $scope.successExtraction = Number($scope.documentSummary[i].value);
                        // $scope.successExtraction = 900;
                        continue;
                    }
                    if ($scope.documentSummary[i].code == "Fail extraction") {
                        $scope.failExtraction = Number($scope.documentSummary[i].value);
                        // $scope.failExtraction = 100;

                    }
                }
            }

            $scope.licenseFound = !($scope.dataSize == undefined || $scope.usedSize == undefined);

            $scope.processDetailsFound = true;
            if ($scope.totalDocs == undefined || $scope.totalDocs == 0 || $scope.successExtraction == undefined || $scope.failExtraction == undefined) {
                $scope.processDetailsFound = false;
            } else {
                var percentSuccess = Math.round($scope.successExtraction / ($scope.successExtraction + $scope.failExtraction) * 100);
                var percentFailed = 100 - percentSuccess;
                $scope.processDetails = [percentSuccess, percentFailed];
            }
        };

        $scope.loadWatcherInfo = function () {
            $scope.watcherInfo = {};

            for (var i = 0; i < $scope.watcherSummary.length; i++) {
                if ($scope.watcherSummary[i].value) {
                    if ($scope.watcherSummary[i].code == "Total") {
                        $scope.watcherInfo.total = $scope.watcherSummary[i].value;
                        continue;
                    }
                    if ($scope.watcherSummary[i].code == "Healthy") {
                        $scope.watcherInfo.healthy = $scope.watcherSummary[i].value;
                        continue;
                    }
                    if ($scope.watcherSummary[i].code == "Warning") {
                        $scope.watcherInfo.warning = $scope.watcherSummary[i].value;
                        continue;
                    }
                    if ($scope.watcherSummary[i].code == "Critical") {
                        $scope.watcherInfo.critical = $scope.watcherSummary[i].value;

                    }
                } else {
                    $scope.watcherInfo.missing = true;
                }
            }

            if ($scope.watcherInfo.total == 0) {
                $scope.watcherInfo.missing = true;
            }

            $scope.watcherData = [Number($scope.watcherInfo.healthy), 0, Number($scope.watcherInfo.critical)];
        };

        $scope.loadExtractorInfo = function () {
            $scope.extractorInfo = {};
            for (var i = 0; i < $scope.extractorSummary.length; i++) {
                if ($scope.extractorSummary[i].value) {
                    if ($scope.extractorSummary[i].code == "Total") {
                        $scope.extractorInfo.total = $scope.extractorSummary[i].value;
                        continue;
                    }
                    if ($scope.extractorSummary[i].code == "Healthy") {
                        $scope.extractorInfo.healthy = $scope.extractorSummary[i].value;
                        continue;
                    }
                    if ($scope.extractorSummary[i].code == "Warning") {
                        $scope.extractorInfo.warning = $scope.extractorSummary[i].value;
                        continue;
                    }
                    if ($scope.extractorSummary[i].code == "Critical") {
                        $scope.extractorInfo.critical = $scope.extractorSummary[i].value;

                    }
                } else {
                    $scope.extractorInfo.missing = true;
                }
            }

            if ($scope.extractorInfo.total == 0) {
                $scope.extractorInfo.missing = true;
            }

            $scope.extractorData = [Number($scope.extractorInfo.healthy), 0, Number($scope.extractorInfo.critical)];
        };

        $scope.loadRuleEngineInfo = function () {
            $scope.ruleEngineInfo = {};
            for (var i = 0; i < $scope.ruleEngineSummary.length; i++) {
                if ($scope.ruleEngineSummary[i].value) {
                    if ($scope.ruleEngineSummary[i].code == "Total") {
                        $scope.ruleEngineInfo.total = $scope.ruleEngineSummary[i].value;
                        continue;
                    }
                    if ($scope.ruleEngineSummary[i].code == "Healthy") {
                        $scope.ruleEngineInfo.healthy = $scope.ruleEngineSummary[i].value;
                        continue;
                    }
                    if ($scope.ruleEngineSummary[i].code == "Warning") {
                        $scope.ruleEngineInfo.warning = $scope.ruleEngineSummary[i].value;
                        continue;
                    }
                    if ($scope.ruleEngineSummary[i].code == "Critical") {
                        $scope.ruleEngineInfo.critical = $scope.ruleEngineSummary[i].value;

                    }
                } else {
                    $scope.ruleEngineInfo.missing = true;
                }
            }

            if ($scope.ruleEngineInfo.total == 0) {
                $scope.ruleEngineInfo.missing = true;
            }

            $scope.ruleEngineData = [Number($scope.ruleEngineInfo.healthy), 0, Number($scope.ruleEngineInfo.critical)];
        };

        $scope.goToService = function (type) {
            $state.go('scservices', {
                type: type
            });
        };

        $scope.getUsedSizeStyle = function () {

            if ($scope.dataSize == "UNLIMITED") {
                return "";
            } else {
                if ($scope.usedSize / $scope.dataSize < 0.5) {
                    return "";
                }

                if ($scope.usedSize / $scope.dataSize < 0.75) {
                    return "";
                }

                if ($scope.usedSize / $scope.dataSize <= 1) {
                    return "";
                }

                if ($scope.usedSize / $scope.dataSize > 1) {
                    return "danger";
                }
            }

        };

        $scope.getRemaining = function () {
            if ($scope.dataSize == "UNLIMITED") {
                return "UNLIMITED";
            }
            return ($scope.dataSize - $scope.usedSize).toFixed(2);
        }

    }]);