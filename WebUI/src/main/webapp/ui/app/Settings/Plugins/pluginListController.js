mainApp.controller('pluginListController', ['$scope', '$state', 'loggerService', '$stateParams', '$filter', 'sharedService', 'autoCloseOptionService', 'configService', 'pluginService',
    function ($scope, $state, loggerService, $stateParams, $filter, sharedService, autoCloseOptionService, configService, pluginService) {

        var PAGE_SIZE = configService.configObject['defaultPluginsPageSize'];

        $scope.$parent.isDetailsPage = false;
        $scope.refreshPluginsList = function () {
            $scope.pluginsPageNumber = 1;
            $scope.total = 0;
            var sortFields = [{"field": "displayName", "order": "asc"}];
            pluginService.getPluginList($scope.pluginsPageNumber, sortFields, function (response) {
                $scope.pluginsList = response.data;
                $scope.total = response.totalNoOfRecords;
                $scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
                loggerService.getLogger().info("The response =");
                loggerService.getLogger().info(JSON.stringify(response));
            });
        };

        $scope.refreshPluginsList();

        $scope.loadMore = function () {
            var sortFields = [];
            $scope.pluginsPageNumber++;
            loggerService.getLogger().log("Page number " + $scope.pluginsPageNumber);
            pluginService.getPluginList($scope.pluginsPageNumber, sortFields, function (response) {
                $scope.pluginsList = $scope.pluginsList.concat(response.data);
                $scope.total = response.totalNoOfRecords;
                $scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
                loggerService.getLogger().debug(JSON.stringify(response));
            });
        };

        $scope.openOption = function (plugins, open, $event) {
            if (angular.isDefined(open)) {
                plugins.optionOpen = open;
                if (open) {
                    $scope.closeAllOpenOption();
                    autoCloseOptionService.close();
                    autoCloseOptionService.registerOpen($scope, $scope.closeAllOpenOption);
                    pluginsWithOpenOption.push(plugins);
                } else {
                    pluginsWithOpenOption = [];
                }
            } else
                return angular.isDefined(plugins.optionOpen) ? plugins.optionOpen : false;
        };

        $scope.openPlugin = function (plugins) {
            loggerService.getLogger().info("Opening plugin " + plugins.id);
            sharedService.data.pluginDetails = plugins;
            $state.go('PluginDetails', {
                id: plugins.id
            });
        };

        $scope.isOptionOpen = function (plugins) {
            if (plugins.optionOpen) {
                return plugins.optionOpen;
            } else {
                return false;
            }
        };

        $scope.getDateFormat = function () {
            loggerService.getLogger().log(configService.configObject['date.format']);
            return configService.configObject['date.format']
        };

        var pluginsWithOpenOption = [];
        $scope.closeAllOpenOption = function () {
            if (pluginsWithOpenOption.length > 0) {
                angular.forEach(pluginsWithOpenOption, function (plugins) {
                    loggerService.getLogger().log("The plugins object = " + JSON.stringify(plugins));
                    if (plugins.optionOpen)
                        plugins.optionOpen = false;
                });
                pluginsWithOpenOption = [];
            }
        }
    }]);