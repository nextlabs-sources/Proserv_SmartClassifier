mainApp
        .controller(
                'serviceDetailsController',
                [
                    '$scope',
                    '$state',
                    '$location',
                    'loggerService',
                    '$stateParams',
                    '$filter',
                    'sharedService',
                    'scServicesService',
                    'dialogService',
                    'collectionService',
                    'executionWindowService',
                    function($scope, $state, $location, loggerService, $stateParams, $filter, sharedService,
                            scServicesService, dialogService, collectionService, executionWindowService) {
                      $scope.$parent.isDetailsPage = true;
                      $scope.serviceTempObject = {};
                      $scope.resetServiceTempObject = {};
                      $scope.serviceType = $stateParams.type;
                      $scope.serviceID = $stateParams.id;
                      $scope.serviceController = $scope.serviceType + "Controller";
                      $scope.mapExecutionWindowCollection = new Object();
                      $scope.serviceController = $scope.serviceType + "Controller";

                      var mapDayOftheWeek = {
                        "0": "Sun",
                        "1": "Mon",
                        "2": "Tue",
                        "3": "Wed",
                        "4": "Thu",
                        "5": "Fri",
                        "6": "Sat"
                      };

                      loggerService.getLogger().info("Service type is " + $scope.serviceType);
                      loggerService.getLogger().info("Service id is " + $scope.serviceID);

                      // initialize service
                      $scope.reloadService = function() {

                        loggerService.getLogger().info(
                                "Cannot retrieve service from shared data. "
                                        + "Making a request to retrieve the service");

                        scServicesService.getService($scope.serviceType, $scope.serviceID, function(response) {
                          $scope.service = response.data;
                          $scope.resetService = angular.copy($scope.service);

                          if (!$scope.service) {
                            $scope.detailsFound = false;
                          } else {
                            $scope.detailsFound = true;
                          }
                        });
                        /*
                         * } else { $scope.service =
                         * sharedService.data.serviceDetails;
                         * $scope.resetService = angular.copy($scope.service);
                         * $scope.detailsFound = true; }
                         */
                      }

                      $scope.loadRepositoryTypes = function() {
                        $scope.repositoryTypes = [];
                        collectionService.getCollection("RepositoryType", function(response) {
                          $scope.repositoryTypes = response.data;
                          loggerService.getLogger().debug($scope.repositoryTypes);
                        });
                      };

                      $scope.loadDocumentTypes = function() {

                        collectionService.getCollection("DocumentType", function(response) {
                          $scope.documentTypes = response.data;
                          $scope.totalDocumentTypes = response.totalNoOfRecords;
                        });

                      };

                      $scope.loadCredentialList = function() {

                        collectionService.getCollection("RepositoryCredentials", function(response) {
                          $scope.credentialList = response.data;
                          $scope.totalCredentials = response.totalNoOfRecords;
                        });

                      };

                      $scope.reloadService();
                      $scope.loadDocumentTypes();
                      $scope.loadRepositoryTypes();
                      $scope.loadCredentialList();

                      $scope.backToServiceList = function(form) {
                        if (!$scope.dataPristine()) {
                          dialogService.confirm({
                            msg: $filter('translate')('details.back.confirm'),
                            ok: function() {
                              $state.go("scservices", {
                                type: $scope.serviceType
                              });
                            }
                          })
                        } else {
                          $state.go("scservices", {
                            type: $scope.serviceType
                          });
                        }

                      };

                      $scope.dataPristine = function() {
                        if (!angular.equals($scope.service, $scope.resetService)) {
                          loggerService.getLogger().debug("service and resetService not equal");
                          return false;
                        }

                        if (!angular.equals($scope.serviceTempObject, $scope.resetServiceTempObject)) {
                          loggerService.getLogger().debug("serviceTempObject and resetServiceTempObject not equal");
                          return false;
                        }
                        return true;
                      };

                      $scope.getServiceController = function() {
                        return $scope.serviceType + 'Controller';
                      };

                      $scope.scrollTo = function(target) {
                        $location.hash(target);
                        $anchorScroll();
                        $location.hash('resourceInfo', '');
                        currentTarget = target;
                      };

                      $scope.checkEmptyExecutionWindowList = function() {
                        if (!$scope.executionWindowCollection) {
                          return true;
                        } else {
                          if ($scope.service.executionWindowSets
                                  && $scope.service.executionWindowSets.length === $scope.executionWindowCollection.length) { return true; }
                        }
                        return false;
                      };

                      $scope.discardServiceChanges = function(form) {
                        loggerService.getLogger().info("Discarding changes");
                        $scope.service = angular.copy($scope.resetService);
                        $scope.serviceTempObject = angular.copy($scope.resetServiceTempObject);
                        form.$setPristine();
                      };

                      $scope.saveServiceChanges = function(form) {
                        // to be implemented

                        if (form.$invalid) {
                          form.$setDirty();
                          for ( var field in form) {
                            if (field[0] == '$') continue;
                            form[field].$touched = true;
                          }
                          return;
                        }

                        loggerService.getLogger().debug($scope.service.name);

                        scServicesService.modifyService($scope.service, $scope.serviceType, function(response) {
                          $scope.resetService = angular.copy($scope.service);
                          $scope.resetServiceTempObject = angular.copy($scope.serviceTempObject);
                          loggerService.getLogger().info(response.message);
                        });
                        form.$setPristine();
                      };

                      $scope.loadJMSList = function() {
                        $scope.jmsList = [];
                        $scope.totalJMSProfiles = 0;
                        collectionService.getCollection("JMSProfile", function(response) {
                          $scope.jmsList = response.data;
                          $scope.totalJMSProfiles = response.totalNoOfRecords;
                          loggerService.getLogger().debug("JMS List" + $scope.jmsList);
                        });
                      };

                      $scope.loadJMSList();

                      $scope.changeJMSProfile = function(jmsProfile) {
                        $scope.service.JMSProfile = jmsProfile;
                      };

                      $scope.loadExecutionWindowCollection = function() {
                        $scope.executionWindowCollection = [];

                        $scope.totalExecutionWindows = 0;
                        collectionService.getCollection("ExecutionWindowSet", function(response) {
                          $scope.executionWindowCollection = response.data;
                          $scope.totalExecutionWindows = response.totalNoOfRecords;
                        });
                      };

                      $scope.returnScheduleString = function(executionWindow) {
                        return executionWindowService.createScheduleString(executionWindow);
                      };

                      $scope.loadExecutionWindowCollection();

                      $scope.setResetServiceTempObject = function(object) {
                        $scope.resetServiceTempObject = object;
                      };

                    }])