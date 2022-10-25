mainApp.factory('jmsService', ['networkService', 'loggerService', 'configService', '$filter', 'statusCodeService', function(networkService, loggerService, configService, $filter, statusCodeService) {

	var PAGE_SIZE = configService.configObject['defaultJMSPageSize'];

	var getSortFields = function() {
		return [{
			label : $filter('translate')('jms.sort.by.name'),
			field : "displayName"
		}];
	}

	var getJMSList = function(pageNo, sortFields, callback) {
		loggerService.getLogger().info("Get jms list of type " + " with page number " + pageNo + " and page size " + PAGE_SIZE);
		var requestData = {
			'sortFields' : sortFields,
			'pageNo' : pageNo,
			'pageSize' : PAGE_SIZE
		};

		networkService.post(configService.getUrl("jms.list"), requestData, function(data) {
			statusCodeService.list("JMSProfile", callback, data);
		});
	}

	var createJMS = function(jms, callback) {
		loggerService.getLogger().info("Creating jms with details " + jms);
		var requestData = jms;
		networkService.post(configService.getUrl("jms.add"), requestData, function(data) {
			statusCodeService.create("JMSProfile", callback, data);
		})
	}

	var getJMS = function(jmsID, callback) {
		loggerService.getLogger().info("Get jms" + " with ID " + jmsID);

		loggerService.getLogger().debug(configService.getUrl("jms.get") + jmsID);

		networkService.get(configService.getUrl("jms.get") + jmsID, function(data) {
			statusCodeService.get("JMSProfile", callback, data);
		});

	}

	var modifyJMS = function(jms, callback) {
		loggerService.getLogger().info("Modifying jms " + jms.id);

		var requestData = jms;

		networkService.put(configService.getUrl("jms.modify"), requestData, function(data) {
			statusCodeService.modify("JMSProfile", callback, data)
		});
	}

	var deleteJMS = function(id, lastModifiedDate, callback) {
		loggerService.getLogger().info("Deleting JMS profile " + id);

		networkService.del(configService.getUrl("jms.delete") + id + "/" + lastModifiedDate, function(data) {
			loggerService.getLogger().debug("jms.delete call back");
			statusCodeService.del("JMSProfile", callback, data);
		})
	}

	return {
		getJMSList : getJMSList,
		getJMS : getJMS,
		modifyJMS : modifyJMS,
		deleteJMS : deleteJMS,
		getSortFields : getSortFields,
		createJMS : createJMS
	}

}]);