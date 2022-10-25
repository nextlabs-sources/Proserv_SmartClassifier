mainApp.factory('generalSettingsService', ['networkService', 'loggerService', 'configService', '$filter', 'statusCodeService',
		function(networkService, loggerService, configService, $filter, statusCodeService) {

			var getSettingList = function(callback) {
				loggerService.getLogger().info("Get general settings list");

				networkService.post(configService.getUrl("generalSettings.list"), null, function(data) {
					statusCodeService.list("General Settings", callback, data);
				});
			}

			var getSetting = function(settingID, callback) {
				loggerService.getLogger().info("Get setting" + " with ID " + settingID);

				networkService.get(configService.getUrl("generalSettings.get") + settingID, function(data) {
					statusCodeService.get("General Settings", callback, data);
				});

			}

			var modifySetting = function(setting, callback) {
				loggerService.getLogger().info("Modifying setting " + setting.id);

				var requestData = setting;

				networkService.put(configService.getUrl("generalSettings.modify"), requestData, function(data) {
					statusCodeService.modify("General Settings", callback, data)
				});
			}

			var modifyAllSettings = function(data, callback) {
				loggerService.getLogger().info("Modifying all settings");

				var requestData = data;
				networkService.put(configService.getUrl("generalSettings.modifyAll"), requestData, function(data) {
					statusCodeService.modify("General Settings", callback, data)
				})
			}

			return {
				getSettingList : getSettingList,
				getSetting : getSetting,
				modifySetting : modifySetting,
				modifyAllSettings : modifyAllSettings
			}

		}]);