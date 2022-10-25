mainApp.factory('passwordService', ['$filter', 'networkService', 'loggerService', 'configService', 'statusCodeService', 
		function($filter, networkService, loggerService, configService, statusCodeService) {
			
			var changePassword = function(userInfo, callback) {
				loggerService.getLogger().debug("Changing password for " + userInfo.username);
				var requestData = userInfo;
				
				networkService.put(configService.getUrl("password.modify"), requestData, function(data) {
					statusCodeService.modify("ChangePassword", callback, data);
				});
			}
			
			return {
				changePassword : changePassword
			}
		}
	]
)