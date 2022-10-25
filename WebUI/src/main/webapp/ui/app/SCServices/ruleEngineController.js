angular.module('mainApp').controller(
		'ruleEngineController',
		[ '$scope', 'loggerService', 'collectionService', 'scServicesService', '$location', "$anchorScroll", '$filter',
				function($scope, loggerService, collectionService, scServicesService, $location, $anchorScroll, $filter) {

					loggerService.getLogger().info("Rule Engine id is " + $scope.serviceID);

					$scope.serviceTempObject.associatedExecutionWindowSetsMap = {};
					angular.forEach($scope.service.executionWindowSets, function(window, index) {
						$scope.serviceTempObject.associatedExecutionWindowSetsMap[window.id] = true;
					})

					// initialize config reload interval .. convert to
					// hours or mins based on the value
					if ($scope.service.configReloadInterval >= 3600 && $scope.service.configReloadInterval % 3600 == 0) {
						$scope.serviceTempObject.configReloadIntervalUnit = "hour";
						$scope.serviceTempObject.configReloadIntervalTemp = $scope.service.configReloadInterval / 3600;
					} else {
						$scope.serviceTempObject.configReloadIntervalUnit = "min";
						$scope.serviceTempObject.configReloadIntervalTemp = $scope.service.configReloadInterval / 60;
						if ($scope.serviceTempObject.configReloadIntervalTemp < 5) {
							$scope.serviceTempObject.configReloadIntervalTemp = 5;
							$scope.service.configReloadInterval = 300;
						}
					}
					// ON DEMAND RULE FREQUENCY
					if ($scope.service.onDemandInterval >= 60 && $scope.service.onDemandInterval % 60 == 0) {
						$scope.serviceTempObject.onDemandIntervalUnit = "min";
						$scope.serviceTempObject.onDemandIntervalTemp = $scope.service.onDemandInterval / 60;
					} else {
						$scope.serviceTempObject.onDemandIntervalUnit = "second";
						$scope.serviceTempObject.onDemandIntervalTemp = $scope.service.onDemandInterval;
					}

					// SCHEDULED RULE FREQUENCY
					if ($scope.service.scheduledInterval >= 60 && $scope.service.scheduledInterval % 60 == 0) {
						$scope.serviceTempObject.scheduledIntervalUnit = "min";
						$scope.serviceTempObject.scheduledIntervalTemp = $scope.service.scheduledInterval / 60;
					} else {
						$scope.serviceTempObject.scheduledIntervalUnit = "second";
						$scope.serviceTempObject.scheduledIntervalTemp = $scope.service.scheduledInterval;
					}

					var currentTarget = "ruleEngineInfo";
					$scope.scrollTo = function(target) {
						loggerService.getLogger().info("Scrolling to " + target);
						$location.hash(target);
						$anchorScroll();
						currentTarget = target;
					}

					$scope.highlightGrammar = function(target) {
						currentTarget = target;
					}

					$scope.selectedExecutionWindowName = $filter('translate')("component.editor.dropdown.heading");

					$scope.addToExecutionWindowList = function(selectedExecutionWindow) {

						if (!$scope.service.executionWindowSets) {
							$scope.service.executionWindowSets = [];
						}
						$scope.service.executionWindowSets.push(selectedExecutionWindow);
						$scope.serviceTempObject.associatedExecutionWindowSetsMap[selectedExecutionWindow.id] = true;
						$scope.selectedExecutionWindowName = $filter('translate')("component.editor.dropdown.heading");
					}

					$scope.getExecutionWindowRowClass = function(index) {
						if (index % 2 == 0) {
							return "sc-data-list-row-even";
						} else {
							return "sc-data-list-row-odd";
						}
					}

					$scope.removeExecutionWindow = function(index) {
						$scope.serviceTempObject.associatedExecutionWindowSetsMap[$scope.service.executionWindowSets[index].id] = false;
						$scope.service.executionWindowSets.splice(index, 1);
					}

					$scope.changeConfigReloadIntervalUnit = function(unit, interval) {
						$scope.serviceTempObject.configReloadIntervalUnit = unit;
						$scope.onConfigReloadIntervalEdit(interval);
					}

					$scope.onConfigReloadIntervalEdit = function(interval) {
						if($scope.serviceTempObject.configReloadIntervalUnit == "min" && interval < 5) {
							$scope.serviceTempObject.configReloadIntervalTemp = 5;
						}

						if ($scope.serviceTempObject.configReloadIntervalUnit == "hour") {
							$scope.service.configReloadInterval = $scope.serviceTempObject.configReloadIntervalTemp * 3600;
						} else {
							$scope.service.configReloadInterval = $scope.serviceTempObject.configReloadIntervalTemp * 60;
						}
					}

					$scope.onDemandRuleIntervalEdit = function(interval) {
						if ($scope.serviceTempObject.onDemandIntervalUnit == "min") {
							$scope.service.onDemandInterval = interval * 60;
						} else {
							$scope.service.onDemandInterval = interval;
						}
					}

					$scope.onScheduledRuleIntervalEdit = function(interval) {
						if ($scope.serviceTempObject.scheduledIntervalUnit == "min") {
							$scope.service.scheduledInterval = interval * 60;
						} else {
							$scope.service.scheduledInterval = interval;
						}
					}

					$scope.changeOnDemandRuleIntervalUnit = function(unit, interval) {
						$scope.serviceTempObject.onDemandIntervalUnit = unit;
						$scope.onDemandRuleIntervalEdit(interval);
					}

					$scope.changeScheduledRuleIntervalUnit = function(unit, interval) {
						$scope.serviceTempObject.scheduledIntervalUnit = unit;
						$scope.onScheduledRuleIntervalEdit(interval);
					}

					$scope.updateResetObject = function() {
						$scope.$parent.setResetServiceTempObject(angular.copy($scope.serviceTempObject));
						loggerService.getLogger().debug("Updating reset object " + JSON.stringify($scope.$parent.resetServiceTempObject));
					}

					$scope.updateResetObject();

				} ])