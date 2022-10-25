mainApp.controller('servicesListController', ['$scope', '$state', 'loggerService', '$window', '$stateParams', '$filter', 'sharedService', 'autoCloseOptionService', 'scServicesService',
		'configService', function($scope, $state, loggerService, $window, $stateParams, $filter, sharedService, autoCloseOptionService, scServicesService, configService) {
			var PAGE_SIZE = configService.configObject['defaultServicePageSize'];
			$scope.$parent.isDetailsPage = false;
			$scope.serviceType = $stateParams.type;
			loggerService.getLogger().info("Service type is " + $scope.serviceType);

			// sorting variables
			// currently hard coded data
			$scope.sortByFields = scServicesService.getSortFields();
			$scope.sortOrders = configService.getSortOrders();
			$scope.sortOrder = $scope.sortOrders[0];
			$scope.sortBy = $scope.sortByFields[0];
			var sortFields = [{
				"field" : $scope.sortBy.field,
				"order" : $scope.sortOrder.value
			}];

			if ($scope.serviceType == "watcher") {
				$scope.title = $filter('translate')('TAB.SCServices.Watchers');
			} else if ($scope.serviceType == "extractor") {
				$scope.title = $filter('translate')('TAB.SCServices.Extractors');
			} else {
				$scope.title = $filter('translate')('TAB.SCServices.RuleEngine');
			}

			$scope.refreshServiceList = function(sortFields) {
				$scope.serviceList = [];
				$scope.paginator = [];
				$scope.total = 0;
				$scope.servicePageNumber = 1;
				scServicesService.getServiceList($scope.serviceType, $scope.servicePageNumber, sortFields, function(serviceList) {
					$scope.serviceList = serviceList.data;
					$scope.total = serviceList.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(serviceList);

					/*
					 * if ($scope.total > 0) { for (i = 1; i <=
					 * $scope.numberOfPage; i++) { $scope.paginator .push(i); } }
					 */
				});
			}

			$scope.refreshServiceList(sortFields);

			$scope.sortByField = function(sortBy) {

				var sortFields = [{
					"field" : sortBy.field,
					"order" : $scope.sortOrder.value
				}];
				$scope.sortBy = sortBy;

				$scope.refreshServiceList(sortFields)
			}

			$scope.sortByOrder = function(sortOrder) {
				var sortFields = [{
					"field" : $scope.sortBy.field,
					"order" : sortOrder.value
				}];
				$scope.sortOrder = sortOrder

				$scope.refreshServiceList(sortFields)
			}

			$scope.loadMore = function() {
				var sortFields = [{
					"field" : $scope.sortBy.field,
					"order" : $scope.sortOrder.value
				}];
				$scope.servicePageNumber++;
				loggerService.getLogger().debug($scope.servicePageNumber);
				scServicesService.getServiceList($scope.serviceType, $scope.servicePageNumber, sortFields, function(serviceList) {
					$scope.serviceList = $scope.serviceList.concat(serviceList.data);
					$scope.total = serviceList.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(serviceList);

					/*
					 * if ($scope.total > 0) { for (i = 1; i <=
					 * $scope.numberOfPage; i++) { $scope.paginator .push(i); } }
					 */
				});
			}

			// pagination starts - if needed
			$scope.goToPage = function(index) {
				$scope.servicePageNumber = index;
				$scope.refreshServiceList();
			}

			$scope.nextPage = function() {
				if ($scope.servicePageNumber != $scope.numberOfPage) {
					$scope.servicePageNumber++;
					$scope.refreshServiceList();
				}
			}

			$scope.previousPage = function() {
				if ($scope.servicePageNumber != 1) {
					$scope.servicePageNumber--;
					$scope.refreshServiceList();
				}
			}
			// pagination ends

			$scope.openService = function(service) {
				loggerService.getLogger().info("Openning " + $scope.serviceType + " " + service.name);
				sharedService.data.serviceDetails = service;
				$state.go('serviceDetails', {
					type : $scope.serviceType,
					id : service.id
				});
			}

			$scope.getServiceClass = function(service) {
				return 'sc-status-' + service.status;
			}

			var serviceWithOpenOption = [];
			$scope.closeAllOpenOption = function() {
				if (serviceWithOpenOption.length > 0) {
					angular.forEach(serviceWithOpenOption, function(service) {
						loggerService.getLogger().debug("The service object is = " + JSON.stringify(service))
						if (service.optionOpen)
							service.optionOpen = false;
					});
					serviceWithOpenOption = [];
				}
			}
			$scope.openOption = function(service, open, $event) {
				if (angular.isDefined(open)) {
					service.optionOpen = open;
					if (open) {
						$scope.closeAllOpenOption();
						autoCloseOptionService.close();
						autoCloseOptionService.registerOpen($scope, $scope.closeAllOpenOption);
						serviceWithOpenOption.push(service);
					} else {
						serviceWithOpenOption = [];
					}
				} else
					return angular.isDefined(service.optionOpen) ? service.optionOpen : false;
			};

			$scope.isOptionOpen = function(service) {
				if (service.optionOpen) {
					return service.optionOpen;
				} else {
					return false;
				}
			}

		}])