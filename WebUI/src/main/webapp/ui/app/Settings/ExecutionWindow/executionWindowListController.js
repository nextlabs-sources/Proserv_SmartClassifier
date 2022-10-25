mainApp.controller('executionWindowListController', ['$scope', '$state', 'loggerService', '$stateParams', '$filter', 'sharedService', 'autoCloseOptionService', 'executionWindowService',
		'configService', 'dialogService', function($scope, $state, loggerService, $stateParams, $filter, sharedService, autoCloseOptionService, executionWindowService, configService, dialogService) {
			var PAGE_SIZE = configService.configObject['defaultExecutionWindowPageSize'];
			$scope.$parent.isDetailsPage = false;

			$scope.refreshExecutionWindowList = function() {
				$scope.executionWindowPageNumber = 1;
				$scope.total = 0;
				var sortFields = [];
				executionWindowService.getExecutionWindowList($scope.executionWindowPageNumber, sortFields, function(response) {
					$scope.executionWindowList = response.data;
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			}

			$scope.refreshExecutionWindowList();

			$scope.loadMore = function() {
				var sortFields = [];
				$scope.executionWindowPageNumber++;
				loggerService.getLogger().info("Page number " + $scope.executionWindowPageNumber);
				executionWindowService.getExecutionWindowList($scope.executionWindowPageNumber, sortFields, function(response) {
					$scope.executionWindowList = $scope.executionWindowList.concat(response.data);
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			}

			$scope.returnScheduleString = function(executionWindow) {
				return executionWindowService.createScheduleString(executionWindow);
			}

			$scope.createNewExecutionWindow = function() {
				loggerService.getLogger().info("Go to creating new execution window");
				$state.go('ExecutionWindowDetails', {
					id : 'create'
				})
			}

			$scope.openExecutionWindow = function(executionWindow) {
				loggerService.getLogger().info("Openning execution window set " + executionWindow.id);
				sharedService.data.executionWindowDetails = executionWindow;
				$state.go('ExecutionWindowDetails', {
					id : executionWindow.id
				});
			}

			$scope.deleteExecutionWindow = function(executionWindow, $event) {
				loggerService.getLogger().info("Deleting execution window set, ID = " + executionWindow.id);
				dialogService.confirm({
					msg : $filter('translate')('execution.window.del.confirm') + ": " + executionWindow.name + "?",
					ok : function() {
						executionWindowService.deleteExecutionWindow(executionWindow.id, executionWindow.modifiedOn, function(response) {
							$scope.refreshExecutionWindowList();
							loggerService.getLogger().debug(response);
						});
					}
				})
				$event.stopPropagation();
			}

			var executionWindowWithOpenOption = [];
			
			$scope.closeAllOpenOption = function() {
				if (executionWindowWithOpenOption.length > 0) {
					angular.forEach(executionWindowWithOpenOption, function(executionWindow) {
						loggerService.getLogger().debug("The execution window is = " + JSON.stringify(executionWindow));
						if (executionWindow.optionOpen)
							executionWindow.optionOpen = false;
					});
					executionWindowWithOpenOption = [];
				} 
			};
			
			$scope.openOption = function(executionWindow, open, $event) {
				if (angular.isDefined(open)) {
					executionWindow.optionOpen = open;
					if (open) {
						$scope.closeAllOpenOption();
						autoCloseOptionService.close();
						autoCloseOptionService.registerOpen($scope, $scope.closeAllOpenOption);
						executionWindowWithOpenOption.push(executionWindow);
					} else {
						executionWindowWithOpenOption = [];
					}
				} else {
				  return angular.isDefined(executionWindow.optionOpen) ? executionWindow.optionOpen : false;
				}	
			};

			$scope.isOptionOpen = function(executionWindow) {
				if (executionWindow.optionOpen) {
					return executionWindow.optionOpen;
				} else {
					return false;
				}
			};

		}])