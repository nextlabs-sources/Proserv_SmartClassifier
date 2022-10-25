mainApp.factory('scServicesService', [
    'networkService',
    'loggerService',
    'configService',
    '$filter',
    'statusCodeService',
    function(networkService, loggerService, configService, $filter, statusCodeService) {

      var PAGE_SIZE = configService.configObject['defaultServicePageSize'];

      var getSortFields = function() {
        return [{
          label: $filter('translate')('services.sort.by.name'),
          field: "name"
        }];
      }

      var getServiceList = function(serviceType, pageNo, sortFields, callback) {
        loggerService.getLogger().info(
                "Get service list of type " + serviceType + " with page number " + pageNo + " and page size "
                        + PAGE_SIZE);
        var requestData = {
          'sortFields': sortFields,
          'pageNo': pageNo,
          'pageSize': PAGE_SIZE
        };

        loggerService.getLogger().info("Request data " + JSON.stringify(requestData));

        loggerService.getLogger().debug(configService.getUrl(serviceType + ".list"));

        networkService.post(configService.getUrl(serviceType + ".list"), requestData, function(data) {
          statusCodeService.list(serviceType, callback, data);
        });
      }

      var getService = function(serviceType, serviceID, callback) {
        loggerService.getLogger().info("Get service of type " + serviceType + " with ID " + serviceID);

        loggerService.getLogger().debug(configService.getUrl(serviceType + ".get") + serviceID);

        networkService.get(configService.getUrl(serviceType + ".get") + serviceID, function(data) {
          statusCodeService.get(serviceType, callback, data);
        });
      }

      var modifyService = function(service, serviceType, callback) {
        loggerService.getLogger().info("Modifying " + serviceType + "  " + service.id);

        loggerService.getLogger().debug(service.name);

        var requestData = service;

        networkService.put(configService.getUrl(serviceType + ".modify"), requestData, function(data) {
          statusCodeService.modify(serviceType, callback, data);
        });
      }

      return {
        getServiceList: getServiceList,
        getService: getService,
        modifyService: modifyService,
        getSortFields: getSortFields
      }

    }]);