mainApp.controller('adHocReportListController', 
		['$scope', 
		 '$state', 
		 '$filter', 
		 'loggerService', 
		 'configService', 
		 'dialogService', 
		 'adHocReportService', 
		 function ($scope,
				   $state,
				   $filter,
				   loggerService,
				   configService, 
				   dialogService,
				   adHocReportService) {
			var PAGE_SIZE = configService.configObject['defaultIDBResultPageSize'];
			$scope.$parent.isDetailsPage = false;
			$scope.longDatetimeFormat = configService.configObject['long.datetime.format'];
			
			$scope.refreshReportList = function() {
				$scope.reportList = [];
				$scope.total = 0;
				$scope.reportPageNumber = 1;
				
				adHocReportService.getAdHocReportList($scope.reportPageNumber, function(response) {
					$scope.reportList = response.data;
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			};
			
			$scope.refreshReportList();
			
			$scope.loadMore = function() {
				$scope.reportPageNumber++;
				loggerService.getLogger().debug("Page number " + $scope.reportPageNumber);
				
				adHocReportService.getAdHocReportList($scope.reportPageNumber, function(response) {
					$scope.reportList = $scope.reportList.concat(response.data);
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
				});
			};
			
			$scope.openReport = function(adHocReport) {
				if(adHocReport.id) {
					$state.go("adHocReportDetails", {
						id : adHocReport.id
					});
				}
			};
			
			$scope.deleteReport = function(adHocReport, $event) {
				loggerService.getLogger().info("Deleting ad-hoc report " + adHocReport.id);
				dialogService.confirm({
					msg : $filter('translate')('adHocReport.del.confirm') + ": " + adHocReport.name + "?",
					ok : function() {
						adHocReportService.deleteAdHocReport(adHocReport.id, adHocReport.modifiedOn, function(response) {
							$scope.refreshReportList();
						});
					}
				});
				
				$event.stopPropagation();
			};
		}
	]
)