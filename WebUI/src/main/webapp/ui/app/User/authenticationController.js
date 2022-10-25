mainApp.controller('authenticationController', ['$window', '$scope', '$state', '$timeout', 'loggerService', '$stateParams', '$filter', 'sharedService', 'passwordService', 'authenticationService', 'dialogService',
		function($window, $scope, $state, $timeout, loggerService, $stateParams, $filter, sharedService, passwordService, authenticationService, dialogService) {
			$scope.$parent.isDetailsPage = false;
			$scope.isDirty = {};
			$scope.passwordRegex = "((?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%!]).{7,12})";
			$scope.passwordMatching = true;
			$scope.passwordChanged = true;
			$scope.showProfile = false;
			$scope.userInfo = {};
			var timer;
			
			$scope.showUserProfile = function() {
				timer = $timeout(function() {
					$scope.showProfile = true;
				}, 500);
			};
			
			$scope.hideUserProfile = function() {
				$timeout.cancel(timer);
				$scope.showProfile = false;
			};
			
			$scope.getUserInfo = function() {
				loggerService.getLogger().debug("Getting user information.");
				authenticationService.getUserInfo(function(response){
					$scope.userInfo = response.data;
				});
			};
			
			$scope.getUserInfo();
			
			$scope.changePassword = function() {
				loggerService.getLogger().debug("Go to change password page");
				$scope.$parent.currentTab = undefined;
				$state.go('changePassword');
			};
			
			$scope.savePasswordChanges = function(form) {
				if (form.$invalid || !$scope.passwordMatching || !$scope.passwordChanged) {
					form.$setDirty();
					for ( var field in form) {
						if (field[0] == '$')
							continue;
						// console.log(field);
						form[field].$touched = true;
					}
					return;
				}
				
				passwordService.changePassword($scope.userInfo, function(response){
					loggerService.getLogger().debug(response.message);
					
					if(response.statusCode == '1001') {
						// Remove information from object
						$scope.userInfo.password = null;
						$scope.userInfo.newPassword = null;
						$scope.userInfo.confirmNewPassword = null;
						$state.go('overview');
					}
				})
				
				form.$setPristine();
			};
			
			$scope.checkPasswordChanged = function() {
				if($scope.userInfo.newPassword) {
					$scope.passwordChanged = $scope.userInfo.password != $scope.userInfo.newPassword;
				}
			};
			
			$scope.checkPasswordMatching = function() {
				$scope.passwordMatching = $scope.userInfo.newPassword == $scope.userInfo.confirmNewPassword;
			};
			
			$scope.logout = function() {
				loggerService.getLogger().info("Logging out");
				authenticationService.logout(function(data) {
					// ignore
				});
				
				$window.location.href = "logout";
			};
		}
	]
)
