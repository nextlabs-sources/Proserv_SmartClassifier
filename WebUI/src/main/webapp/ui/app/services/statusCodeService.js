mainApp.factory('statusCodeService', ['$window', 'loggerService', 'configService', '$filter', 'dialogService', function($window, loggerService, configService, $filter, dialogService) {

	var undefinedStatusCode = function() {
		dialogService.notify({
			type : "sc-dialog-error",
			msg : $filter('translate')('status.code.missing'),
			ok : function() {

			}
		});
	};

	var create = function(type, callback, data) {
		loggerService.getLogger().debug("Process response for creating " + type);
		var statusCode = data.statusCode;
		loggerService.getLogger().debug("Status Code is " + statusCode);

		if (!statusCode) {
			undefinedStatusCode();
			return;
		}

		switch (statusCode) {
			case "1000" :
				if (callback) {
					callback(data);
				}
				dialogService.notifyWithoutBlocking({
					msg : $filter('translate')('create.saved.notify')
				});
				break;
			case "5004" :
				loggerService.getLogger().error("Server error " + data.message);
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.5004'),
					ok : function() {

					}
				});
				break;
			case "6000" :
				loggerService.getLogger().error("Server error " + data.message);
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {

					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {

					}
				});
		}
	};

	var execute = function(type, callback, data) {
		loggerService.getLogger().debug("Process response for executing " + type);
		var statusCode = data.statusCode;
		loggerService.getLogger().debug("Status Code is " + statusCode);
		if (!statusCode) {
			undefinedStatusCode();
			return;
		}
		switch (statusCode) {
			case "1000" :
				if (callback) {
					callback(data);
				}
				dialogService.notifyWithoutBlocking({
					msg : $filter('translate')('execute.issued.notify')
				});
				break;
			case "1005" :
				if (callback) {
					callback(data);
				}
				break;
			case "1006" :
				if (callback) {
					callback(data);
				}
				dialogService.notifyWithoutBlocking({
					msg : $filter('translate')('ldap.connected.notify')
				});
				break;
			case "4018" :
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {
						callback(data);

					}
				});
				break;
			case "5006" :
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.5006'),
					ok : function() {

					}
				});
				break;
			case "6000" :
				loggerService.getLogger().error("Server error " + data.message);
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {

					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {

					}
				});
		}
	};

	var modify = function(type, callback, data) {
		loggerService.getLogger().debug("Process response for modifying " + type);
		var statusCode = data.statusCode;
		loggerService.getLogger().debug("Status Code is " + statusCode);
		if (!statusCode) {
			undefinedStatusCode();
			return;
		}
		switch (statusCode) {
			case "1001" :
				dialogService.notifyWithoutBlocking({
					msg : $filter('translate')('modify.saved.notify')
				});
				if (callback) {
					callback(data);
				}
				break;
			case "4017" :
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.4017'),
					ok : function() {

					}
				});
				break;
			case "5002" :
				loggerService.getLogger().error("Server error " + data.message);
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.5002'),
					ok : function() {

					}
				});
				break;
			case "5004" :
				loggerService.getLogger().error("Server error " + data.message);
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.5004'),
					ok : function() {

					}
				});
				break;
			case "6000" :
				loggerService.getLogger().error("Server error " + data.message);
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {

					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {

					}
				});
		}
	};

	var get = function(type, callback, data) {
		loggerService.getLogger().debug("Process response for getting " + type);
		var statusCode = data.statusCode;
		loggerService.getLogger().debug("Status Code is " + statusCode);
		if (!statusCode) {
			undefinedStatusCode();
			return;
		}
		switch (statusCode) {
			case "1003" :
				if (callback) {
					callback(data);
				}
				break;
			case "5001" :
				if (callback) {
					callback(data);
				}
				break;
			case "5007" :
				dialogService.notify({
					type : "sc-dialog-success",
					msg : data.message,
					ok : function() {
						callback(data);

					}
				});
				break;
			case "5008" :
				if (callback) {
					callback(data);
				}
				break;
			case "6000" :
				loggerService.getLogger().error("Server error " + data.message);
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {

					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {

					}
				});
		}
	};

	var del = function(type, callback, data) {
		loggerService.getLogger().log("Process response for deleting " + type);
		var statusCode = data.statusCode;
		loggerService.getLogger().debug("Status Code is " + statusCode);
		if (!statusCode) {
			undefinedStatusCode();
			return;
		}
		switch (statusCode) {
			case "1002" :
				dialogService.notifyWithoutBlocking({
					msg : $filter('translate')('delete.deleted.notify')
				});
				if (callback) {
					callback(data);
				}
				break;
			case "5005" :
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.5005'),
					ok : function() {

					}
				});
				break;
			case "6000" :
				loggerService.getLogger().error("Server error " + data.message);
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {

					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {

					}
				});
		}
	};

	var list = function(type, callback, data) {
		loggerService.getLogger().debug("Process response for listing " + type);
		var statusCode = data.statusCode;
		loggerService.getLogger().debug("Status Code is " + statusCode);
		if (!statusCode) {
			undefinedStatusCode();
			return;
		}
		switch (statusCode) {
			case "1004" :
				if (callback) {
					callback(data);
				}
				break;
			case "5000" :
				if (callback) {
					callback(data);
				}
				break;
			case "6000" :
				loggerService.getLogger().error("Server error " + data.message);
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {

					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {

					}
				});
		}
	};

	var getCollection = function(type, callback, data) {
		loggerService.getLogger().debug("Process response for getting collection of " + type);
		var statusCode = data.statusCode;
		if (!statusCode) {
			undefinedStatusCode();
			return;
		}
		switch (statusCode) {
			case "1004" :
				if (callback) {
					callback(data)
				}
				break;
			case "4006" :
				loggerService.getLogger().error("Get collection: " + data.message);
				break;
			case "5000" :
				loggerService.getLogger().info("No data found");
				if (callback) {
					callback(data)
				}
				break;
			case "6000" :
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {

					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {

					}
				});
		}
	};

	var getSummary = function(type, callback, data) {
		loggerService.getLogger().debug("Process response for getting summary of " + type);
		var statusCode = data.statusCode;
		if (!statusCode) {
			undefinedStatusCode();
			return;
		}
		switch (statusCode) {
			case "1004" :
				if (callback) {
					callback(data)
				}
				break;
			case "5000" :
				loggerService.getLogger().info("No data found");
				if (callback) {
					callback(data)
				}
				break;
			case "6000" :
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {

					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {

					}
				});
		}
	};
	
	var importData = function(type, callback, data) {
		loggerService.getLogger().debug("Process response for importing data of " + type);
		var statusCode = data.statusCode;
		if (!statusCode) {
			undefinedStatusCode();
			return;
		}
		
		switch (statusCode) {
			case "1000" :
				if (callback) {
					callback(data);
				}
				dialogService.notifyWithoutBlocking({
					msg : $filter('translate')('user.imported.notify')
				});
				break;
			case "1007" :
				if (callback) {
					callback(data)
				}
				break;
			case "5000" :
				loggerService.getLogger().info("No data found");
				if (callback) {
					callback(data)
				}
				break;
			case "4005" :
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.4005'),
					ok : function() {
						
					}
				});
				break;
			case "6000" :
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {
	
					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {
	
					}
				});
		}
	};
	
	var exportData = function(type, callback, data) {
		loggerService.getLogger().debug("Process response for exporting data of " + type);
		var statusCode = data.statusCode;
		if (!statusCode) {
			undefinedStatusCode();
			return;
		}
		
		switch (statusCode) {
			case "1008" :
				if (callback) {
					callback(data)
				}
				break;
			case "5000" :
				loggerService.getLogger().info("No data found");
				if (callback) {
					callback(data)
				}
				break;
			case "6000" :
				dialogService.notify({
					type : "sc-dialog-error",
					msg : $filter('translate')('ERROR.6000'),
					ok : function() {
	
					}
				});
				break;
			case "6004" :
				loggerService.getLogger().info("Unauthenticated service access. Session may timed out.");
				$window.location.reload();
				break;
			default :
				loggerService.getLogger().info(data.message);
				dialogService.notify({
					type : "sc-dialog-warning",
					msg : data.message,
					ok : function() {
	
					}
				});
		}
	};
	
	return {
		create : create,
		get : get,
		modify : modify,
		del : del,
		list : list,
		getCollection : getCollection,
		execute : execute,
		getSummary : getSummary,
		importData : importData,
		exportData : exportData
	}
}]);