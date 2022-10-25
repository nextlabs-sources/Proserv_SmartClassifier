mainApp.factory('collectionService', ['$http', 'networkService', 'loggerService', 'configService', '$filter', 'statusCodeService',
		function($http, networkService, loggerService, configService, $filter, statusCodeService) {

			var getCollection = function(collectionName, callback) {
				loggerService.getLogger().debug("Get collection " + collectionName);

				networkService.get(configService.getUrl("collection.get") + collectionName, function(data) {
					statusCodeService.getCollection(collectionName, callback, data);
				});
			}

			return {
				getCollection : getCollection
			}

		}]);