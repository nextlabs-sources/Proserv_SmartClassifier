mainApp.factory('adHocReportQueryService', 
		['$filter',
		'networkService',
		'loggerService',
		'configService',
		'statusCodeService',
		function($filter, 
				networkService, 
				loggerService, 
				configService,
				statusCodeService) {
			var PAGE_SIZE = configService.configObject['defaultIDBResultPageSize'];
			
			var search = function(query, pageNo, sortingOrder, callback) {
				loggerService.getLogger().debug("Search report.");
				
				var requestData = {
					"range" : query.range,
					"filterGroups" : query.filterGroups,
					"refineGroups" : query.refineGroups,
					"eventStatus" : query.eventStatus,
					"sortFields" : [{"field" : sortingOrder.field, "order" : sortingOrder.order}],
					"pageNo" : pageNo,
					"pageSize" : PAGE_SIZE
				};
				
				networkService.post(configService.getUrl("adHocReportQuery.search"), requestData, function(data) {
					statusCodeService.list("AdHocReportQuery", callback, data);
				});
			};
			
			var loadDocument = function(query, pageNo, sortingOrder, callback) {
				loggerService.getLogger().debug("Load document.");
				
				var requestData = {
					"range" : query.range,
					"filterGroups" : query.filterGroups,
					"refineGroups" : query.refineGroups,
					"eventStatus" : query.eventStatus,
					"sortFields" : [{"field" : sortingOrder.field, "order" : sortingOrder.order}],
					"pageNo" : pageNo,
					"pageSize" : PAGE_SIZE
				};
				
				networkService.post(configService.getUrl("adHocReportQuery.loadDocument"), requestData, function(data) {
					statusCodeService.list("AdHocReportQuery", callback, data);
				});
			};
			
			var loadDocumentEvent = function(query, pageNo, sortingOrder, callback) {
				loggerService.getLogger().debug("Load document event.");
				
				var requestData = {
					"range" : query.range,
					"filterGroups" : query.filterGroups,
					"refineGroups" : query.refineGroups,
					"eventStatus" : query.eventStatus,
					"filePath" : query.filePath,
					"fileName" : query.fileName,
					"sortFields" : sortingOrder,
					"pageNo" : pageNo,
					"pageSize" : PAGE_SIZE
				};
				
				networkService.post(configService.getUrl("adHocReportQuery.loadDocumentEvent"), requestData, function(data) {
					statusCodeService.list("AdHocReportQuery", callback, data);
				});
			};
			
			return {
				search : search,
				loadDocument : loadDocument,
				loadDocumentEvent : loadDocumentEvent
			};
		}
	]
)