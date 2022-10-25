mainApp.controller('executionWindowDetailsController', [
    '$scope',
    '$state',
    '$location',
    'loggerService',
    '$stateParams',
    '$filter',
    'dialogService',
    'executionWindowService',
    '$anchorScroll',
    'uibDateParser',
    'generalService',
    function($scope, $state, $location, loggerService, $stateParams, $filter, dialogService, executionWindowService,
            $anchorScroll, uibDateParser, generalService) {
      $scope.$parent.isDetailsPage = true;
      $scope.validity = {};

      // validity variables
      $scope.executionWindowID = $stateParams.id;
      $scope.validFromPopUp = {
        opened: false
      };
      $scope.expiringOnPopUp = {
        opened: false
      }

      $scope.validFromOpen = function() {
        $scope.validFromPopUp.opened = true;
      }

      $scope.expiringOnOpen = function() {
        $scope.expiringOnPopUp.opened = true;
      }

      // schedule variables
      $scope.dayOfTheWeek = generalService.getDayOfTheWeek();

      // reload executionWindow
      $scope.reloadExecutionWindow = function() {
        executionWindowService.getExecutionWindow($scope.executionWindowID, function(response) {
          $scope.executionWindow = response.data;

          // process validity
          $scope.validity.validFrom = new Date($scope.executionWindow.effectiveFrom);
          if ($scope.executionWindow.effectiveUntil == null || $scope.executionWindow.effectiveUntil == 0) {
            $scope.validity.expiringOnOption = "never";
            $scope.validity.expiringOn = new Date();
          } else {
            $scope.validity.expiringOnOption = "specific";
            $scope.validity.expiringOn = new Date($scope.executionWindow.effectiveUntil);
          }

          $scope.loadExecutionWindowSlots();

          // copy data to reset
          // variables to be used in
          // discard function
          $scope.copyResetObjects();

          loggerService.getLogger().debug($scope.executionWindow);
          if (!$scope.executionWindow) {
            $scope.detailsFound = false;
          } else {
            $scope.detailsFound = true;
          }
        })
      };

      $scope.copyResetObjects = function() {
        $scope.resetExecutionWindow = angular.copy($scope.executionWindow);
        $scope.resetWeeklySchedules = angular.copy($scope.weeklySchedules);
        $scope.resetDailySchedules = angular.copy($scope.dailySchedules);
        $scope.resetValidity = angular.copy($scope.validity);
      }

      $scope.loadExecutionWindowSlots = function() {
        // loop through all execution windows and in
        // each execution window, loop through all
        // timeslots, for each timeslot add an element
        // to the schedule array
        if (!$scope.executionWindow.executionWindows) {
          $scope.executionWindow.executionWindows = [];
          $scope.executionWindow.scheduleType = 'D';
          $scope.initializeDailyTimeSlot();
          $scope.initializeWeeklyTimeSlot();
        }

        if ($scope.executionWindow.scheduleType === 'W') {
          $scope.weeklySchedules = [];
          $scope.initializeDailyTimeSlot();

          angular.forEach($scope.executionWindow.executionWindows, function(value, index) {

            angular.forEach(value.executionTimeSlots, function(timeSlot, index) {
              var days = {
                "sun": false,
                "mon": false,
                "tue": false,
                "wed": false,
                "thu": false,
                "fri": false,
                "sat": false
              };
              angular.forEach(value.day, function(dayOfWeek, index) {
                days[generalService.getDayOfTheWeekMap()[dayOfWeek]] = true;
              });
              var start = $scope.parseTimeFromString(timeSlot.startTime);
              var end = $scope.parseTimeFromString(timeSlot.endTime);

              $scope.weeklySchedules.push({
                days: days,
                start: start,
                end: end,
                validEndTime: true
              })
            });

          });
        } else {
          $scope.dailySchedules = [];
          $scope.initializeWeeklyTimeSlot()
          angular.forEach($scope.executionWindow.executionWindows, function(value, index) {
            angular.forEach(value.executionTimeSlots, function(timeSlot, index) {
              var start = $scope.parseTimeFromString(timeSlot.startTime);
              var end = $scope.parseTimeFromString(timeSlot.endTime);

              $scope.dailySchedules.push({
                start: start,
                end: end,
                validEndTime: true
              })
            });
          });

        }
      }

      $scope.checkEndTime = function(schedule) {
        if (schedule.start && schedule.end) {
          if (schedule.start.getTime() < schedule.end.getTime()) {
            schedule.validEndTime = true;
          }
        }
      }

      // get daily or weekly schedule as text
      $scope.getScheduleAsText = function() {
        var returnedString = ""
        if ($scope.executionWindow.scheduleType === 'D') {
          returnedString += $filter('translate')('daily.label') + " "
          angular.forEach($scope.dailySchedules, function(slot, index) {
            if (index != 0) {
              returnedString += " " + $filter('translate')('and.label') + " ";
            }

            returnedString += $scope.parseTimeToString(slot.start, true);
            returnedString += "-";
            returnedString += $scope.parseTimeToString(slot.end, true);
          });
        } else {
          angular.forEach($scope.weeklySchedules, function(slot, index) {
            if (index != 0) {
              returnedString += " " + $filter('translate')('and.label') + " ";
            }

            var hasDay = false;

            angular.forEach(slot.days, function(value, key) {
              if (value) {
                hasDay = true;
                returnedString += $filter('translate')(key + ".label") + " ";
              }
            });
            if (!hasDay) {
              returnedString += "<" + $filter('translate')('week.days.alt') + ">";
            }
            returnedString += " ";
            returnedString += $scope.parseTimeToString(slot.start, true);
            returnedString += "-";
            returnedString += $scope.parseTimeToString(slot.end, true);
          })
        }
        return returnedString;
      }

      // initialize an array with one default element for
      // weekly timeslot
      $scope.initializeWeeklyTimeSlot = function() {
        var start = new Date();
        start.setHours(0);
        start.setMinutes(0);
        var end = new Date();
        end.setHours(8);
        end.setMinutes(0);
        $scope.weeklySchedules = [{
          days: {
            "sun": false,
            "mon": false,
            "tue": false,
            "wed": false,
            "thu": false,
            "fri": false,
            "sat": false,
          },
          start: start,
          end: end,
          validEndTime: true
        }]
      }

      // initialize an array with one default element for
      // daily timeslot
      $scope.initializeDailyTimeSlot = function() {
        var start = new Date();
        start.setHours(0);
        start.setMinutes(0);
        var end = new Date();
        end.setHours(8);
        end.setMinutes(0);
        $scope.dailySchedules = [{
          start: start,
          end: end,
          validEndTime: true
        }]
      }

      if ($scope.executionWindowID == "create") {
        // if creating executionWindow, initialize
        // default values
        $scope.executionWindow = {
          "name": null,
          "scheduleType": "D",
        }
        $scope.validity.validFrom = new Date();
        $scope.validity.expiringOn = new Date();
        $scope.validity.expiringOnOption = "never";
        $scope.initializeWeeklyTimeSlot();
        $scope.initializeDailyTimeSlot();
        $scope.detailsFound = true;
        $scope.copyResetObjects();
        $scope.isCreated = true;
      } else {
        // get from web service
        $scope.reloadExecutionWindow();
        $scope.isCreated = false;
      }

      $scope.backToExecutionWindowList = function(form) {
        if (!$scope.dataPristine()) {
          dialogService.confirm({
            msg: $filter('translate')('details.back.confirm'),
            ok: function() {
              $state.go("ExecutionWindow");
            }
          })
        } else {
          $state.go("ExecutionWindow");
        }

      }

      $scope.dataPristine = function() {
        if (!angular.equals($scope.executionWindow, $scope.resetExecutionWindow)) { return false; }

        if (!angular.equals($scope.dailySchedules, $scope.resetDailySchedules)) { return false; }

        if (!angular.equals($scope.weeklySchedules, $scope.resetWeeklySchedules)) { return false; }

        if (!angular.equals($scope.validity, $scope.resetValidity)) { return false; }

        return true;
      };

      // scrollTo functionalities
      $scope.scrollTo = function(target) {
        loggerService.getLogger().info("Scrolling to " + target);
        $location.hash(target);
        $anchorScroll();
        // $location.hash(target, '');
        currentTarget = target;
      }

      $scope.discardExecutionWindowChanges = function(form) {

        loggerService.getLogger().info("Discarding changes");
        $scope.executionWindow = angular.copy($scope.resetExecutionWindow);
        $scope.weeklySchedules = angular.copy($scope.resetWeeklySchedules);
        $scope.dailySchedules = angular.copy($scope.resetDailySchedules);
        $scope.validity = angular.copy($scope.resetValidity);
        form.$setPristine();
      }

      $scope.saveExecutionWindowChanges = function(form) {
        if (form.$invalid) {
          form.$setDirty();
          for ( var field in form) {
            if (field[0] == '$') continue;
            // console.log(field);
            form[field].$touched = true;
          }
          return;
        }

        $scope.executionWindow.effectiveFrom = $scope.validity.validFrom.getTime();

        if ($scope.validity.expiringOnOption == 'specific') {
          $scope.executionWindow.effectiveUntil = $scope.validity.expiringOn.getTime();
        } else {
          $scope.executionWindow.effectiveUntil = 0;
          $scope.validity.expiringOn = new Date();
        }

        // process validity
        if ($scope.validity.expiringOn != 0
                && $scope.validity.validFrom.getTime() > $scope.validity.expiringOn.getTime()) {
          dialogService.notify({
            type: "sc-dialog-error",
            msg: $filter('translate')('executionWindow.editor.invalid.expiration.validation'),
            ok: function() {
              return;
            }
          });
          return;
        }
        
        // clear existing timeslots
        $scope.executionWindow.executionWindows = [];

        // process timeslots
        loggerService.getLogger().debug($scope.executionWindow.scheduleType);
        if ($scope.executionWindow.scheduleType == 'D') {
          for (var index = 0; index < $scope.dailySchedules.length; index++) {
            var dailyTimeSlot = $scope.dailySchedules[index];

            if (dailyTimeSlot.start.getTime() > dailyTimeSlot.end.getTime()) {
              dailyTimeSlot.validEndTime = false;
              return;
            }

            var element = {
              "displayOrder": index + 1,
              "day": ["0", "1", "2", "3", "4", "5", "6"],
              "executionTimeSlots": [{
                "displayOrder": 1,
                "startTime": $scope.parseTimeToString(dailyTimeSlot.start, false),
                "endTime": $scope.parseTimeToString(dailyTimeSlot.end, false)
              }]
            }
            $scope.executionWindow.executionWindows.push(element);
          }
        } else {
          for (var index = 0; index < $scope.weeklySchedules.length; index++) {
            var weeklyTimeSlot = $scope.weeklySchedules[index];

            if (weeklyTimeSlot.start.getTime() > weeklyTimeSlot.end.getTime()) {
              weeklyTimeSlot.validEndTime = false;
              return;
            }

            var dayArray = [];
            angular.forEach(weeklyTimeSlot.days, function(value, key) {
              if (value == true) {
                dayArray.push(generalService.getDayOfTheWeekMap()[key]);
              }
            });
            var element = {
              "displayOrder": index + 1,
              "day": dayArray,
              "executionTimeSlots": [{
                "displayOrder": 1,
                "startTime": $scope.parseTimeToString(weeklyTimeSlot.start, false),
                "endTime": $scope.parseTimeToString(weeklyTimeSlot.end, false)
              }]
            }
            $scope.executionWindow.executionWindows.push(element);

          }
        }

        if ($scope.isCreated) {
          // if created, call add web service
          executionWindowService.createExecutionWindow($scope.executionWindow, function(response) {
            $state.go("ExecutionWindow");
            loggerService.getLogger().debug(response.message);
          })
        } else {
          // call modify web service
          executionWindowService.modifyExecutionWindow($scope.executionWindow, function(response) {
            $scope.copyResetObjects();
            loggerService.getLogger().debug(response.message);
          })
        }
        form.$setPristine();
      }
    }])