mainApp.factory('summaryService', ['networkService', 'loggerService', 'configService', 'statusCodeService', function(networkService, loggerService, configService, statusCodeService) {

	var getSummary = function(componentName, callback) {
		loggerService.getLogger().debug("Get summary of " + componentName);
		var requestData = {};
		networkService.post(configService.getUrl("summary.get") + componentName + "Summary", requestData, function(data) {
			statusCodeService.getCollection(componentName, callback, data);
		});
	}

	return {
		getSummary : getSummary,
	}

}]);