mainApp.controller('externalUserDetailsController',
		['$scope', 
		 '$state', 
		 '$location', 
		 '$window', 
		 '$stateParams', 
		 '$filter', 
		 '$anchorScroll',
		 'loggerService', 
		 'dialogService', 
		 'collectionService', 
		 'userService', 
		 function($scope, 
				  $state, 
				  $location, 
				  $window, 
				  $stateParams, 
				  $filter, 
				  $anchorScroll,
				  loggerService, 
				  dialogService, 
				  collectionService,
				  userService) {
			$scope.$parent.isDetailsPage = true;
			$scope.checkStatus = {
				allUserChecked: false
			};
			$scope.authenticationHandler = {"name" : null, "filter" : null};
			$scope.userList = [];
			$scope.loadedUser = false;
			
			// reload Authentication Handler
			$scope.reloadAuthenticationHandlers = function() {
				$scope.authenticationHandlerFound = false;
				$scope.authenticationHandlers = [];
				$scope.totalAuthenticationHandlers = 0;
				collectionService.getCollection("AuthenticationHandler", function(response) {
					$scope.authenticationHandlers = response.data;
					$scope.totalAuthenticationHandlers = response.totalNoOfRecords;
					
					if($scope.authenticationHandlers && $scope.authenticationHandlers.length > 0) {
						$scope.authenticationHandlerFound = true;
					}
				});
			};
			
			$scope.reloadAuthenticationHandlers();
			
			$scope.loadUsers = function(authHandler) {
				$scope.userList = [];
				authHandler.filter = $scope.authenticationHandler.filter;
				$scope.authenticationHandler = authHandler;
				$scope.checkStatus.allUserChecked = false;
				
				loggerService.getLogger().debug("Loading LDAP users.");
				
				userService.getExternalUsers($scope.authenticationHandler, function(response) {
					$scope.userList = response.data;
					$scope.loadedUser = true;
				});
			};
			
			$scope.backToUserList = function(form) {
				if ($scope.userList.length > 0) {
					dialogService.confirm({
						msg : $filter('translate')('details.back.confirm'),
						ok : function() {
							$state.go("users");
						}
					});
				} else {
					$state.go("users");
				}
			};
			
			$scope.discardChanges = function() {
				$scope.checkStatus.allUserChecked = false;
				$scope.filter = null;
				$scope.authenticationHandler = {"name" : null, "filter" : null};
				$scope.userList = [];
			}
			
			$scope.toggleCheckAll = function() {
				angular.forEach($scope.userList, function(user){
					user.$checked = $scope.checkStatus.allUserChecked;
				});
			};
			
			$scope.checkStatusChanged = function() {
				$scope.checkStatus.allUserChecked = $filter('filter')($scope.userList, {
					$checked: true
				}).length == $scope.userList.length;
			};
			
			$scope.refresh = function() {
				if($scope.authenticationHandler.name == undefined || $scope.authenticationHandler.name == null) {
					dialogService.notify({
						type : "sc-dialog-warning",
						msg : $filter('translate')('user.editor.invalid.handler.validation'),
						ok : function() {
							return;
						}
					});
					return;
				} else {
					$scope.loadUsers($scope.authenticationHandler);
				}
			};
			
			$scope.importUsers = function() {
				var users = $filter('filter')($scope.userList, {
					$checked: true
				}).map(function(user) {
					return {
						type: "L",
						username: user.username,
						firstName: user.firstName,
						lastName: user.lastName,
						authenticationHandlerId: $scope.authenticationHandler.id
					}
				});
				
				userService.importExternalUser(users, function(response) {
					if(response.statusCode == "1000" || response.statusCode == "4018") {
						$scope.loadUsers($scope.authenticationHandler);
					}
				});
			};
			
			$scope.createNewUserSource = function() {
				$state.go('loginConfigurationDetails', {
					id : 'create'
				});
			};
		}
	]
)