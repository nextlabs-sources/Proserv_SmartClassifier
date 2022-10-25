mainApp.controller('jmsListController', ['$scope', '$state', 'loggerService', '$stateParams', '$filter', 'autoCloseOptionService', 'jmsService', 'configService', 'dialogService',
		function($scope, $state, loggerService, $stateParams, $filter, autoCloseOptionService, jmsService, configService, dialogService) {
			var PAGE_SIZE = configService.configObject['defaultJMSPageSize'];
			$scope.$parent.isDetailsPage = false;

			$scope.refreshJMSList = function() {
				$scope.jmsPageNumber = 1;
				$scope.total = 0;
				var sortFields = [];
				jmsService.getJMSList($scope.jmsPageNumber, sortFields, function(response) {
					$scope.jmsList = response.data;
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(JSON.stringify(response));
				});
			}

			$scope.refreshJMSList();

			$scope.loadMore = function() {
				var sortFields = [];
				$scope.jmsPageNumber++;
				loggerService.getLogger().info("Page number " + $scope.jmsPageNumber);
				jmsService.getJMSList($scope.jmsPageNumber, sortFields, function(response) {
					$scope.jmsList = $scope.jmsList.concat(response.data);
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(JSON.stringify(response));
				});
			}

			$scope.createNewJMS = function() {
				loggerService.getLogger().info("Go to creating new jms");
				$state.go('JMSDetails', {
					id : 'create'
				})
			}

			$scope.openJMS = function(jms) {
				loggerService.getLogger().info("Openning jms " + jms.id);
				$state.go('JMSDetails', {
					id : jms.id
				});
			}

			$scope.deleteJMS = function(jms, $event) {
				loggerService.getLogger().info("Deleting jms " + jms.id);
				dialogService.confirm({
					msg : $filter('translate')('jms.del.confirm') + ": " + jms.displayName + "?",
					ok : function() {
						jmsService.deleteJMS(jms.id, jms.modifiedOn, function(response) {
							$scope.refreshJMSList();
							loggerService.getLogger().debug(JSON.stringify(response));
						});
					}
				})
				$event.stopPropagation();
			}

			var jmsWithOpenOption = [];
			$scope.closeAllOpenOption = function() {
				if (jmsWithOpenOption.length > 0) {
					angular.forEach(jmsWithOpenOption, function(jms) {
						loggerService.getLogger().debug("The JMS is = " + JSON.stringify(jms));
						if (jms.optionOpen)
							jms.optionOpen = false;
					});
					jmsWithOpenOption = [];
				}
			}
			$scope.openOption = function(jms, open, $event) {
				if (angular.isDefined(open)) {
					jms.optionOpen = open;
					if (open) {
						$scope.closeAllOpenOption();
						autoCloseOptionService.close();
						autoCloseOptionService.registerOpen($scope, $scope.closeAllOpenOption);
						jmsWithOpenOption.push(jms);
					} else {
						jmsWithOpenOption = [];
					}
				} else
					return angular.isDefined(jms.optionOpen) ? jms.optionOpen : false;
			};

			$scope.isOptionOpen = function(jms) {
				if (jms.optionOpen) {
					return jms.optionOpen;
				} else {
					return false;
				}
			}

		}])