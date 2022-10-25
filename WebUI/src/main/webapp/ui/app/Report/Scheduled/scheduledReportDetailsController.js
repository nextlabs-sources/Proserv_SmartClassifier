mainApp.controller('scheduledReportDetailsController', 
		['$scope', 
		 '$state', 
		 '$location', 
		 '$window', 
		 '$stateParams', 
		 '$filter', 
		 '$anchorScroll',
		 'loggerService', 
		 'dialogService', 
		 'generalService',
		 'scheduledReportService', 
		 function ($scope,
				   $state,
				   $location,
				   $window,
				   $stateParams,
				   $filter,
				   $anchorScroll,
				   loggerService,
				   dialogService,
				   generalService,
				   scheduledReportService) {
			$scope.$parent.isDetailsPage = true;
			$scope.isCustomRange = false;
			$scope.scheduledReportID = $stateParams.id;
			$scope.rangeList = [
				{"option" : "Predefined", "lastXDays" : 7, "from" : 0, "to" : 0, "display" : "scheduledReport.editor.filter.range.past.7.days.label"}, 
				{"option" : "Predefined", "lastXDays" : 30, "from" : 0, "to" : 0, "display" : "scheduledReport.editor.filter.range.past.30.days.label"}, 
				{"option" : "Predefined", "lastXDays" : 90, "from" : 0, "to" : 0, "display" : "scheduledReport.editor.filter.range.past.3.months.label"}, 
				{"option" : "Predefined", "lastXDays" : 180, "from" : 0, "to" : 0, "display" : "scheduledReport.editor.filter.range.past.6.months.label"},
				{"option" : "Predefined", "lastXDays" : 365, "from" : 0, "to" : 0, "display" : "scheduledReport.editor.filter.range.past.1.year.label"},
				{"option" : "Custom", "lastXDays" : -1, "from" : 0, "to" : 0, "display" : "scheduledReport.editor.filter.range.date.range.label"}
			];
			$scope.fieldNameList = ["Directory", "File Name", "Rule Name", "Action"];
			$scope.operatorList = ["=", "!="];
			$scope.validity = {};
			
			// validity variables
			$scope.validFromPopUp = {
				opened : false
			};
			
			$scope.expiringOnPopUp = {
				opened : false
			};

			$scope.validFromOpen = function() {
				$scope.validFromPopUp.opened = true;
			};

			$scope.expiringOnOpen = function() {
				$scope.expiringOnPopUp.opened = true;
			};

			// schedule variables
			$scope.dayOfTheWeek = generalService.getDayOfTheWeek();
			$scope.dayOfTheMonth = generalService.getDayOfTheMonth();
			
			// reload scheduled report
			$scope.reloadScheduledReport = function() {
				scheduledReportService.getScheduledReport($scope.scheduledReportID, function(response) {
					$scope.scheduledReport = response.data;
					$scope.resetScheduledReport = angular.copy($scope.scheduledReport);
					loggerService.getLogger().debug($scope.scheduledReport);
					if (!$scope.scheduledReport) {
						$scope.detailsFound = false;
						
						// process validity
						$scope.validity.validFrom = new Date($scope.scheduledReport.effectiveFrom);
						if ($scope.scheduledReport.effectiveUntil === 0) {
							$scope.validity.expiringOnOption = "never";
							$scope.validity.expiringOn = new Date();
						} else {
							$scope.validity.expiringOnOption = "specific";
							$scope.validity.expiringOn = new Date($scope.scheduledReport.effectiveUntil);
						}
						
						// process schedule
						$scope.loadSchedule();
						
						// copy initial data to
						// reset object
						$scope.copyResetObjects();
					} else {
						$scope.detailsFound = true;
					}
				})
			};
			
			$scope.copyResetObjects = function() {
				$scope.resetScheduledReport = angular.copy($scope.scheduledReport);
				$scope.resetWeeklySchedules = angular.copy($scope.weeklySchedules);
				$scope.resetDailySchedules = angular.copy($scope.dailySchedules);
				$scope.resetMonthlySchedules = angular.copy($scope.monthlySchedules);
				$scope.resetValidity = angular.copy($scope.validity);
			};
			
			// load schedule variables from object loaded from
			// web service
			$scope.loadSchedule = function() {
				if ($scope.scheduledReport.executionFrequency) {
					var date = new Date();
					date.setHours($scope.scheduledReport.executionFrequency.time.slice(0, 2));
					date.setMinutes($scope.scheduledReport.executionFrequency.time.slice(2, 4));
					if ($scope.scheduledReport.scheduleType === "D") {
						$scope.dailySchedules = [{
							time : date
						}];
						
						$scope.initializeWeeklyTimeSlot();
						$scope.initializeMonthlyTimeSlot();
					} else if ($scope.scheduledReport.scheduleType === "W") {
						var days = {
							"sun" : false,
							"mon" : false,
							"tue" : false,
							"wed" : false,
							"thu" : false,
							"fri" : false,
							"sat" : false
						};
						
						angular.forEach($scope.scheduledReport.executionFrequency.dayOfWeek, function(day, index) {
							loggerService.getLogger().debug(generalService.getDayOfTheWeekMap()[day]);
							days[generalService.getDayOfTheWeekMap()[day]] = true;
						});
						
						$scope.weeklySchedules = [{
							time : date,
							days : days
						}];
						
						$scope.initializeDailyTimeSlot();
						$scope.initializeMonthlyTimeSlot();
					} else {
						var days = {};
						for (var i = 1; i < 31; i++) {
							days[i] = false;
						}
						days['L'] = false;
						angular.forEach($scope.scheduledReport.executionFrequency.dayOfMonth, function(day, index) {
							days[day] = true;
						});
						$scope.monthlySchedules = [{
							time : date,
							days : days
						}]
						
						$scope.initializeDailyTimeSlot();
						$scope.initializeWeeklyTimeSlot();
					}
				} else {
					$scope.scheduledReport.executionFrequency = {};
					$scope.initializeDailyTimeSlot();
					$scope.initializeWeeklyTimeSlot();
					$scope.initializeMonthlyTimeSlot();
				}
			};
			
			// initialize an array with one default element for
			// weekly timeslot
			$scope.initializeWeeklyTimeSlot = function() {
				var time = new Date();
				time.setHours(0);
				time.setMinutes(0);
				$scope.weeklySchedules = [{
					days : {
						"sun" : false,
						"mon" : false,
						"tue" : false,
						"wed" : false,
						"thu" : false,
						"fri" : false,
						"sat" : false,
					},
					time : time
				}]
			};
			
			// initialize an array with one default element for
			// daily timeslot
			$scope.initializeDailyTimeSlot = function() {
				var time = new Date();
				time.setHours(0);
				time.setMinutes(0);
				$scope.dailySchedules = [{
					time : time,
				}]
			};
			
			// initialize an array with one default element for
			// monthly timeslot
			$scope.initializeMonthlyTimeSlot = function() {
				var time = new Date();
				time.setHours(0);
				time.setMinutes(0);
				var days = {};
				for (var i = 1; i < 31; i++) {
					days[i + ""] = false;
				}
				days['L'] = false;
				$scope.monthlySchedules = [{
					days : days,
					time : time
				}]
			};
			
			$scope.selectAll = function(monthlySchedule) {
				angular.forEach(monthlySchedule.days, function(value, key) {
					if (key != 'L') {
						monthlySchedule.days[key] = true;
					}
				});
			};
			
			$scope.clearAll = function(monthlySchedule) {
				angular.forEach(monthlySchedule.days, function(value, key) {
					monthlySchedule.days[key] = false;
				});
			};
			
			$scope.getDayOfMonthString = function(days) {
				var returnedString = "";
				var object = [];
				angular.forEach(days, function(value, day) {
					if (value) {
						if (day === 'L') {
							object.push($filter('translate')('L'));
						} else {
							object.push(day);
						}
					} else {
						if (object.length > 2) {
							returnedString += object[0] + "-" + object[object.length - 1] + ", ";
						} else {
							for (var i = 0; i < object.length; i++) {
								returnedString += object[i] + ", ";
							}
						}
						object.length = 0;
					}
				});
				
				if (object.length > 0) {
					if (object.length > 2) {
						returnedString += object[0] + "-" + object[object.length - 1] + ", ";
					} else {
						for (var i = 0; i < object.length; i++) {
							returnedString += object[i] + ", ";
						}
					}
				}
				
				if (returnedString.length === 0) {
					returnedString = $filter('translate')('month.days.alt');
				} else {
					returnedString = returnedString.slice(0, returnedString.length - 2);
				}
				
				return returnedString;
			};

			// get daily or weekly or monthly schedule as text
			$scope.getScheduleAsText = function() {
				var returnedString = ""
				if ($scope.scheduledReport.scheduleType === 'D') {
					returnedString += $filter('translate')('daily.label') + " " + $filter('translate')('at.label') + " ";
					angular.forEach($scope.dailySchedules, function(slot, index) {
						if (index != 0) {
							returnedString += " " + $filter('translate')('and.label') + " ";
						}
						
						returnedString += $scope.parseTimeToString(slot.time, true);
					});
				} else if ($scope.scheduledReport.scheduleType === 'W') {
					returnedString += $filter('translate')('weekly.label') + " " + $filter('translate')('on.label') + " ";
					angular.forEach($scope.weeklySchedules, function(slot, index) {
						if (index != 0) {
							returnedString += " " + $filter('translate')('and.label') + " ";
						}
						
						var hasDay = false;
						var dayString = "";
						angular.forEach(slot.days, function(value, key) {
							if (value) {
								hasDay = true;
								dayString += $filter('translate')(key + ".label") + " ";
							}
						});
						
						if (!hasDay) {
							returnedString += "<" + $filter('translate')('week.days.alt') + ">";
						} else {
							returnedString += "(" + dayString.slice(0, dayString.length - 1) + ")";
						}
						returnedString += " " + $filter('translate')('at.label') + " ";
						returnedString += $scope.parseTimeToString(slot.time, true);
					})
				} else {
					returnedString += $filter('translate')('monthly.label') + " " + $filter('translate')('on.label') + " ";
					angular.forEach($scope.monthlySchedules, function(slot, index) {
						if (index != 0) {
							returnedString += " " + $filter('translate')('and.label') + " ";
						}
						
						var dayString = $scope.getDayOfMonthString(slot.days);
						if (dayString.length === 0) {
							returnedString += "<" + $filter('translate')('month.days.alt') + ">";
						} else {
							returnedString += "(" + dayString + ")";
						}
						returnedString += " " + $filter('translate')('at.label') + " ";
						returnedString += $scope.parseTimeToString(slot.time, true);
					})
				}
				return returnedString;
			};
			
			$scope.monthlyScheduleChange = function(monthlySchedule, day, value) {
				loggerService.getLogger().debug(day + " " + value);
				if (day == 'L' && value) {
					for (var i = 1; i < 31; i++) {
						monthlySchedule.days[i] = false;
					}
				}
				
				if (day != 'L' && value) {
					monthlySchedule.days['L'] = false;
				}
			};
			
			if ($scope.scheduledReportID == "create") {
				// if creating scheduled report, initialize default values
				$scope.scheduledReport = {
					"type" : "S",
					"name" : null,
					"description" : null,
					"range" : {"option" : "Predefined", "lastXDays" : 0, "from" : 0, "to" : 0, "display" : "scheduledReport.editor.filter.range.any.date.label"},
					"filters" : [],
					"schedule" : {"scheduleType" : "D", "enabled" : true, "recipients" : []},
				}
				$scope.validity.validFrom = new Date();
				$scope.validity.expiringOn = new Date();
				$scope.validity.expiringOnOption = "never";
				$scope.initializeWeeklyTimeSlot();
				$scope.initializeDailyTimeSlot();
				$scope.initializeMonthlyTimeSlot();
				$scope.copyResetObjects();
				$scope.detailsFound = true;
				$scope.isCreated = true;
			} else {
				// get from web service
				$scope.reloadScheduledReport();
				$scope.isCreated = false;
			}
			
			$scope.formInvalid = function() {
				var hasError = false;
				angular.forEach($scope.scheduledReportDetailsForm.val.$error, function(f) {
					hasError = true;
				});
				
				return hasError;
			};
			
			$scope.backToScheduledReportList = function(form) {
				if (!angular.equals($scope.scheduledReport, $scope.resetScheduledReport)) {
					dialogService.confirm({
						msg : $filter('translate')('details.back.confirm'),
						ok : function() {
							$state.go("scheduledReports");
						}
					});
				} else {
					$state.go("scheduledReports");
				}
			};
			
			$scope.discardScheduledReportChanges = function(form) {
				loggerService.getLogger().info("Discarding changes");
				$scope.scheduledReport = angular.copy($scope.resetScheduledReport);
				$scope.weeklySchedules = angular.copy($scope.resetWeeklySchedules);
				$scope.dailySchedules = angular.copy($scope.resetDailySchedules);
				$scope.monthlySchedules = angular.copy($scope.resetMonthlySchedules);
				$scope.validity = angular.copy($scope.resetValidity);
				
				form.$setPristine();
			};
			
			$scope.saveScheduledReportChanges = function(form) {
				if (form.$invalid) {
					form.$setDirty();
					for ( var field in form) {
						if (field[0] == '$')
							continue;
						// console.log(field);
						form[field].$touched = true;
					}
					return;
				}
				
				if ($scope.isCreated) {
					// if created, call add web service
					scheduledReportService.createScheduledReport($scope.scheduledReport, function(response) {
						$state.go("scheduledReports");
						loggerService.getLogger().debug(response.message);
					})
				} else {
					// call modify web service
					scheduledReportService.modifyScheduledReport($scope.scheduledReport, function(response) {
						$scope.resetScheduledReport = angular.copy($scope.scheduledReport);
						loggerService.getLogger().debug(response.message);
					})
				}
				form.$setPristine();
			};
			
			$scope.dataPristine = function() {
				if (!angular.equals($scope.scheduledReport, $scope.resetScheduledReport)) {
					return false;
				}
				
				if (!angular.equals($scope.weeklySchedules, $scope.resetWeeklySchedules)) {
					return false;
				}
				
				if (!angular.equals($scope.dailySchedules, $scope.resetDailySchedules)) {
					return false;
				}
				
				if (!angular.equals($scope.monthlySchedules, $scope.resetMonthlySchedules)) {
					return false;
				}
				
				if (!angular.equals($scope.validity, $scope.resetValidity)) {
					return false;
				}
				
				return true;
			};
			
			// Add filter
			$scope.addFilter = function() {
				if(!$scope.scheduledReport.filters) {
					$scope.scheduledReport.filters = [];
				}
				
				$scope.scheduledReport.filters.push({
					"displayOrder" : $scope.scheduledReport.filters.length + 1,
					"fieldName" : null,
					"operator" : null,
					"value" : null
				});
			};
			
			// Remove filter
			$scope.removeFilter = function(index) {
				$scope.scheduledReport.filters.splice(index, 1);
			}
			
			$scope.setFilterFieldName = function(index, fieldName) {
				$scope.scheduledReport.filters[index].fieldName = fieldName;
			};
			
			$scope.setFilterOperator = function(index, operator) {
				$scope.scheduledReport.filters[index].operator = operator;
			};
			
			// scrollTo functionalities
			$scope.scrollTo = function(target) {
				loggerService.getLogger().info("Scrolling to " + target);
				$location.hash(target);
				$anchorScroll();
				// $location.hash(target, '');
				currentTarget = target;
			};
		}
	]
)