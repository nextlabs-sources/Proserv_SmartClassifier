angular.module('mainApp').controller(
		'extractorController',
		[
				'$scope',
				'loggerService',
				'collectionService',
				'scServicesService',
				'$location',
				"$anchorScroll",
				'$filter',
				function($scope, loggerService, collectionService, scServicesService, $location, $anchorScroll, $filter) {

					loggerService.getLogger().info("Extractor id is " + $scope.serviceID);

					if (!$scope.service.documentSizeLimits) {
						$scope.service.documentSizeLimits = [];
					}

					// initialize execution window sets
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

					var currentTarget = "extractorInfo";
					$scope.scrollTo = function(target) {
						loggerService.getLogger().info("Scrolling to " + target);
						$location.hash(target);
						$anchorScroll();
						currentTarget = target;
					}

					$scope.highlightGrammar = function(target) {
						currentTarget = target;
					}

					$scope.mapDocumentTypeAssocations = new Object();

					$scope.selectedExecutionWindowName = $filter('translate')("component.editor.dropdown.heading");
					$scope.loadHeapMemoryList = function() {
						$scope.heapMemoryList = [ 512, 1024, 1536, 2048, 3072, 4096 ];
						loggerService.getLogger().debug($scope.heapMemoryList);
					}

					$scope.loadHeapMemoryList();

					$scope.changeHeapMemory = function(heapMemory) {
						$scope.service.minimumHeapMemory = heapMemory;
					}

					$scope.addToExecutionWindowList = function(selectedExecutionWindow) {
						// add to the array

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

					$scope.loadDocumentTypes = function() {
						$scope.documentTypes = [];
						$scope.totalDocumentTypes = 0;
						collectionService.getCollection("DocumentType", function(response) {
							$scope.documentTypes = response.data;
							$scope.totalDocumentTypes = response.totalNoOfRecords;
							$scope.updateDocumentSizeLimitsArray()

						});

					};
					$scope.loadDocumentTypes();

					$scope.updateDocumentSizeLimitsArray = function() {

						if (typeof $scope.service.documentSizeLimits != 'undefined') {
							for (var i = 0; i < $scope.documentTypes.length; i++) {
								if (!containsDocumentExtractor($scope.service.documentSizeLimits, $scope.documentTypes[i])) {
									var documentSizeObject = {
										"documentExtractor" : {
											"extension" : "extension",
											"id" : 0
										},
										"maxFileSize" : 0
									};
									documentSizeObject.documentExtractor.extension = $scope.documentTypes[i].extension;
									documentSizeObject.documentExtractor.id = $scope.documentTypes[i].id;
									documentSizeObject.maxFileSize = 0;
									$scope.service.documentSizeLimits.push(documentSizeObject);
								}
							}

						} else {
							$scope.service.documentSizeLimits = [];
							for (var i = 0; i < $scope.documentTypes.length; i++) {
								var documentSizeObject = {
									"documentExtractor" : {
										"extension" : "extension",
										"id" : 0
									},
									"maxFileSize" : 0
								};
								documentSizeObject.documentExtractor.extension = $scope.documentTypes[i].extension;
								documentSizeObject.documentExtractor.id = $scope.documentTypes[i].id;
								documentSizeObject.maxFileSize = 0;
								$scope.service.documentSizeLimits.push(documentSizeObject);
							}

						}
						$scope.resetService = angular.copy($scope.service)
						loggerService.getLogger().debug(
								"The service.documentSizeLimits now become = " + JSON.stringify($scope.service.documentSizeLimits));
					}

					var containsDocumentExtractor = function(list, obj) {
						for (var i = 0; i < list.length; i++) {
							if (list[i].documentExtractor.id === obj.id) {
								return true;
							}
						}
						return false;
					}

					$scope.updateResetObject = function() {
						$scope.$parent.setResetServiceTempObject(angular.copy($scope.serviceTempObject));
						loggerService.getLogger().debug("Updating reset object " + JSON.stringify($scope.$parent.resetServiceTempObject));
					}

					$scope.updateResetObject();

				} ])