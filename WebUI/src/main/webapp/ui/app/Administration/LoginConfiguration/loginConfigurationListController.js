mainApp.controller('loginConfigurationListController', 
	  ['$scope',
	   '$state', 
	   '$window', 
	   '$filter', 
	   'loggerService', 
	   'sharedService', 
	   'autoCloseOptionService', 
	   'loginConfigurationService', 
	   'configService', 
	   'dialogService',
	   function($scope,
			   $state, 
			   $window, 
			   $filter, 
			   loggerService, 
			   sharedService, 
			   autoCloseOptionService, 
			   loginConfigurationService, 
			   configService, 
			   dialogService) {
			var PAGE_SIZE = configService.configObject['defaultIDBResultPageSize'];
			$scope.$parent.isDetailsPage = false;
			
			$scope.refreshLoginConfigurationList = function() {
				$scope.loginConfigurationList = [];
				$scope.total = 0;
				$scope.loginConfigurationPageNumber = 1;
				
				loginConfigurationService.getLoginConfigurationList($scope.loginConfigurationPageNumber, function(response) {
					$scope.loginConfigurationList = response.data;
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			};
			
			$scope.refreshLoginConfigurationList();
			
			$scope.loadMore = function() {
				$scope.loginConfigurationPageNumber++;
				loggerService.getLogger().info("Page number " + $scope.loginConfigurationPageNumber);
				
				loginConfigurationService.getLoginConfigurationList($scope.loginConfigurationPageNumber, function(response) {
					$scope.loginConfigurationList = $scope.loginConfigurationList.concat(response.data);
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			};
			
			$scope.createNewLoginConfiguration = function() {
				loggerService.getLogger().info("Go to creating new Login Configuration");
				$state.go('loginConfigurationDetails', {
					id : 'create'
				});
			};
			
			$scope.openLoginConfiguration = function(loginConfiguration) {
				loggerService.getLogger().info("Openning login configuration " + loginConfiguration.id);
				$state.go('loginConfigurationDetails', {
					id : loginConfiguration.id
				});
			};
			
			$scope.duplicateLoginConfiguration = function(loginConfiguration) {
				loginConfiguration.name = loginConfiguration.name + " - Duplicate";
				loginConfigurationService.createLoginConfiguration(loginConfiguration, function(response) {
					$scope.refreshLoginConfigurationList();
				});
			};
			
			$scope.deleteLoginConfiguration = function(loginConfiguration, $event) {
				loggerService.getLogger().info("Deleting login configuration " + loginConfiguration.id);
				dialogService.confirm({
					msg : $filter('translate')('loginConfiguration.del.confirm') + ": " + loginConfiguration.name + "?",
					ok : function() {
						loginConfigurationService.deleteLoginConfiguration(loginConfiguration.id, loginConfiguration.modifiedOn, function(response) {
							$scope.refreshLoginConfigurationList();
							loggerService.getLogger().debug(response);
						});
					}
				});
				$event.stopPropagation();
			};
			
			var loginConfigurationWithOpenOption = [];
			$scope.closeAllOpenOption = function() {
				if (loginConfigurationWithOpenOption.length > 0) {
					angular.forEach(loginConfigurationWithOpenOption, function(loginConfiguration) {
						loggerService.getLogger().debug("The login configuration is = " + JSON.stringify(loginConfiguration));
						if (loginConfiguration.optionOpen) {
							loginConfiguration.optionOpen = false;
						}
					});
					loginConfigurationWithOpenOption = [];
				}
			};
			
			$scope.openOption = function(loginConfiguration, open, $event) {
				if (angular.isDefined(open)) {
					loginConfiguration.optionOpen = open;
					if (open) {
						$scope.closeAllOpenOption();
						autoCloseOptionService.close();
						autoCloseOptionService.registerOpen($scope, $scope.closeAllOpenOption);
						loginConfigurationWithOpenOption.push(loginConfiguration);
					} else {
						loginConfigurationWithOpenOption = [];
					}
				} else {
					return angular.isDefined(loginConfiguration.optionOpen) ? loginConfiguration.optionOpen : false;
				}
			};
			
			$scope.isOptionOpen = function(loginConfiguration) {
				if (loginConfiguration.optionOpen) {
					return loginConfiguration.optionOpen;
				} else {
					return false;
				}
			};
		}
	]
)
