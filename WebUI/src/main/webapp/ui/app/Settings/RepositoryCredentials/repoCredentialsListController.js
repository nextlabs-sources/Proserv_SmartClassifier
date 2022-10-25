/**
 * Created by @pkalra
 */
mainApp.controller('repoCredentialsListController', ['$scope', '$state', 'loggerService', '$stateParams', '$filter', 'repoCredentialsService', 'configService', 'autoCloseOptionService','sharedService','dialogService',
    function($scope, $state, loggerService, $stateParams, $filter, repoCredentialsService, configService, autoCloseOptionService, sharedService, dialogService) {

  var PAGE_SIZE = configService.configObject['defaultCredentialsPageSize'];
  
      $scope.refreshList = function() {
        $scope.pageNo = 1;
        $scope.credList = [];
        $scope.totalCreds = 0;
        $scope.sortFields = [];
        repoCredentialsService.getRepoCredentialList($scope.sortFields, $scope.pageNo, function(response) {
          $scope.credList = response.data;
          $scope.totalCreds = response.totalNoOfRecords;
          $scope.totalPages = ($scope.totalCreds / PAGE_SIZE == 0) ? $scope.totalCreds / PAGE_SIZE : $scope.totalCreds / PAGE_SIZE + 1;
          loggerService.getLogger().debug(response);
        });
      }

      $scope.refreshList();
      
      $scope.loadMore = function() {
        $scope.credentialPageNo++;
        loggerService.getLogger().info("Page number now becomes " + $scope.credentialPageNo);
        repoCredentialsService.getRepoCredentialList($scope.sortFields, $scope.pageNo, function(response) {
          $scope.credList = $scope.credList.concat(response.data);
          $scope.total = response.totalNoOfRecords;
          $scope.totalPages = ($scope.total % PAGE_SIZE == 0) ? $scope.total / PAGE_SIZE : $scope.total / PAGE_SIZE + 1;
          loggerService.getLogger().debug(response);
        })
      };
      
      $scope.createNewCredential = function() {
        loggerService.getLogger().info("Go to -> Create New Credentials");
        $state.go('RepositoryCredentialsDetails', {
          id : 'create'
        })
      };
      
      $scope.openCredential = function(credentials) {
        loggerService.getLogger().info("Opening Credential Set, ID = " + credentials.id);
        sharedService.data.credentials = credentials;
        $state.go('RepositoryCredentialsDetails', {
          id : credentials.id
        })};
      
        $scope.deleteCredential = function(credential, $event) {
          loggerService.getLogger().info("Deleting credential set, ID = " + credential.id);
          dialogService.confirm({
            msg : $filter('translate')('repository.credential.del.confirm') + ": " + credential.name + "?",
            ok : function() {
              repoCredentialsService.deleteCredentials(credential.id, credential.modifiedOn, function(response) {
                $scope.refreshList();
                loggerService.getLogger().debug(response);
              });
            }
          })
          $event.stopPropagation();
        };
        
      var credentialWithOpenOption = [];
      
      $scope.closeAllOpenOption = function() {
        if (credentialWithOpenOption.length > 0) {
          angular.forEach(credentialWithOpenOption, function(credential) {
            loggerService.getLogger().debug("Credential = " + JSON.stringify(credential));
            if (credential.optionOpen)
              credential.optionOpen = false;
          });
          credentialWithOpenOption = [];
        } 
      };
      
      $scope.openOption = function(credential, open, $event) {
        if (angular.isDefined(open)) {
          credential.optionOpen = open;
          if (open) {
            $scope.closeAllOpenOption();
            autoCloseOptionService.close();
            autoCloseOptionService.registerOpen($scope, $scope.closeAllOpenOption);
            credentialWithOpenOption.push(credential);
          } else {
            credentialWithOpenOption = [];
          }
        } else {
          return angular.isDefined(credential.optionOpen) ? credential.optionOpen : false;
        } 
      };

      $scope.isOptionOpen = function(credential) {
        if (credential.optionOpen) {
          return credential.optionOpen;
        } else {
          return false;
        }
      };
      
    }]);
