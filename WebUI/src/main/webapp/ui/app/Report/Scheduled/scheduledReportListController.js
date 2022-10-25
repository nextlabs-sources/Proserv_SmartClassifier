mainApp.controller('scheduledReportListController', 
		['$scope', 
		 '$state', 
		 '$location', 
		 '$window', 
		 '$stateParams', 
		 '$filter', 
		 '$anchorScroll',
		 'loggerService', 
		 'configService', 
		 'dialogService', 
		 'scheduledReportService', 
		 function ($scope,
				   $state,
				   $location,
				   $window,
				   $stateParams,
				   $filter,
				   $anchorScroll,
				   loggerService,
				   configService, 
				   dialogService,
				   scheduledReportService) {
			var PAGE_SIZE = configService.configObject['defaultIDBResultPageSize'];
			$scope.$parent.isDetailsPage = false;
			
			$scope.refreshScheduledReportList = function() {
				$scope.scheduledReportList = [];
				$scope.total = 0;
				$scope.scheduledReportPageNumber = 1;
				
				scheduledReportService.getScheduledReportList($scope.scheduledReportPageNumber, function(response) {
					$scope.scheduledReportList = response.data;
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			};
			
			$scope.refreshScheduledReportList();
			
			$scope.loadMore = function() {
				$scope.scheduledReportPageNumber++;
				loggerService.getLogger().info("Page number " + $scope.scheduledReportPageNumber);
				
				scheduledReportService.getScheduledReportList($scope.scheduledReportPageNumber, function(response) {
					$scope.scheduledReportList = $scope.scheduledReportList.concat(response.data);
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			};
			
			$scope.createNewScheduledReport = function() {
				loggerService.getLogger().info("Go to creating new Scheduled Report");
				$state.go('scheduledReportDetails', {
					id : 'create'
				});
			};
			
			$scope.openScheduledReport = function(scheduledReport) {
				loggerService.getLogger().info("Openning scheduled report " + scheduledReport.id);
				$state.go('scheduledReportDetails', {
					id : scheduledReport.id
				});
			};
			
			$scope.duplicateScheduledReport = function(scheduledReport) {
				scheduledReport.name = scheduledReport.name + " - Duplicate";
				scheduledReportService.createScheduledReport(scheduledReport, function(response) {
					$scope.refreshScheduledReportList();
				});
			};
			
			$scope.deleteScheduledReport = function(scheduledReport, $event) {
				loggerService.getLogger().info("Deleting scheduled report " + scheduledReport.id);
				dialogService.confirm({
					msg : $filter('translate')('scheduledReport.del.confirm') + ": " + scheduledReport.name + "?",
					ok : function() {
						scheduledReportService.deleteScheduledReport(scheduledReport.id, scheduledReport.modifiedOn, function(response) {
							$scope.refreshScheduledReportList();
							loggerService.getLogger().debug(response);
						});
					}
				});
				
				$event.stopPropagation();
			};
			
			var scheduledReportWithOpenOption = [];
			$scope.closeAllOpenOption = function() {
				if (scheduledReportWithOpenOption.length > 0) {
					angular.forEach(scheduledReportWithOpenOption, function(scheduledReport) {
						loggerService.getLogger().debug("The scheduled report is = " + JSON.stringify(scheduledReport));
						if (scheduledReport.optionOpen) {
							scheduledReport.optionOpen = false;
						}
					});
					scheduledReportWithOpenOption = [];
				}
			};
			
			$scope.openOption = function(scheduledReport, open, $event) {
				if (angular.isDefined(open)) {
					scheduledReport.optionOpen = open;
					if (open) {
						$scope.closeAllOpenOption();
						autoCloseOptionService.close();
						autoCloseOptionService.registerOpen($scope, $scope.closeAllOpenOption);
						scheduledReportWithOpenOption.push(scheduledReport);
					} else {
						scheduledReportWithOpenOption = [];
					}
				} else {
					return angular.isDefined(scheduledReport.optionOpen) ? scheduledReport.optionOpen : false;
				}
			};
			
			$scope.isOptionOpen = function(scheduledReport) {
				if (scheduledReport.optionOpen) {
					return scheduledReport.optionOpen;
				} else {
					return false;
				}
			};
		}
	]
)