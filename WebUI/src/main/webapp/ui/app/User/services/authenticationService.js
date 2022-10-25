mainApp.factory('authenticationService', ['networkService', 'loggerService', 'configService', '$filter', 'statusCodeService',
		function(networkService, loggerService, configService, $filter, statusCodeService) {
			var getUserInfo = function(callback) {
				loggerService.getLogger().debug("Get logged in user information.");
				
				networkService.get(configService.getUrl("user.details"), function(data) {
						statusCodeService.get("UserDetails", callback, data);
					}
				);
			}
			
			var logout = function(callback) {
				loggerService.getLogger().debug("Logging out");
				
				networkService.post(configService.getUrl("user.logout"), null, function(data) {
						// Do nothing
					}
				);
			}
			
			return {
				getUserInfo : getUserInfo,
				logout : logout
			}
		}
	]
)