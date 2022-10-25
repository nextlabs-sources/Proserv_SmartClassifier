mainApp.factory('userService', 
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
			var PAGE_SIZE = configService.configObject['defaultUserPageSize'];
			
			var getUserList = function(pageNo, callback) {
				loggerService.getLogger().info("Get user list of type " + " with page number " + pageNo + " and page size " + PAGE_SIZE);
				var requestData = {
					'pageNo' : pageNo,
					'pageSize' : PAGE_SIZE
				};
				
				networkService.post(configService.getUrl("user.list"), requestData, function(data) {
					statusCodeService.list("Users", callback, data);
				});
			};
			
			var createUser = function(user, callback) {
				loggerService.getLogger().info("Creating user with details " + JSON.stringify(user));
				var requestData = user;
				
				networkService.post(configService.getUrl("user.add"), requestData, function(data) {
					statusCodeService.create("User", callback, data);
				});
			};
			
			var getUser = function(userID, callback) {
				loggerService.getLogger().info("Get user with ID " + userID);
				loggerService.getLogger().debug(configService.getUrl("user.get") + userID);
				
				networkService.get(configService.getUrl("user.get") + userID, function(data) {
					statusCodeService.get("User", callback, data);
				});
			};
			
			var modifyUser = function(user, callback) {
				loggerService.getLogger().info("Modifying user " + user.id);
				var requestData = user;
				
				networkService.put(configService.getUrl("user.modify"), requestData, function(data) {
					statusCodeService.modify("User", callback, data);
				});
			};
			
			var deleteUser = function(id, lastModifiedDate, callback) {
				loggerService.getLogger().info("Deleting user " + id);
				
				networkService.del(configService.getUrl("user.delete") + id + "/" + lastModifiedDate, function(data) {
					statusCodeService.del("User", callback, data);
				});
			};
			
			var getExternalUsers = function(authenticationHandler, callback) {
				loggerService.getLogger().info("Retrieve external user for " + authenticationHandler.name);
				var requestData = authenticationHandler;
				
				networkService.post(configService.getUrl("loginConfiguration.listUsers"), requestData, function(data) {
					statusCodeService.get("LDAP User", callback, data);
				});
			};
			
			var importExternalUser = function(userList, callback) {
				loggerService.getLogger().info("Importing users from LDAP.");
				var requestData = userList;
				
				networkService.post(configService.getUrl("user.import"), requestData, function(data) {
					statusCodeService.importData("LDAP User", callback, data);
				});
			}
			
			return {
				getUserList : getUserList,
				getExternalUsers : getExternalUsers,
				createUser : createUser,
				getUser : getUser,
				modifyUser : modifyUser,
				deleteUser : deleteUser,
				importExternalUser : importExternalUser
			};
		}
	]
)