mainApp.factory('pluginService', ['networkService', 'loggerService',

'configService', '$filter', 'statusCodeService', 'collectionService', function(networkService, loggerService, configService, $filter, statusCodeService, collectionService) {

	var PAGE_SIZE = configService.configObject['defaultPluginsPageSize'];

	var getSortFields = function() {
		return [{
			label : $filter('translate')('plugins.sort.by.name'),
			field : "name",
			order : "asc"
		}];
	}

	var getPluginList = function(pageNo, sortFields, callback) {
		loggerService.getLogger().info("Get plugin list of type " + " with page number " + pageNo + " and page size " + PAGE_SIZE);
		var requestData = {
			'sortFields' : sortFields,
			'pageNo' : pageNo,
			'pageSize' : PAGE_SIZE
		};

		networkService.post(configService.getUrl("plugins.list"), requestData, function(data) {
			statusCodeService.list("Plugins", callback, data);
		});
	}

	var getPlugin = function(pluginID, callback) {
		loggerService.getLogger().info("Get plugin" + " with ID " + pluginID);

		loggerService.getLogger().debug(configService.getUrl("plugins.get") + pluginID);

		networkService.get(configService.getUrl("plugins.get") + pluginID, function(data) {
			statusCodeService.get("Plugins", callback, data);
		});

	}

	var modifyPlugin = function(plugins, callback) {
		loggerService.getLogger().info("Modifying plugin " + plugins.id);

		var requestData = plugins;

		networkService.put(configService.getUrl("plugins.modify"), requestData, function(data) {
			statusCodeService.modify("Plugins", callback, data)
		});
	}

	return {
		getPluginList : getPluginList,
		getPlugin : getPlugin,
		modifyPlugin : modifyPlugin,
		getSortFields : getSortFields
	}

}]);