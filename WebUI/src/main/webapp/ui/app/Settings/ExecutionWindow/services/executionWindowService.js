mainApp.factory('executionWindowService', [
    'networkService',
    'loggerService',
    'configService',
    '$filter',
    'statusCodeService',
    'generalService',
    function(networkService, loggerService, configService, $filter, statusCodeService, generalService) {

      var PAGE_SIZE = configService.configObject['defaultExecutionWindowPageSize'];
      var mapDayOftheWeek = generalService.getDayOfTheWeekMap();

      var getSortFields = function() {
        return [{
          label: $filter('translate')('executionWindow.sortby.name'),
          field: "name"
        }];
      };

      var createScheduleString = function(executionWindowSet) {
        var returnString = "";
        var executionWindows = executionWindowSet.executionWindows;

        for (var i = 0; i < executionWindows.length; i++) {
          if (i > 0) {
            returnString += ", "
          }
          var currentExecutionWindow = executionWindows[i];
          var currentExecutionWindowDay = currentExecutionWindow.day;
          if (executionWindowSet.scheduleType === "D") {
            returnString += $filter('translate')('daily.label');
          } else {
            for (var j = 0; j < currentExecutionWindowDay.length; j++) {
              returnString += $filter('translate')(mapDayOftheWeek[currentExecutionWindowDay[j]] + ".label") + " ";
            }
          }
          returnString += " " + currentExecutionWindow.executionTimeSlots[0].startTime.slice(0, 2) + ":"
                  + currentExecutionWindow.executionTimeSlots[0].startTime.slice(2, 4) + "-"
                  + currentExecutionWindow.executionTimeSlots[0].endTime.slice(0, 2) + ":"
                  + currentExecutionWindow.executionTimeSlots[0].endTime.slice(2, 4);

        }
        return returnString;
      };

      var getExecutionWindowList = function(pageNo, sortFields, callback) {
        loggerService.getLogger().info(
                "Get execution window list of type " + " with page number " + pageNo + " and page size " + PAGE_SIZE);
        var requestData = {
          'sortFields': sortFields,
          'pageNo': pageNo,
          'pageSize': PAGE_SIZE
        };

        networkService.post(configService.getUrl("executionWindow.list"), requestData, function(data) {
          statusCodeService.list("ExecutionWindow", callback, data);
        });
      };

      var getExecutionWindow = function(executionWindowID, callback) {
        loggerService.getLogger().info("Get execution window " + " with ID " + executionWindowID);

        loggerService.getLogger().debug(configService.getUrl("executionWindow.get") + executionWindowID);

        networkService.get(configService.getUrl("executionWindow.get") + executionWindowID, function(data) {
          statusCodeService.get("ExecutionWindow", callback, data);
        });

      };

      var modifyExecutionWindow = function(executionWindow, callback) {
        loggerService.getLogger().info("Modifying execution window " + executionWindow.id);

        var requestData = executionWindow;

        networkService.put(configService.getUrl("executionWindow.modify"), requestData, function(data) {
          statusCodeService.modify("ExecutionWindow", callback, data);
        });
      };

      var deleteExecutionWindow = function(id, lastModifiedDate, callback) {
        loggerService.getLogger().info("Deleting execution window " + id);

        networkService.del(configService.getUrl("executionWindow.delete") + id + "/" + lastModifiedDate,
                function(data) {
                  statusCodeService.del("ExecutionWindow", callback, data);
                });
      };

      var createExecutionWindow = function(executionWindow, callback) {
        loggerService.getLogger().info("Creating execution window with details " + executionWindow);
        var requestData = executionWindow;
        networkService.post(configService.getUrl("executionWindow.add"), requestData, function(data) {
          statusCodeService.create("ExecutionWindow", callback, data);
        });
      };

      return {
        getExecutionWindowList: getExecutionWindowList,
        getExecutionWindow: getExecutionWindow,
        modifyExecutionWindow: modifyExecutionWindow,
        deleteExecutionWindow: deleteExecutionWindow,
        getSortFields: getSortFields,
        createScheduleString: createScheduleString,
        createExecutionWindow: createExecutionWindow,
      }

    }]);