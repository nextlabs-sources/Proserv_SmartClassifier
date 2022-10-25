mainApp.controller('userListController', 
	  ['$scope',
	   '$state', 
	   '$window', 
	   '$filter', 
	   'loggerService', 
	   'sharedService', 
	   'autoCloseOptionService', 
	   'userService', 
	   'configService', 
	   'dialogService',
	   function($scope,
			   $state, 
			   $window, 
			   $filter, 
			   loggerService, 
			   sharedService, 
			   autoCloseOptionService, 
			   userService, 
			   configService, 
			   dialogService) {
			var PAGE_SIZE = configService.configObject['defaultUserPageSize'];
			$scope.$parent.isDetailsPage = false;
			
			$scope.refreshUserList = function() {
				$scope.userList = [];
				$scope.total = 0;
				$scope.userPageNumber = 1;
				
				userService.getUserList($scope.userPageNumber, function(response) {
					$scope.userList = response.data;
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			};
		  
			$scope.refreshUserList();
			
			$scope.loadMore = function() {
				$scope.userPageNumber++;
				loggerService.getLogger().info("Page number " + $scope.userPageNumber);
				
				userService.getUserList($scope.userPageNumber, function(response) {
					$scope.userList = $scope.userList.concat(response.data);
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			};
			
			$scope.createNewExternalUser = function() {
				loggerService.getLogger().info("Go to creating new External User");
				$state.go("importExternalUser");
			};
			
			$scope.createNewInternalUser = function() {
				loggerService.getLogger().info("Go to creating new Internal User");
				$state.go("internalUserDetails", {
					id : 'create'
				});
			};
			
			$scope.openUser = function(user) {
				loggerService.getLogger().info("Openning user " + user.id);
				if(user.type == "I") {
					$state.go("internalUserDetails", {
						id : user.id
					});
				}
			};
			
			$scope.deleteUser = function(user, $event) {
				loggerService.getLogger().info("Deleting user " + user.id);
				dialogService.confirm({
					msg : $filter('translate')('user.del.confirm') + ": " + user.displayName + "?",
					ok : function() {
						userService.deleteUser(user.id, user.modifiedOn, function(response) {
							$scope.refreshUserList();
							loggerService.getLogger().debug(response);
						});
					}
				});
				$event.stopPropagation();
			};
			
			var userWithOpenOption = [];
			$scope.closeAllOpenOption = function() {
				if (userWithOpenOption.length > 0) {
					angular.forEach(userWithOpenOption, function(user) {
						loggerService.getLogger().debug("The user is = " + JSON.stringify(user));
						if (user.optionOpen) {
							user.optionOpen = false;
						}
					});
					userWithOpenOption = [];
				}
			};
			
			$scope.openOption = function(user, open, $event) {
				if (angular.isDefined(open)) {
					user.optionOpen = open;
					if (open) {
						$scope.closeAllOpenOption();
						autoCloseOptionService.close();
						autoCloseOptionService.registerOpen($scope, $scope.closeAllOpenOption);
						userWithOpenOption.push(user);
					} else {
						userWithOpenOption = [];
					}
				} else {
					return angular.isDefined(user.optionOpen) ? user.optionOpen : false;
				}
			};
			
			$scope.isOptionOpen = function(user) {
				if (user.optionOpen) {
					return user.optionOpen;
				} else {
					return false;
				}
			};
		}
	]
)
