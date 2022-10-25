mainApp.controller('ruleListController', ['$scope', '$state', 'loggerService', '$window', '$filter', 'sharedService', 'autoCloseOptionService', 'ruleService', 'configService', 'dialogService',
		function($scope, $state, loggerService, $window, $filter, sharedService, autoCloseOptionService, ruleService, configService, dialogService) {
			var PAGE_SIZE = configService.configObject['defaultRulePageSize'];
			$scope.$parent.isDetailsPage = false;

			// sorting variables

			$scope.sortOrders = configService.getSortOrders();
			$scope.sortOrder = $scope.sortOrders[1];
			
			$scope.refreshRuleList = function(sortFields) {
				$scope.ruleList = [];
				$scope.total = 0;
				$scope.rulePageNumber = 1;
				$scope.totalSortFields = 0;

				if (!sortFields) {
					ruleService.getSortFields(function(response) {
						$scope.sortByFields = response.data;
						loggerService.getLogger().debug(response);

						if (!$scope.sortByFields) {
							$scope.sortByFields = [{
								"code" : "modifiedOn",
								"value" : "Last Updated"
							}];
						}
						$scope.sortBy = $scope.sortByFields[0];
						var sortFields = [{
							"field" : $scope.sortBy.code,
							"order" : $scope.sortOrder.value
						}];

						ruleService.getRuleList($scope.rulePageNumber, sortFields, function(response) {
							$scope.ruleList = response.data;
							$scope.total = response.totalNoOfRecords;
							$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
							loggerService.getLogger().debug(response);
						});
					})
				} else {
					ruleService.getRuleList($scope.rulePageNumber, sortFields, function(response) {
						$scope.ruleList = response.data;
						$scope.total = response.totalNoOfRecords;
						$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
						loggerService.getLogger().debug(response);
					});
				}
			};

			$scope.refreshRuleList();

			$scope.sortByField = function(sortBy) {
				var sortFields = [{
					"field" : sortBy.code,
					"order" : $scope.sortOrder.value
				}];
				$scope.sortBy = sortBy;

				$scope.refreshRuleList(sortFields)
			};

			$scope.sortByOrder = function(sortOrder) {
				var sortFields = [{
					"field" : $scope.sortBy.code,
					"order" : sortOrder.value
				}];
				$scope.sortOrder = sortOrder

				$scope.refreshRuleList(sortFields)
			};

			$scope.loadMore = function() {
				var sortFields = [{
					"field" : $scope.sortBy.code,
					"order" : $scope.sortOrder.value
				}];
				$scope.rulePageNumber++;
				loggerService.getLogger().info("Page number " + $scope.rulePageNumber);
				ruleService.getRuleList($scope.rulePageNumber, sortFields, function(response) {
					$scope.ruleList = $scope.ruleList.concat(response.data);
					$scope.total = response.totalNoOfRecords;
					$scope.numberOfPage = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
					loggerService.getLogger().debug(response);
				});
			};

			$scope.deleteRule = function(rule, $event) {
				dialogService.confirm({
					msg : $filter('translate')('rule.del.confirm') + ": " + rule.name + "?",
					ok : function() {
						ruleService.deleteRule(rule.id, rule.modifiedOn, function(response) {
							$scope.refreshRuleList();
							loggerService.getLogger().debug(response);
						});
					}
				})
				$event.stopPropagation();
			};

			$scope.preview = function(rule) {
				if (!rule.criteriaGroups) {
					rule.criteriaGroups = [];
				}
				if (!rule.directories) {
					rule.directories = [{}];
				}
				sharedService.data.query = {
					"repositoryType" : rule.repositoryType,
					"criteria" : angular.copy(rule.criteriaGroups),
					"directories" : angular.copy(rule.directories)
				};

				$state.go("indexDatabaseQuery", {
					id : rule.id
				});
			};

			$scope.duplicateRule = function(rule) {
				rule.name = rule.name + " - Duplicate";
				rule.enabled = false;
				ruleService.createRule(rule, function(response) {
					$scope.refreshRuleList();
				});
			};

			$scope.openRule = function(rule) {
				loggerService.getLogger().debug("Openning rule " + rule.id);
				$state.go('ruleDetails', {
					id : rule.id
				});
			};
			
			$scope.importRule = function() {
				$scope.jsonFile = [];
				document.getElementById("file-browser").click();
			};
			
			$scope.jsonFileSelected = function(element) {
				loggerService.getLogger().debug('Uploading file.');
				$scope.jsonFile = element.files;
				
				if($scope.jsonFile && $scope.jsonFile.length > 0) {
					ruleService.importRule($scope.jsonFile[0], function(response) {
						$scope.refreshRuleList();
					});
				}
			};
			
			$scope.exportRule = function() {
				loggerService.getLogger().debug("Exporting rule.");
				document.getElementById("exportRuleForm").submit();
			};
			
			$scope.createNewRule = function() {
				loggerService.getLogger().debug("Go to creating new rule");
				$state.go('ruleDetails', {
					id : 'create'
				})
			};

			var ruleWithOpenOption = [];
			$scope.closeAllOpenOption = function() {
				if (ruleWithOpenOption.length > 0) {
					angular.forEach(ruleWithOpenOption, function(rule) {
						if (rule.optionOpen)
							rule.optionOpen = false;
					});
					ruleWithOpenOption = [];
				}
			};
			
			$scope.openOption = function(rule, open, $event) {
				if (angular.isDefined(open)) {
					rule.optionOpen = open;
					if (open) {
						$scope.closeAllOpenOption();
						autoCloseOptionService.close();
						autoCloseOptionService.registerOpen($scope, $scope.closeAllOpenOption);
						ruleWithOpenOption.push(rule);
					} else {
						ruleWithOpenOption = [];
					}
				} else
					return angular.isDefined(rule.optionOpen) ? rule.optionOpen : false;
			};

			$scope.isOptionOpen = function(rule) {
				if (rule.optionOpen) {
					return rule.optionOpen;
				} else {
					return false;
				}
			};
		}])