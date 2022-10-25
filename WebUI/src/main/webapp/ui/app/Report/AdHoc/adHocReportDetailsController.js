mainApp.controller('adHocReportDetailsController', 
		['$scope', 
		 '$state', 
		 '$location', 
		 '$stateParams', 
		 '$filter', 
		 '$anchorScroll',
		 'loggerService', 
		 'dialogService', 
		 'configService',
		 'collectionService',
		 'adHocReportService', 
		 'adHocReportQueryService', 
		 function ($scope,
				   $state,
				   $location,
				   $stateParams,
				   $filter,
				   $anchorScroll,
				   loggerService,
				   dialogService,
				   configService,
				   collectionService,
				   adHocReportService,
				   adHocReportQueryService) {
			var PAGE_SIZE = configService.configObject['defaultIDBResultPageSize'];
			$scope.longDatetimeFormat = configService.configObject['long.datetime.format'];
			$scope.shortDatetimeFormat = configService.configObject['short.datetime.format'];
			$scope.isSearched = false;
			$scope.isCustomRange = false;
			$scope.showActions = false;
			$scope.showFilters = true;
			$scope.rangeList = [
				{"option" : "Any", "unit" : "DAY", "value" : 0, "from" : 0, "to" : 0, "display" : "adHocReport.editor.filter.range.any.date.label"},
				{"option" : "Predefined", "unit" : "DAY", "value" : 6, "from" : 0, "to" : 0, "display" : "adHocReport.editor.filter.range.past.7.days.label"}, 
				{"option" : "Predefined", "unit" : "MONTH", "value" : 1, "from" : 0, "to" : 0, "display" : "adHocReport.editor.filter.range.past.30.days.label"}, 
				{"option" : "Predefined", "unit" : "MONTH", "value" : 3, "from" : 0, "to" : 0, "display" : "adHocReport.editor.filter.range.past.3.months.label"}, 
				{"option" : "Predefined", "unit" : "MONTH", "value" : 6, "from" : 0, "to" : 0, "display" : "adHocReport.editor.filter.range.past.6.months.label"},
				{"option" : "Predefined", "unit" : "YEAR", "value" : 1, "from" : 0, "to" : 0, "display" : "adHocReport.editor.filter.range.past.1.year.label"},
				{"option" : "Custom", "unit" : "NONE", "value" : -1, "from" : 0, "to" : 0, "display" : "adHocReport.editor.filter.range.date.range.label"}
			];
			$scope.fieldNameList = ["Directory", "File Name", "Rule Name", "Action"];
			$scope.operatorList = ["=", "!=", "like", "not like"];
			$scope.sortingOrderList = [
				{"display" : "adHocReport.sort.file.path.asc.label", "field" : "filePath", "order" : "asc"},
				{"display" : "adHocReport.sort.file.path.desc.label", "field" : "filePath", "order" : "desc"},
				{"display" : "adHocReport.sort.file.name.asc.label", "field" : "fileName", "order" : "asc"},
				{"display" : "adHocReport.sort.file.name.desc.label", "field" : "fileName", "order" : "desc"}
			];
			$scope.range = {};
			$scope.sortingOrder = $scope.sortingOrderList[0];
			$scope.reportID = $stateParams.id;
			
			// range variables
			$scope.rangeFromPopUp = {
				opened : false
			};
			
			$scope.rangeToPopUp = {
				opened : false
			};
			
			$scope.rangeFromOpen = function() {
				$scope.rangeFromPopUp.opened = true;
			}

			$scope.rangeToOpen = function() {
				$scope.rangeToPopUp.opened = true;
			}
			
			// load ad hoc report
			$scope.loadAdHocReport = function(adHocReportID) {
				adHocReportService.getAdHocReport(adHocReportID, function(response) {
					$scope.adHocReport = response.data;
					$scope.range = angular.copy($scope.adHocReport.range);
					$scope.resetAdHocReport = angular.copy($scope.adHocReport);
					$scope.resetRange = angular.copy($scope.range);
					
					$scope.isCustomRange = $scope.range.option == 'Custom';
				});
			};
			
			$scope.loadReport = function() {
				$scope.adHocReportRow = [];
				$scope.ruleList = [];
				$scope.actionList = [];
				
				collectionService.getCollection("Rule", function(response) {
					$scope.ruleList = response.data;
				});
				
				collectionService.getCollection("ActionName", function(response) {
					$scope.actionList = response.data;
				});
				
				if($scope.reportID == 'custom') {
					$scope.adHocReport = {
						"type" : "O",
						"name" : null,
						"description" : null,
						"range" : {"option" : "Any", "unit" : "DAY", "value" : 0, "from" : 0, "to" : 0, "display" : "adHocReport.editor.filter.range.any.date.label"},
						"filterGroups" : [],
						"refineGroups" : [],
						"eventStatus" : "A"
					};
					$scope.range = angular.copy($scope.adHocReport.range);
					$scope.resetAdHocReport = angular.copy($scope.adHocReport);
					$scope.resetRange = angular.copy($scope.range);
					$scope.isCustomRange = $scope.range.option == 'Custom';
				} else {
					$scope.loadAdHocReport($scope.reportID);
				}
			};
			
			$scope.loadReport();
			
			$scope.discardAdHocReportChanges = function(form) {
				$scope.adHocReport = angular.copy($scope.resetAdHocReport);
				$scope.range = angular.copy($scope.resetRange);
				$scope.isCustomRange = $scope.adHocReport.range.option == 'Custom';
				
				form.$setPristine();
			};
			
			$scope.saveAdHocReportChanges = function(form) {
				if (form.$invalid) {
					form.$setDirty();
					for ( var field in form) {
						if (field[0] == '$')
							continue;
						form[field].$touched = true;
						form[field].$dirty = true;
					}
					return;
				}
				
				if(!$scope.adHocReport.name) {
					dialogService.input({
						title : $filter('translate')('adHocReport.save.report.title'),
						templateUrl : "ui/app/Report/AdHoc/partials/savereportdialog.html",
						ok : function(param) {
							$scope.adHocReport.name = param.name;
							$scope.adHocReport.description = param.description;
							$scope.saveAdHocReport();
						}
					});
				} else {
					$scope.saveAdHocReport();
				}
				
				$scope.showActions = false;
				form.$setPristine();
			};
			
			$scope.saveAdHocReportChangesAs = function(form) {
				if (form.$invalid) {
					form.$setDirty();
					for ( var field in form) {
						if (field[0] == '$')
							continue;
						form[field].$touched = true;
						form[field].$dirty = true;
					}
					return;
				}
				
				dialogService.input({
					title : $filter('translate')('adHocReport.save.report.as.title'),
					templateUrl : "ui/app/Report/AdHoc/partials/savereportdialog.html",
					ok : function(param) {
						$scope.adHocReport.id = null;
						$scope.adHocReport.name = param.name;
						$scope.adHocReport.description = param.description;
						$scope.saveAdHocReportAs();
					}
				});
				
				$scope.showActions = false;
				form.$setPristine();
			};
			
			$scope.saveAdHocReport = function() {
				if($scope.reportID == 'custom') {
					adHocReportService.createAdHocReport($scope.adHocReport, function(response) {
						$scope.reportID = response.entityId;
						
						$state.go("adHocReportDetails", {
							id : $scope.reportID
						});
					});
				} else {
					adHocReportService.modifyAdHocReport($scope.adHocReport, function(response) {
						$scope.resetAdHocReport = angular.copy($scope.adHocReport);
						$scope.resetRange = angular.copy($scope.range);
					});
				}
			};
			
			$scope.saveAdHocReportAs = function() {
				adHocReportService.createAdHocReport($scope.adHocReport, function(response) {
					$scope.reportID = response.entityId;
					
					$state.go("adHocReportDetails", {
						id : $scope.reportID
					});
				});
			};
			
			$scope.manageAdHocReport = function() {
				if (!$scope.dataPristine()) {
	                dialogService.confirm({
	                    msg: $filter('translate')('details.back.confirm'),
	                    ok: function () {
	                        $state.go("adHocReports");
	                    }
	                })
	            } else {
	                $state.go("adHocReports");
	            }
			};
			
			$scope.dataPristine = function() {
				if(!angular.equals($scope.adHocReport, $scope.resetAdHocReport))
					return false;
				
				return true;
			};
			
			$scope.updateRange = function(range) {
				if(range == undefined) {
					$scope.adHocReport.range = {"option" : "Any", "unit" : "DAY", "value" : 0, "from" : 0, "to" : 0, "display" : "scheduledReport.editor.filter.range.any.date.label"};
				} else {
					$scope.isCustomRange = range.option == 'Custom';
					
					if($scope.isCustomRange) {
						if(!$scope.range.from || $scope.range.from == 0) {
							$scope.range.from = new Date();
							$scope.range.from.setHours(0, 0, 0, 0);
						}
						
						if(!$scope.range.to || $scope.range.to == 0) {
							$scope.range.to = new Date();
							$scope.range.to.setHours(23, 59, 59, 999);
						}
					}
					
					$scope.adHocReport.range = angular.copy(range);
				}
			};
			
			$scope.updateSortingOrder = function(sortingOrder) {
				$scope.sortingOrder = sortingOrder;
				$scope.refineResult();
			};
			
			$scope.addFilterGroup = function() {
				if(!$scope.adHocReport.filterGroups) {
					$scope.adHocReport.filterGroups = [];
				}
				
				$scope.adHocReport.filterGroups.push({
					"displayOrder" : $scope.adHocReport.filterGroups.length + 1, 
					"operator" : "AND",
					"filters" : [{
						"displayOrder" : 1,
						"fieldName" : 'Directory',
						"operator" : '=',
						"value" : null
					}]
				});
			};
			
			$scope.addFilter = function(filterGroup) {
				if(!filterGroup.filters) {
					filterGroup.filters = [];
				}
				
				filterGroup.filters.push({
					"displayOrder" : filterGroup.filters.length + 1,
					"fieldName" : 'Directory',
					"operator" : '=',
					"value" : null
				});
			};
			
			$scope.removeFilterGroup = function(index) {
				$scope.adHocReport.filterGroups.splice(index, 1);
			};
			
			$scope.removeFilter = function(filterGroup, index, findex) {
	            filterGroup.filters.splice(findex, 1);
	            if (filterGroup.filters.length === 0) {
	            	$scope.adHocReport.filterGroups.splice(index, 1);
	            }
			};
			
			$scope.setFilterFieldName = function(filter, fieldName) {
				filter.fieldName = fieldName;
				filter.value = null;
			};
			
			$scope.setFilterOperator = function(filter, operator) {
				filter.operator = operator;
			};
			
			$scope.setFilterValue = function(filter, value) {
				filter.value = value;
			};
			
			$scope.search = function(form) {
				if (form.$invalid) {
					form.$setDirty();
					for ( var field in form) {
						if (field[0] == '$')
							continue;
						form[field].$touched = true;
						form[field].$dirty = true;
					}
					return;
				}
				
				if($scope.isCustomRange) {
					$scope.adHocReport.range.from = $scope.range.from.getTime();
					$scope.adHocReport.range.to = $scope.range.to.getTime();
				}
				
				angular.forEach($scope.adHocReport.filterGroups, function (group, index) {
	                angular.forEach(group.filters, function (filter, index) {
	                    $scope.checkDirectory(filter);
	                });
	            });
				
				$scope.query = angular.copy($scope.adHocReport);
				loggerService.getLogger().debug("Query: " + JSON.stringify($scope.query));
				
				// Reset query result data
				$scope.pageNumber = 1;
				$scope.totalRecordCount = 0;
				$scope.totalDocumentCount = 0;
				// Reset refine search section
				$scope.isSearched = true;
				$scope.showFilters = false;
				$scope.refinable = false;
				$scope.documentCollapse = {};
				$scope.repositoryTypeFacet = {};
				$scope.clearRepositoryType = false;
				$scope.eventStageFacet = {};
				$scope.clearEventStage = false;
				$scope.ruleNameFacet = {};
				$scope.clearRuleName = false;
				$scope.actionNameFacet = {};
				$scope.clearActionName = false;
				$scope.eventStatusFacet = {};
				$scope.clearEventStatus = false;
				$scope.documentList = {};
				
				adHocReportQueryService.search($scope.query, $scope.pageNumber, $scope.sortingOrder, function(response) {
					if(response.data && response.data.facets) {
						$scope.populateFacets(response.data.facets);
					}
					
					if(response.data && response.data.documents) {
						$scope.documentList = response.data.documents;
						
						if($scope.documentList) {
							for(var i = 0; i < $scope.documentList.length; i++) {
								$scope.documentCollapse[i] = true;
							}
						}
					}
					
					$scope.totalRecordCount = response.totalNoOfRecords;
					$scope.totalDocumentCount = response.totalDocumentRecords;
				});
			};
			
			$scope.populateFacets = function(facets) {
				for(var facet in facets) {
					if("repositoryType" == facet) {
						$scope.repositoryTypeFacet = facets[facet];
						if($scope.repositoryTypeFacet.length > 1) {
							$scope.refinable = true;
						}
					} else if("eventStage" == facet) {
						$scope.eventStageFacet = facets[facet];
						if($scope.eventStageFacet.length > 1) {
							$scope.refinable = true;
						}
					} else if("ruleName" == facet) {
						$scope.ruleNameFacet = facets[facet];
						if($scope.ruleNameFacet.length > 1) {
							$scope.refinable = true;
						}
					} else if("actionName" == facet) {
						$scope.actionNameFacet = facets[facet];
						if($scope.actionNameFacet.length > 1) {
							$scope.refinable = true;
						}
					} else if("eventStatus" == facet) {
						$scope.eventStatusFacet = facets[facet];
						if($scope.eventStatusFacet.length > 1) {
							$scope.refinable = true;
						}
					}
				}
			};
			
			$scope.loadDocument = function() {
				var currentRecordCount = $scope.documentList.length;
				
				$scope.query.filePath = {};
				$scope.query.fileName = {};
				
				adHocReportQueryService.loadDocument($scope.query, $scope.pageNumber + 1, $scope.sortingOrder, function(response) {
					$scope.documentList = $scope.documentList.concat(response.data);
					
					for(var i = (currentRecordCount - 1); i < $scope.documentList.length; i++) {
						$scope.documentCollapse[i] = true;
					}
					
					$scope.pageNumber++;
				});
			};
			
			$scope.expandDocument = function(index, document) {
				// First time load document event
				if(!document.eventList) {
					document.pageNumber = 1;
					$scope.query.filePath = document.filePath;
					$scope.query.fileName = document.fileName;
					
					var sortingOrder = [{"field" : "timestamp", "order" : "desc"}, 
										{"field" : "id", "order" : "desc"}];
					adHocReportQueryService.loadDocumentEvent($scope.query, document.pageNumber, sortingOrder, function(response) {
						document.eventList = response.data;
						document.totalEventCount = response.totalNoOfRecords;
					});
				}
				
				$scope.documentCollapse[index] = false;
			};
			
			$scope.collapseDocument = function(index) {
				$scope.documentCollapse[index] = true;
			};
			
			$scope.loadDocumentEvent = function(document) {
				$scope.query.filePath = document.filePath;
				$scope.query.fileName = document.fileName;
				
				var sortingOrder = [{"field" : "timestamp", "order" : "desc"}, 
									{"field" : "id", "order" : "desc"}];
				adHocReportQueryService.loadDocumentEvent($scope.query, document.pageNumber + 1, sortingOrder, function(response) {
					document.eventList = document.eventList.concat(response.data);
					document.pageNumber++;
				});
			};
			
			$scope.clearFacet = function(facet) {
				angular.forEach(facet, function(f){
					f.$checked = false;
				});
				
				$scope.refineResult();
			};
			
			$scope.refineResult = function() {
				// Reset page number
				$scope.pageNumber = 1;
				$scope.query.refineGroups = [];
				
				// Repository Type
				$scope.clearRepositoryType = false;
				if($scope.repositoryTypeFacet && $scope.repositoryTypeFacet.length > 0) {
					var repositoryTypeFilters = $filter('filter')($scope.repositoryTypeFacet, {
						$checked: true
					}).map(function(facet) {
						return {
							"fieldName" : 'Repository Type',
							"operator" : '=',
							"value" : facet.value
						}
					});
					
					if(repositoryTypeFilters.length > 0) {
						$scope.clearRepositoryType = true;
						$scope.query.refineGroups.push({
							"displayOrder" : $scope.query.refineGroups.length + 1, 
							"operator" : "AND",
							"filters" : repositoryTypeFilters
						});
					}
				}
				
				// Event Stage
				$scope.clearEventStage = false;
				if($scope.eventStageFacet && $scope.eventStageFacet.length > 0) {
					var eventStageFilters = $filter('filter')($scope.eventStageFacet, {
						$checked: true
					}).map(function(facet) {
						return {
							"fieldName" : 'Event Stage',
							"operator" : '=',
							"value" : facet.value
						}
					});
					
					if(eventStageFilters.length > 0) {
						$scope.clearEventStage = true;
						$scope.query.refineGroups.push({
							"displayOrder" : $scope.query.refineGroups.length + 1, 
							"operator" : "AND",
							"filters" : eventStageFilters
						});
					}
				}
				
				// Rule Name
				$scope.clearRuleName = false;
				if($scope.ruleNameFacet && $scope.ruleNameFacet.length > 0) {
					var ruleNameFilters = $filter('filter')($scope.ruleNameFacet, {
						$checked: true
					}).map(function(facet) {
						return {
							"fieldName" : 'Rule Name',
							"operator" : '=',
							"value" : facet.value
						}
					});
					
					if(ruleNameFilters.length > 0) {
						$scope.clearRuleName = true;
						$scope.query.refineGroups.push({
							"displayOrder" : $scope.query.refineGroups.length + 1, 
							"operator" : "AND",
							"filters" : ruleNameFilters
						});
					}
				}
				
				// Action
				$scope.clearActionName = false;
				if($scope.actionNameFacet && $scope.actionNameFacet.length > 0) {
					var actionNameFilters = $filter('filter')($scope.actionNameFacet, {
						$checked: true
					}).map(function(facet) {
						return {
							"fieldName" : 'Action',
							"operator" : '=',
							"value" : facet.value
						}
					});
					
					if(actionNameFilters.length > 0) {
						$scope.clearActionName = true;
						$scope.query.refineGroups.push({
							"displayOrder" : $scope.query.refineGroups.length + 1, 
							"operator" : "AND",
							"filters" : actionNameFilters
						});
					}
				}
				
				// Event Status
				$scope.clearEventStatus = false;
				if($scope.eventStatusFacet && $scope.eventStatusFacet.length > 0) {
					var eventStatusFilters = $filter('filter')($scope.eventStatusFacet, {
						$checked: true
					}).map(function(facet) {
						return {
							"fieldName" : 'Event Status',
							"operator" : '=',
							"value" : facet.value
						}
					});
					
					if(eventStatusFilters.length > 0) {
						$scope.clearEventStatus = true;
						$scope.query.refineGroups.push({
							"displayOrder" : $scope.query.refineGroups.length + 1, 
							"operator" : "AND",
							"filters" : eventStatusFilters
						});
					}
				}
				
				loggerService.getLogger().debug("Query: " + JSON.stringify($scope.query));
				
				$scope.documentList = {};
				$scope.documentCollapse = {};
				
				adHocReportQueryService.search($scope.query, $scope.pageNumber, $scope.sortingOrder, function(response) {
					if(response.data && response.data.documents) {
						$scope.documentList = response.data.documents;
						
						if($scope.documentList) {
							for(var i = 0; i < $scope.documentList.length; i++) {
								$scope.documentCollapse[i] = true;
							}
						}
					}
					
					$scope.totalRecordCount = response.totalNoOfRecords;
					$scope.totalDocumentCount = response.totalDocumentRecords;
				});
			};
			
			$scope.exportReport = function() {
				loggerService.getLogger().debug("Exporting report.");
				var requestData = {
					"range" : $scope.query.range,
					"filterGroups" : $scope.query.filterGroups,
					"eventStatus" : $scope.query.eventStatus,
					"sortFields" : [{"field" : "timestamp", "order" : "asc"}, 
									{"field" : "id", "order" : "asc"}],
					"layout" : "flat",
					"format" : "csv"
				};
				
				var defaultValue = {"exportFormat" : requestData.format, "sortBy" : "timestamp"};
				
				dialogService.input({
					title : $filter('translate')('adHocReport.export.option.title'),
					templateUrl : "ui/app/Report/AdHoc/partials/exportoptiondialog.html",
					initialParam : defaultValue,
					ok : function(param) {
						requestData.format = param.exportFormat;
						
						if("directory" == param.sortBy) {
							requestData.sortFields = [{"field" : "filePath", "order" : "asc"},
													  {"field" : "timestamp", "order" : "asc"}, 
													  {"field" : "id", "order" : "asc"}];
						} else if("fileName" == param.sortBy) {
							requestData.sortFields = [{"field" : "fileName", "order" : "asc"},
													  {"field" : "timestamp", "order" : "asc"}, 
													  {"field" : "id", "order" : "asc"}];
						} else {
							requestData.sortFields = [{"field" : "timestamp", "order" : "asc"}, 
													  {"field" : "id", "order" : "asc"}];
						}
						
						console.log(JSON.stringify(requestData));
						
						document.getElementById("exportQuery").value = JSON.stringify(requestData);
						document.getElementById("exportReportForm").submit();
					}
				});
			};
			
			// scrollTo functionalities
			$scope.scrollTo = function(target) {
				loggerService.getLogger().debug("Scrolling to " + target);
				$location.hash(target);
				$anchorScroll();
				// $location.hash(target, '');
				currentTarget = target;
			};
			
	        $scope.checkDirectory = function (filter) {
	            if(filter.fieldName != 'Directory') {
	            	return
	            }
	        	
	        	if (!filter.value) {
	                return;
	            }
	            
	            if (filter.value.slice(filter.value.length - 2, filter.value.length) != ("\\*")
	                && (filter.value.slice(filter.value.length - 1, filter.value.length) != ("\\") && filter.value.slice(filter.value.length - 1, filter.value.length) != ("/"))
	                && filter.value.slice(filter.value.length - 2, filter.value.length) != ("/*")) {
	                filter.value += "/";
	            }
	        };
		}
	]
)