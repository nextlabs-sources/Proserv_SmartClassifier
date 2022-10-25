mainApp.controller('generalSettingsController', ['$scope', '$state', 'loggerService', '$filter', 'generalSettingsService', 'configService',
		function($scope, $state, loggerService, $filter, generalSettingsService, configService) {
			$scope.$parent.isDetailsPage = false;
			$scope.isDirty = {};

			$scope.refreshGeneralSettings = function() {
				generalSettingsService.getSettingList(function(response) {
					$scope.settingList = response.data;
					$scope.resetSettingList = angular.copy($scope.settingList);
					loggerService.getLogger().debug(response);
				});
			}

			$scope.refreshGeneralSettings();

			$scope.modifyGeneralSetting = function(setting, field) {

				if (field.$invalid) {
					field.$dirty = true;
					field.$touched = true;
					return;
				}

				generalSettingsService.modifySetting(setting, function(response) {
					$scope.isDirty[setting.id] = false;
					$scope.resetSettingList = angular.copy($scope.settingList);
				})
			}

			$scope.onSettingChange = function(id) {
				$scope.isDirty[id] = true;
			}

			$scope.resetSettings = function() {
				$scope.settingList = angular.copy($scope.resetSettingList);
				$scope.isDirty = {};
			}

			$scope.saveAll = function(form) {
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

				var settings = [];
				if ($scope.settingList) {
					angular.forEach($scope.settingList, function(settingGroup, index) {
						if (settingGroup.systemConfigs) {
							angular.forEach(settingGroup.systemConfigs, function(setting, sindex) {
								settings.push(setting)
							});
						}
					})
				}
				generalSettingsService.modifyAllSettings(settings, function(response) {
					$scope.resetSettingList = angular.copy($scope.settingList);
					$scope.isDirty = {};
				})

			}

		}])