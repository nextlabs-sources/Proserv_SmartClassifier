mainApp.controller('internalUserDetailsController',
		['$scope', 
		 '$state', 
		 '$location', 
		 '$window', 
		 '$stateParams', 
		 '$filter', 
		 '$anchorScroll',
		 'loggerService', 
		 'dialogService', 
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
				  userService) {
			$scope.$parent.isDetailsPage = true;
			$scope.userID = $stateParams.id;
			$scope.passwordRegex = "((?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%!]).{7,12})";
			$scope.passwordMatching = true;
			
			// reload user
			$scope.reloadUser = function() {
				userService.getUser($scope.userID, function(response) {
					$scope.user = response.data;
					$scope.resetUser = angular.copy($scope.user);
					loggerService.getLogger().debug($scope.user);
					if (!$scope.user) {
						$scope.detailsFound = false;
					} else {
						$scope.detailsFound = true;
					}
				})
			};
			
			if ($scope.userID == "create") {
				// if creating user, initialize default values
				$scope.user = {
					"type" : "I",
					"admin" : false,
					"visible" : true,
					"enabled" : true,
					"firstName" : null,
					"lastName" : null,
					"username" : null,
					"password" : null,
					"confirmPassword" : null
					}
				$scope.detailsFound = true;
				$scope.resetUser = angular.copy($scope.user);
				$scope.isCreated = true;
			} else {
				// get from web service
				$scope.reloadUser();
				$scope.isCreated = false;
			}
			
			$scope.formInvalid = function() {
				var hasError = false;
				angular.forEach($scope.userDetailsForm.val.$error, function(f) {
					hasError = true;
				});
				
				return hasError;
			};
			
			$scope.backToUserList = function(form) {
				if (!angular.equals($scope.user, $scope.resetUser)) {
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
			
			$scope.discardUserChanges = function(form) {
				loggerService.getLogger().info("Discarding changes");
				$scope.user = angular.copy($scope.resetUser);
				form.$setPristine();
			};
			
			$scope.saveUserChanges = function(form) {
				if (form.$invalid || !$scope.passwordMatching) {
					form.$setDirty();
					for ( var field in form) {
						if (field[0] == '$')
							continue;
						// console.log(field);
						form[field].$touched = true;
					}
					return;
				}
				
				if ($scope.isCreated) {
					// if created, call add web service
					userService.createUser($scope.user, function(response) {
						$state.go("users");
						loggerService.getLogger().debug(response.message);
					})
				} else {
					// call modify web service
					userService.modifyUser($scope.user, function(response) {
						$scope.resetUser = angular.copy($scope.user);
						loggerService.getLogger().debug(response.message);
					})
				}
				form.$setPristine();
			};
			
			$scope.checkPasswordMatching = function() {
				$scope.passwordMatching = $scope.user.password == $scope.user.confirmPassword;
				loggerService.getLogger().info("Match: " + $scope.passwordMatching);
			}
			
			// scrollTo functionalities
			$scope.scrollTo = function(target) {
				loggerService.getLogger().info("Scrolling to " + target);
				$location.hash(target);
				$anchorScroll();
				// $location.hash(target, '');
				currentTarget = target;
			};
		}
	]
)