angular.module('mainApp').controller(
        'watcherController',
        [
            '$scope',
            'loggerService',
            'scServicesService',
            '$location',
            "$anchorScroll",
            'collectionService',
            'generalService',
            function($scope, loggerService, scServicesService, $location, $anchorScroll, collectionService,
                    generalService) {

              $scope.scrollTo = function(target) {
                loggerService.getLogger().info("Scrolling to " + target);
                $location.hash(target);
                $anchorScroll();
                /* $location.hash('watcherInfo', ''); */
                currentTarget = target;
              };

              $scope.highlightGrammar = function(target) {
                currentTarget = target;
              };

              $scope.changeConfigReloadIntervalUnit = function(unit, interval) {
                $scope.serviceTempObject.configReloadIntervalUnit = unit;
                $scope.onConfigReloadIntervalEdit(interval);
              };

              $scope.onConfigReloadIntervalEdit = function(interval) {
                if($scope.serviceTempObject.configReloadIntervalUnit == "min" && interval < 5) {
                  $scope.serviceTempObject.configReloadIntervalTemp = 5;
                }
                
                if ($scope.serviceTempObject.configReloadIntervalUnit == "hour") {
                  $scope.service.configReloadInterval = $scope.serviceTempObject.configReloadIntervalTemp * 3600;
                } else {
                  $scope.service.configReloadInterval = $scope.serviceTempObject.configReloadIntervalTemp * 60;
                }
              };

              $scope.loadDirectories = function() {
                $scope.serviceTempObject.inDirectories = [];
              };

              var containsDocumentExtractor = function(list, obj) {
                for (var i = 0; i < list.length; i++) {
                  if (list[i].documentExtractor.id === obj.id) { return true; }
                }
                return false;
              };

              $scope.updateDocumentTypeAssociations = function() {

                if (typeof $scope.service.documentTypeAssociations != 'undefined') {

                  loggerService.getLogger().debug("Adding the missing document type associations (if any)!!!");
                  var addedObject = false;
                  for (var i = 0; i < $scope.documentTypes.length; i++) {
                    if (!containsDocumentExtractor($scope.service.documentTypeAssociations, $scope.documentTypes[i])) {
                      var documentExtractorObject = {
                        "documentExtractor": {
                          "extension": "doc",
                          "id": 1512010900000003
                        },
                        "include": false
                      };
                      documentExtractorObject.documentExtractor.extension = $scope.documentTypes[i].extension;
                      documentExtractorObject.documentExtractor.id = $scope.documentTypes[i].id;
                      documentExtractorObject.include = false;
                      $scope.service.documentTypeAssociations.push(documentExtractorObject);
                      addedObject = true;
                      loggerService.getLogger().debug("Added extension =" + $scope.documentTypes[i].extension);
                    }
                  }
                  if (!addedObject) {
                    loggerService.getLogger().debug("No missing document type associations");
                  }
                } else {
                  loggerService.getLogger().debug("No document type association object found. Creating one now!!!");

                  $scope.service.documentTypeAssociations = [];
                  $scope.resetService.documentTypeAssociations = [];
                  for (var i = 0; i < $scope.documentTypes.length; i++) {
                    var documentExtractorObject = {
                      "documentExtractor": {
                        "extension": "doc",
                        "id": 1512010900000003
                      },
                      "include": false
                    };
                    documentExtractorObject.documentExtractor.extension = $scope.documentTypes[i].extension;
                    documentExtractorObject.documentExtractor.id = $scope.documentTypes[i].id;
                    documentExtractorObject.include = false;
                    $scope.service.documentTypeAssociations.push(documentExtractorObject);
                    
                    var resetDocumentExtractorObject = {
                        "documentExtractor": {
                          "extension": "doc",
                          "id": 1512010900000003
                        },
                        "include": false
                      };
                      resetDocumentExtractorObject.documentExtractor.extension = $scope.documentTypes[i].extension;
                      resetDocumentExtractorObject.documentExtractor.id = $scope.documentTypes[i].id;
                      resetDocumentExtractorObject.include = false;
                      $scope.resetService.documentTypeAssociations.push(resetDocumentExtractorObject);
                  }
                }
              };

              $scope.checkExFolder = function(inFolder, exFolder) {
                if (!exFolder.path || exFolder.path.length === 0) {
                  exFolder.path = inFolder.path;
                }

                if (!inFolder.path) { return; }

                if (!generalService.isSubFolder(exFolder.path, inFolder.path)) {
                  exFolder.path = inFolder.path;
                }
              };

              $scope.addExcludeFolder = function(inFolder) {
                if (!inFolder.excludeRepositoryFolders) {
                  inFolder.excludeRepositoryFolders = [];
                }
                inFolder.excludeRepositoryFolders.push({
                  path: inFolder.path
                })
              };

              $scope.removeExcludeFolder = function(index, inFolder) {
                inFolder.excludeRepositoryFolders.splice(index, 1);
              };

              $scope.removeInFolder = function(index) {
                $scope.service.repositoryFolders.splice(index, 1)
              };

              $scope.addIncludeFolder = function() {
                if (!$scope.service.repositoryFolders) {
                  $scope.service.repositoryFolders = [];
                }
                $scope.service.repositoryFolders.push({
                  "path": "",
                  "repositoryType": "SHARED FOLDER",
                  "sourceAuthentication": {}
                });
                loggerService.getLogger().info(JSON.stringify($scope.service.repositoryFolders));
              };

              $scope.updateResetObject = function() {
                $scope.$parent.setResetServiceTempObject(angular.copy($scope.serviceTempObject));
                loggerService.getLogger().debug(
                        "Updating reset object = " + JSON.stringify($scope.$parent.resetServiceTempObject));
              };

              $scope.changeRepositoryType = function(index, repositoryType) {
                loggerService.getLogger().info($scope.service.repositoryFolders[index]);
                $scope.service.repositoryFolders[index].repositoryType = repositoryType;
                loggerService.getLogger().info($scope.service.repositoryFolders[index]);
              };

              $scope.changeCredentials = function(index, credential) {
                $scope.service.repositoryFolders[index].sourceAuthentication = credential;
                loggerService.getLogger().info(JSON.stringify($scope.service.repositoryFolders[index]));
              };

              $scope.loadDocumentTypes = function() {
                collectionService.getCollection("DocumentType", function(response) {
                  $scope.documentTypes = response.data;
                  $scope.totalDocumentTypes = response.totalNoOfRecords;
                  $scope.updateDocumentTypeAssociations();
                });

              };

              $scope.loadCredentialList = function() {
                collectionService.getCollection("RepositoryCredentials", function(response) {
                  $scope.credentialList = response.data;
                  $scope.totalCredentials = response.totalNoOfRecords;
                });

              };

              $scope.loadRepositoryTypes = function() {
                $scope.repositoryTypes = [];
                collectionService.getCollection("RepositoryType", function(response) {
                  $scope.repositoryTypes = response.data;
                  loggerService.getLogger().debug($scope.repositoryTypes);
                });
              };

              /* Functions end and statements begin */

              loggerService.getLogger().info("Watcher id is " + $scope.serviceID);
              $scope.loadDocumentTypes();
              $scope.loadRepositoryTypes();
              $scope.loadCredentialList();

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

              // scroll to functionality
              var currentTarget = "watcherInfo";

              $scope.updateResetObject();
            }])