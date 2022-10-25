mainApp.factory('scheduledReportService', 
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
			
			var getScheduledReportList = function(pageNo, callback) {
				loggerService.getLogger().info("Get scheduled report list of type " + " with page number " + pageNo + " and page size " + PAGE_SIZE);
				var requestData = {
					'pageNo' : pageNo,
					'pageSize' : PAGE_SIZE
				};
				
				networkService.post(configService.getUrl("scheduledReport.list"), requestData, function(data) {
					statusCodeService.list("ScheduledReports", callback, data);
				});
			};
			
			var createScheduledReport = function(scheduledReport, callback) {
				loggerService.getLogger().info("Creating scheduled report with details " + JSON.stringify(scheduledReport));
				var requestData = scheduledReport;
				
				networkService.post(configService.getUrl("scheduledReport.add"), requestData, function(data) {
					statusCodeService.create("ScheduledReports", callback, data);
				});
			};
			
			var getScheduledReport = function(scheduledReportID, callback) {
				loggerService.getLogger().info("Get scheduled report with ID " + scheduledReportID);
				loggerService.getLogger().debug(configService.getUrl("scheduledReport.get") + scheduledReportID);
				
				networkService.get(configService.getUrl("scheduledReport.get") + scheduledReportID, function(data) {
					statusCodeService.get("ScheduledReport", callback, data);
				});
			};
			
			var modifyScheduledReport = function(scheduledReport, callback) {
				loggerService.getLogger().info("Modifying scheduled report " + scheduledReport.id);
				var requestData = scheduledReport;
				
				networkService.put(configService.getUrl("scheduledReport.modify"), requestData, function(data) {
					statusCodeService.modify("ScheduledReport", callback, data);
				});
			};
			
			var deleteScheduledReport = function(id, lastModifiedDate, callback) {
				loggerService.getLogger().info("Deleting scheduled report " + id);
				
				networkService.del(configService.getUrl("scheduledReport.delete") + id + "/" + lastModifiedDate, function(data) {
					statusCodeService.del("ScheduledReport", callback, data);
				});
			};
			
			return {
				getScheduledReportList : getScheduledReportList,
				createScheduledReport : createScheduledReport,
				getScheduledReport : getScheduledReport,
				modifyScheduledReport : modifyScheduledReport,
				deleteScheduledReport : deleteScheduledReport
			};
		}
	]
)