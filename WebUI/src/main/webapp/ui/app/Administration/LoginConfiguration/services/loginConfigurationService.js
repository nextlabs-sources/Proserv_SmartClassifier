mainApp.factory('loginConfigurationService', 
		['$filter',
		'networkService',
		'loggerService',
		'configService',
		'statusCodeService',
		function($filter, 
				networkService, 
				loggerService, 
				configService,
				statusCodeService) {
			var PAGE_SIZE = configService.configObject['defaultIDBResultPageSize'];
			
			var getLoginConfigurationList = function(pageNo, callback) {
				loggerService.getLogger().info("Get login configuration list of type " + " with page number " + pageNo + " and page size " + PAGE_SIZE);
				var requestData = {
					'pageNo' : pageNo,
					'pageSize' : PAGE_SIZE
				};
				
				networkService.post(configService.getUrl("loginConfiguration.list"), requestData, function(data) {
					statusCodeService.list("LoginConfigurations", callback, data);
				});
			};
			
			var createLoginConfiguration = function(loginConfiguration, callback) {
				loggerService.getLogger().info("Creating login configuration with details " + JSON.stringify(loginConfiguration));
				var requestData = loginConfiguration;
				
				networkService.post(configService.getUrl("loginConfiguration.add"), requestData, function(data) {
					statusCodeService.create("LoginConfigurations", callback, data);
				});
			};
			
			var getLoginConfiguration = function(loginConfigurationID, callback) {
				loggerService.getLogger().info("Get login configuration with ID " + loginConfigurationID);
				loggerService.getLogger().debug(configService.getUrl("loginConfiguration.get") + loginConfigurationID);
				
				networkService.get(configService.getUrl("loginConfiguration.get") + loginConfigurationID, function(data) {
					statusCodeService.get("LoginConfiguration", callback, data);
				});
			};
			
			var modifyLoginConfiguration = function(loginConfiguration, callback) {
				loggerService.getLogger().info("Modifying login configuration " + loginConfiguration.id);
				var requestData = loginConfiguration;
				
				networkService.put(configService.getUrl("loginConfiguration.modify"), requestData, function(data) {
					statusCodeService.modify("LoginConfiguration", callback, data);
				});
			};
			
			var deleteLoginConfiguration = function(id, lastModifiedDate, callback) {
				loggerService.getLogger().info("Deleting login configuration " + id);
				
				networkService.del(configService.getUrl("loginConfiguration.delete") + id + "/" + lastModifiedDate, function(data) {
					statusCodeService.del("LoginConfiguration", callback, data);
				});
			};
			
			var connectLDAP = function(loginConfiguration, callback) {
				loggerService.getLogger().info("LDAP connection test");
				var requestData = loginConfiguration;
				
				networkService.post(configService.getUrl("loginConfiguration.connect"), requestData, function(data){
					statusCodeService.execute("LDAP connection", callback, data);
				});
			}
			
			return {
				getLoginConfigurationList : getLoginConfigurationList,
				createLoginConfiguration : createLoginConfiguration,
				getLoginConfiguration : getLoginConfiguration,
				modifyLoginConfiguration : modifyLoginConfiguration,
				deleteLoginConfiguration : deleteLoginConfiguration,
				connectLDAP : connectLDAP
			};
		}
	]
)