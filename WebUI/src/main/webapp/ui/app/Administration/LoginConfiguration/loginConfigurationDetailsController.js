mainApp.controller('loginConfigurationDetailsController', 
		['$scope', 
		 '$state', 
		 '$location', 
		 '$window', 
		 '$stateParams', 
		 '$filter', 
		 '$anchorScroll',
		 'loggerService', 
		 'dialogService', 
		 'loginConfigurationService', 
		 function ($scope,
				   $state,
				   $location,
				   $window,
				   $stateParams,
				   $filter,
				   $anchorScroll,
				   loggerService,
				   dialogService,
				   loginConfigurationService) {
			$scope.$parent.isDetailsPage = true;
			$scope.isConnected = false;
			$scope.loginConfigurationID = $stateParams.id;
			$scope.loginConfigurationTypes = ["LDAP"];
			$scope.ldapAttributeList = [];
			
			// reload login configuration
			$scope.reloadLoginConfiguration = function() {
				loginConfigurationService.getLoginConfiguration($scope.loginConfigurationID, function(response) {
					$scope.loginConfiguration = response.data;
					$scope.resetLoginConfiguration = angular.copy($scope.loginConfiguration);
					loggerService.getLogger().debug($scope.loginConfiguration);
					if (!$scope.loginConfiguration) {
						$scope.detailsFound = false;
					} else {
						$scope.detailsFound = true;
					}
				})
			};
			
			if ($scope.loginConfigurationID == "create") {
				// if creating login configuration, initialize default values
				$scope.loginConfiguration = {
					"name" : "",
					"type" : "LDAP",
					"configurationData" : {"secureChannel" : false,  
										   "url" : "",
										   "domain" : "",
										   "rootDN" : "",
										   "username" : "",
										   "password" : "",
										   "userFilter" : "",
										   "userSearchBase" : ""},
					"userAttributeMapping" : []
					}
				$scope.detailsFound = true;
				$scope.resetLoginConfiguration = angular.copy($scope.loginConfiguration);
				$scope.isCreated = true;
			} else {
				// get from web service
				$scope.reloadLoginConfiguration();
				$scope.isCreated = false;
			}
			
			$scope.formInvalid = function() {
				var hasError = false;
				angular.forEach($scope.loginConfigurationDetailsForm.val.$error, function(f) {
					hasError = true;
				});
				
				return hasError;
			};
			
			$scope.backToLoginConfigurationList = function(form) {
				if (!angular.equals($scope.loginConfiguration, $scope.resetLoginConfiguration)) {
					dialogService.confirm({
						msg : $filter('translate')('details.back.confirm'),
						ok : function() {
							$state.go("loginConfigurations");
						}
					});
				} else {
					$state.go("loginConfigurations");
				}
			};
			
			$scope.connectLDAP = function(loginConfiguration) {
				loggerService.getLogger().debug("LoginConfiguration:" + JSON.stringify(loginConfiguration));
				
				$scope.isConnected = false;
				$scope.loginConfiguration.userAttributeMapping = [];
				loginConfigurationService.connectLDAP($scope.loginConfiguration, function(response) {
					loggerService.getLogger().debug(JSON.stringify(response));
					$scope.ldapAttributeList = response.data;
					if(response.statusCode == '1006') {
						$scope.isConnected = true;
						
						if($scope.loginConfiguration.userAttributeMapping.length == 0) {
							$scope.loginConfiguration.userAttributeMapping = [{"from" : "sAMAccountName", "to" : "username"}, {"from" : "displayName", "to" : "firstName"}, {"from" : "", "to" : "lastName"}];
						}
					}
				});
			};
			
			$scope.addUserAttribute = function() {
				if(!$scope.loginConfiguration.userAttributeMapping) {
					$scope.loginConfiguration.userAttributeMapping = [];
				}
				
				$scope.loginConfiguration.userAttributeMapping.push({
					from : "key",
					to : "value"
				});
			};
			
			$scope.mapUserAttribute = function(index, ldapAttribute) {
				$scope.loginConfiguration.userAttributeMapping[index].from = ldapAttribute;
			};
			
			$scope.removeMapping = function(index) {
				$scope.loginConfiguration.userAttributeMapping.splice(index, 1);
			};
			
			$scope.discardLoginConfigurationChanges = function(form) {
				loggerService.getLogger().info("Discarding changes");
				$scope.loginConfiguration = angular.copy($scope.resetLoginConfiguration);
				$scope.isConnected = false;
				$scope.ldapAttributeList = [];
				form.$setPristine();
			};
			
			$scope.saveLoginConfigurationChanges = function(form) {
				if (form.$invalid) {
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
					loginConfigurationService.createLoginConfiguration($scope.loginConfiguration, function(response) {
						$state.go("loginConfigurations");
						loggerService.getLogger().debug(response.message);
					})
				} else {
					// call modify web service
					loginConfigurationService.modifyLoginConfiguration($scope.loginConfiguration, function(response) {
						$scope.resetLoginConfiguration = angular.copy($scope.loginConfiguration);
						loggerService.getLogger().debug(response.message);
					})
				}
				form.$setPristine();
			};
			
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