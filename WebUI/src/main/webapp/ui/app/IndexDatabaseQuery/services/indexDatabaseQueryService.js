mainApp.factory('indexDatabaseQueryService', ['networkService', 'loggerService', 'configService', '$filter', 'statusCodeService', function (networkService, loggerService, configService, $filter, statusCodeService) {

    var PAGE_SIZE = configService.configObject['defaultIDBResultPageSize'];

    var executeQuery = function (query, pageNo, sortFields, callback) {
        loggerService.getLogger().info("Executing index database query");

        var requestData = {
            'repositoryType': query.repositoryType,
            'criteria': query.criteria,
            'directories': query.directories,
            'sortFields': sortFields,
            'pageNo': pageNo,
            'pageSize': PAGE_SIZE
        };

        loggerService.getLogger().debug("The request date is = " + JSON.stringify(requestData));

        networkService.post(configService.getUrl("solr.query"), requestData, function (data) {
            statusCodeService.list("solr query", callback, data);
        });

        // statusCodeService.list("sol query", callback,
        // executeQueryOffline());
    };

    var executeQueryOffline = function () {
        return {
            "data": [{
                "id": "//SCDEV02W12R2/MySharedFolder/Classified/confidential classified.doc",
                "directory": "//SCDEV02W12R2/MySharedFolder/Classified/",
                "documentName": "confidential classified.doc",
                "author": "Micheal Dell",
                "createdOn": 1458017853860,
                "modifiedOn": 1458017853860
            }, {
                "id": "//SCDEV02W12R2/MySharedFolder/Classified/confidential classified.docx",
                "directory": "//SCDEV02W12R2/MySharedFolder/Classified/",
                "documentName": "confidential classified.docx",
                "author": "Micheal Dell",
                "createdOn": 1458017853860,
                "modifiedOn": 1458017853860
            }, {
                "id": "//SCDEV02W12R2/MySharedFolder/confidential classified.doc",
                "directory": "//SCDEV02W12R2/MySharedFolder/",
                "documentName": "confidential classified.doc",
                "author": "Jimmy Choo",
                "createdOn": 1458017853860,
                "modifiedOn": 1458017853860
            }, {
                "id": "//SCDEV01W12R2/MySharedFolder/Four.msg",
                "directory": "//SCDEV01W12R2/MySharedFolder/",
                "documentName": "Four.msg",
                "author": "Jimmy Choo",
                "createdOn": 1458017853860,
                "modifiedOn": 1458017853860
            }, {
                "id": "//SCDEV01W12R2/MySharedFolder/Day 01-NextLabs IRM _030612.pptx",
                "directory": "//SCDEV01W12R2/MySharedFolder/",
                "documentName": "Day 01-NextLabs IRM _030612.pptx",
                "author": "Jimmy Choo",
                "createdOn": 1458017853860,
                "modifiedOn": 1458017853860
            }],
            "pageNo": 1,
            "pageSize": 5,
            "totalNoOfRecords": 6,
            "statusCode": "1004",
            "message": "Data loaded successfully."
        }
    };

    var getSortFields = function (callback) {
        // temporarily get sort field of rule
        networkService.get(configService.getUrl("solr.sortFields"), function (data) {
            statusCodeService.list("idq sort fields", callback, data);
        });
    };

    return {
        executeQuery: executeQuery,
        getSortFields: getSortFields
    }

}]);