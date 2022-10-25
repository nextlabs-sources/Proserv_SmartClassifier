mainApp.controller('pluginDetailsController', ['$scope', '$state', '$location', 'loggerService', '$stateParams', '$filter', 'dialogService', 'pluginService', 'generalService', '$anchorScroll',
		function($scope, $state, $location, loggerService, $stateParams, $filter, dialogService, pluginService, generalService, $anchorScroll) {
			$scope.$parent.isDetailsPage = true;
			$scope.pluginsID = $stateParams.id;

			$scope.changeBooleanValue = function(bool, paramIndex) {
				loggerService.getLogger().info("Boolean value is now = " + bool);
				loggerService.getLogger().info("Parameter is " + $scope.plugins.params[paramIndex].label);
				$scope.plugins.params[paramIndex].value = bool;
				loggerService.getLogger().debug(JSON.stringify($scope.plugins.params));
			}

			$scope.dateValuePopup = {
				opened : false
			}

			$scope.dateValueOpened = function() {
				$scope.dateValuePopup.opened = true;
			}

			$scope.reloadPlugin = function() {

				pluginService.getPlugin($scope.pluginsID, function(response) {
					$scope.plugins = response.data;
					$scope.resetPlugin = angular.copy($scope.plugins);
					loggerService.getLogger().debug(JSON.stringify($scope.plugins));
					if (!$scope.plugins) {
						$scope.detailsFound = false;
					} else {
						$scope.detailsFound = true;
					}
				})

			}

			$scope.reloadPlugin();

			$scope.backToPluginsList = function(form) {
				if (!angular.equals($scope.plugins, $scope.resetPlugin)) {
					dialogService.confirm({
						msg : $filter('translate')('details.back.confirm'),
						ok : function() {
							$state.go("Plugins");
						}
					})
				} else {
					$state.go("Plugins");
				}
			}

			// scrollTo functionalities
			$scope.scrollTo = function(target) {
				$location.hash(target);
				$anchorScroll();
				// $location.hash(target, '');
				currentTarget = target;
			}

			$scope.discardPluginChanges = function(form) {
				loggerService.getLogger().log("Discarding changes");
				$scope.plugins = angular.copy($scope.resetPlugin);
				form.$setPristine();
			}

			$scope.savePluginChanges = function(form) {
				// to be implemented

				if (form.$invalid) {
					form.$setDirty();
					for ( var field in form) {
						if (field[0] == '$')
							continue;
						// console.log(field);
						form[field].$touched = true;
						form[field].$dirty = true;
					}
					return;
				}

				// call modify web service
				pluginService.modifyPlugin($scope.plugins, function(response) {
					$scope.resetPlugin = angular.copy($scope.plugins);
					loggerService.getLogger().log(response.message);
				})
				form.$setPristine();
			}

			$scope.booleanValues = generalService.getBooleanValuesArray();
			loggerService.getLogger().log("scope.booleanvalues = " + JSON.stringify($scope.booleanValues));
		}])