mainApp.factory('adHocReportService', 
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
			
			var getAdHocReportRow = function(pageNo, adHocReport, callback) {
				
			};
			
			var getAdHocReportList = function(pageNo, callback) {
				loggerService.getLogger().debug("Get ad-hoc report list of type with page number " + pageNo + " and page size " + PAGE_SIZE);
				var requestData = {
					'pageNo' : pageNo,
					'pageSize' : PAGE_SIZE
				};
				
				networkService.post(configService.getUrl("adHocReport.list"), requestData, function(data) {
					statusCodeService.list("AdHocReport", callback, data);
				});
			};
			
			var createAdHocReport = function(adHocReport, callback) {
				loggerService.getLogger().debug("Creating ad-hoc report with details " + JSON.stringify(adHocReport));
				var requestData = adHocReport;
				
				networkService.post(configService.getUrl("adHocReport.add"), requestData, function(data) {
					statusCodeService.create("AdHocReport", callback, data);
				});
			};
			
			var getAdHocReport = function(adHocReportID, callback) {
				loggerService.getLogger().debug("Get ad-hoc report with ID " + adHocReportID);
				
				networkService.get(configService.getUrl("adHocReport.get") + adHocReportID, function(data) {
					statusCodeService.get("AdHocReport", callback, data);
				});
			};
			
			var modifyAdHocReport = function(adHocReport, callback) {
				loggerService.getLogger().debug("Modifying ad-hoc report " + adHocReport.id);
				var requestData = adHocReport;
				
				networkService.put(configService.getUrl("adHocReport.modify"), requestData, function(data) {
					statusCodeService.modify("AdHocReport", callback, data);
				});
			};

			var deleteAdHocReport = function(id, lastModifiedDate, callback) {
				loggerService.getLogger().debug("Deleting ad-hoc report " + id);
				
				networkService.del(configService.getUrl("adHocReport.delete") + id + "/" + lastModifiedDate, function(data) {
					statusCodeService.del("AdHocReport", callback, data);
				});
			};
			
			return {
				getAdHocReportRow : getAdHocReportRow,
				getAdHocReportList : getAdHocReportList,
				createAdHocReport : createAdHocReport,
				getAdHocReport : getAdHocReport,
				modifyAdHocReport : modifyAdHocReport,
				deleteAdHocReport : deleteAdHocReport
			};
		}
	]
)